#!/usr/bin/env bash
set -euo pipefail

app_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_dir="$(dirname -- "$app_dir")"
action="${1:-build}"

# Gradle 8.13 and Kotlin 2.0.21 need a compatible runtime, even when
# Android Studio itself ships with a newer Java version.
if [[ -x /usr/libexec/java_home ]]; then
    for version in 21 17; do
        if project_java_home="$(/usr/libexec/java_home -v "$version" 2>/dev/null)"; then
            export JAVA_HOME="$project_java_home"
            break
        fi
    done
fi

case "$action" in
    format) tasks=(:app:spotlessApply) ;;
    review) tasks=(:app:spotlessCheck :app:assembleDebug :app:lintDebug) ;;
    build) tasks=(:app:assembleDebug) ;;
    check) tasks=(:app:spotlessCheck :app:assembleDebug :app:testDebugUnitTest :app:lintDebug) ;;
    run) tasks=(:app:assembleDebug :app:installDebug) ;;
    *) echo "Usage: bash app/run-project.sh [build|review|format|check|run]" >&2; exit 2 ;;
esac

bash "$project_dir/gradlew" -p "$project_dir" "${tasks[@]}" --console=plain

if [[ "$action" == run ]]; then
    sdk_dir="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
    if [[ -z "$sdk_dir" && -f "$project_dir/local.properties" ]]; then
        sdk_dir="$(sed -n 's/^sdk.dir=//p' "$project_dir/local.properties" | tail -n 1)"
    fi
    adb_bin="${sdk_dir:+$sdk_dir/platform-tools/adb}"
    if [[ -z "$adb_bin" || ! -x "$adb_bin" ]]; then adb_bin=adb; fi
    "$adb_bin" shell am start -W -n com.example.triptogether/.WelcomeActivity
fi
