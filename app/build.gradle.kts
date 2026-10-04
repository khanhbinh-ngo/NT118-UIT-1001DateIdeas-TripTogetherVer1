import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
    alias(libs.plugins.spotless)
}

val localProperties =
    Properties().apply {
        val propertiesFile = rootProject.file("local.properties")
        if (propertiesFile.isFile) {
            propertiesFile.inputStream().use { load(it) }
        }
    }
val mapsApiKey =
    providers
        .gradleProperty("MAPS_API_KEY")
        .orElse(providers.environmentVariable("MAPS_API_KEY"))
        .orElse(localProperties.getProperty("MAPS_API_KEY", ""))
        .get()
val geocodingApiKey =
    providers
        .gradleProperty("GEOCODING_API_KEY")
        .orElse(providers.environmentVariable("GEOCODING_API_KEY"))
        .orElse(localProperties.getProperty("GEOCODING_API_KEY", ""))
        .get()

fun escapeBuildConfigValue(value: String) =
    value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")

val driveProperties =
    Properties().apply {
        val configuration = file("drive.local.properties")
        if (configuration.isFile) configuration.inputStream().use { load(it) }
    }

android {
    namespace = "com.example.triptogether"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.triptogether"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "DRIVE_FOLDER_ID",
            "\"${escapeBuildConfigValue(driveProperties.getProperty("DRIVE_FOLDER_ID", ""))}\"",
        )

        manifestPlaceholders["mapsApiKey"] = mapsApiKey
        buildConfigField("String", "MAPS_API_KEY", "\"${escapeBuildConfigValue(mapsApiKey)}\"")

        buildConfigField(
            "String",
            "GEOCODING_API_KEY",
            "\"${escapeBuildConfigValue(geocodingApiKey)}\"",
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.maps)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.kotlinx.coroutines.android)

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation("com.google.android.gms:play-services-auth:22.0.0")

    // Firebase Auth UI (kéo theo firebase-auth)
    implementation(libs.firebase.ui.auth)

    // Image load
    implementation(libs.glide)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktfmt("0.54").kotlinlangStyle().configure { it.setRemoveUnusedImports(true) }
    }
    kotlinGradle {
        target("build.gradle.kts")
        ktfmt("0.54").kotlinlangStyle().configure { it.setRemoveUnusedImports(true) }
    }
}
