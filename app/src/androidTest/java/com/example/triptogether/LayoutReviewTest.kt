package com.example.triptogether

import android.content.Intent
import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayoutReviewTest {
    @Test
    fun allRedesignedLayoutsInflateAndSurviveRecreation() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val layouts =
            listOf(
                R.layout.activity_main,
                R.layout.activity_favorites,
                R.layout.activity_add_trip,
                R.layout.activity_trip_summary,
                R.layout.activity_trip_plan,
                R.layout.activity_friends,
                R.layout.activity_edit_friends,
                R.layout.activity_group_chat,
                R.layout.activity_documents_home,
                R.layout.activity_documents,
                R.layout.activity_images_list,
                R.layout.activity_image_preview,
                R.layout.activity_search_results,
                R.layout.activity_login,
                R.layout.activity_welcome,
                R.layout.activity_forgot_password,
                R.layout.activity_account,
                R.layout.activity_edit_profile,
                R.layout.activity_settings,
                R.layout.activity_trip_map,
                R.layout.activity_help_support,
                R.layout.activity_notifications,
            )
        val requested =
            InstrumentationRegistry.getArguments().getString("reviewLayouts")?.split(",")?.toSet()
        layouts
            .filter { requested == null || context.resources.getResourceEntryName(it) in requested }
            .forEach { layout ->
                ActivityScenario.launch<LayoutReviewActivity>(
                        Intent(context, LayoutReviewActivity::class.java)
                            .putExtra("layout", layout)
                            .putExtra("lightTheme", true)
                    )
                    .use { scenario ->
                        scenario.recreate()
                        scenario.onActivity {
                            assertTrue(
                                it.findViewById<android.view.ViewGroup>(android.R.id.content)
                                    .childCount > 0
                            )
                        }
                        instrumentation.waitForIdleSync()
                        val image = instrumentation.uiAutomation.takeScreenshot()
                        val folder =
                            File(context.getExternalFilesDir(null), "ui-review").apply { mkdirs() }
                        File(folder, context.resources.getResourceEntryName(layout) + ".png")
                            .outputStream()
                            .use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
                        exportReviewImage(
                            context,
                            image,
                            context.resources.getResourceEntryName(layout) + ".png",
                        )
                        image.recycle()
                    }
            }
    }

    @Test
    fun darkModeAndLargeTextLayoutsRender() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val preferences =
            context.getSharedPreferences("appearance", android.content.Context.MODE_PRIVATE)
        val previous = preferences.getInt("nightMode", -1)
        try {
            preferences
                .edit()
                .putInt("nightMode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
                .putBoolean("reviewLargeText", true)
                .commit()
            listOf(R.layout.activity_main, R.layout.activity_login, R.layout.activity_settings)
                .forEach { layout ->
                    ActivityScenario.launch<LayoutReviewActivity>(
                            Intent(context, LayoutReviewActivity::class.java)
                                .putExtra("layout", layout)
                                .putExtra("largeText", true)
                        )
                        .use {
                            instrumentation.waitForIdleSync()
                            val image = instrumentation.uiAutomation.takeScreenshot()
                            val folder =
                                File(context.getExternalFilesDir(null), "ui-review").apply {
                                    mkdirs()
                                }
                            File(
                                    folder,
                                    context.resources.getResourceEntryName(layout) +
                                        "_dark_large.png",
                                )
                                .outputStream()
                                .use { output ->
                                    image.compress(Bitmap.CompressFormat.PNG, 100, output)
                                }
                            exportReviewImage(
                                context,
                                image,
                                context.resources.getResourceEntryName(layout) + "_dark_large.png",
                            )
                            image.recycle()
                        }
                }
        } finally {
            preferences.edit().putInt("nightMode", previous).remove("reviewLargeText").commit()
        }
    }

    @Test
    fun authFormsValidateAndRegistrationSurvivesRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ActivityScenario.launch<LoginActivity>(
                Intent(context, LoginActivity::class.java).putExtra("reviewAuth", true)
            )
            .use { scenario ->
                scenario.onActivity { activity ->
                    assertTrue(
                        activity
                            .findViewById<android.view.View>(R.id.registerNameGroup)
                            .visibility == android.view.View.GONE
                    )
                    activity.findViewById<android.view.View>(R.id.btnSignIn).performClick()
                    assertTrue(
                        activity.findViewById<android.view.View>(R.id.authError).visibility ==
                            android.view.View.VISIBLE
                    )
                }
            }
        ActivityScenario.launch<RegisterActivity>(
                Intent(context, RegisterActivity::class.java).putExtra("reviewAuth", true)
            )
            .use { scenario ->
                scenario.onActivity { activity ->
                    activity
                        .findViewById<android.widget.EditText>(R.id.authName)
                        .setText("Traveller")
                    activity
                        .findViewById<android.widget.EditText>(R.id.authEmail)
                        .setText("traveller@example.com")
                    activity
                        .findViewById<android.widget.EditText>(R.id.authPassword)
                        .setText("password123")
                    activity
                        .findViewById<android.widget.EditText>(R.id.authConfirm)
                        .setText("different")
                    activity.findViewById<android.view.View>(R.id.btnSignIn).performClick()
                    org.junit.Assert.assertEquals(
                        activity.getString(R.string.password_mismatch),
                        activity
                            .findViewById<android.widget.TextView>(R.id.authError)
                            .text
                            .toString(),
                    )
                }
                scenario.recreate()
                scenario.onActivity { activity ->
                    assertTrue(
                        activity
                            .findViewById<android.view.View>(R.id.registerNameGroup)
                            .visibility == android.view.View.VISIBLE
                    )
                    assertTrue(
                        activity
                            .findViewById<android.view.View>(R.id.btnForgotPassword)
                            .visibility == android.view.View.GONE
                    )
                    org.junit.Assert.assertEquals(
                        "Traveller",
                        activity
                            .findViewById<android.widget.EditText>(R.id.authName)
                            .text
                            .toString(),
                    )
                }
            }
    }

    @Test
    fun welcomeOpensSeparateFormsAndBackReturnsToChoice() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val loginMonitor = instrumentation.addMonitor(LoginActivity::class.java.name, null, false)
        val registerMonitor =
            instrumentation.addMonitor(RegisterActivity::class.java.name, null, false)
        try {
            ActivityScenario.launch<WelcomeActivity>(
                    Intent(context, WelcomeActivity::class.java).putExtra("reviewAuth", true)
                )
                .use { scenario ->
                    scenario.onActivity {
                        it.findViewById<android.view.View>(R.id.btnChooseLogin).performClick()
                    }
                    val login =
                        instrumentation.waitForMonitorWithTimeout(loginMonitor, 5000)
                            as? LoginActivity
                    org.junit.Assert.assertNotNull("Login choice must open LoginActivity", login)
                    instrumentation.runOnMainSync {
                        org.junit.Assert.assertEquals(
                            login!!.getString(R.string.login_title),
                            login
                                .findViewById<android.widget.TextView>(R.id.authTitle)
                                .text
                                .toString(),
                        )
                        login.onBackPressedDispatcher.onBackPressed()
                    }
                    instrumentation.waitForIdleSync()
                    scenario.onActivity {
                        it.findViewById<android.view.View>(R.id.btnChooseRegister).performClick()
                    }
                    val register =
                        instrumentation.waitForMonitorWithTimeout(registerMonitor, 5000)
                            as? RegisterActivity
                    org.junit.Assert.assertNotNull(
                        "Register choice must open RegisterActivity",
                        register,
                    )
                    instrumentation.runOnMainSync {
                        org.junit.Assert.assertEquals(
                            register!!.getString(R.string.register_title),
                            register
                                .findViewById<android.widget.TextView>(R.id.authTitle)
                                .text
                                .toString(),
                        )
                        register.findViewById<android.view.View>(R.id.btnBackScreen).performClick()
                    }
                    instrumentation.waitForIdleSync()
                    scenario.onActivity {
                        assertTrue(it.findViewById<android.view.View>(R.id.btnChooseLogin).isShown)
                    }
                }
        } finally {
            instrumentation.removeMonitor(loginMonitor)
            instrumentation.removeMonitor(registerMonitor)
        }
    }

    @Test
    fun helpFaqCanExpandAndRecreate() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        ActivityScenario.launch(HelpSupportActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val content = activity.findViewById<android.widget.LinearLayout>(R.id.faqContent)
                content.getChildAt(1).performClick()
                assertTrue(
                    content.findViewWithTag<android.view.View>("faq0").visibility ==
                        android.view.View.VISIBLE
                )
            }
            scenario.recreate()
            scenario.onActivity { activity ->
                assertTrue(
                    activity
                        .findViewById<android.widget.LinearLayout>(R.id.faqContent)
                        .findViewWithTag<android.view.View>("faq0")
                        .visibility == android.view.View.VISIBLE
                )
            }
            instrumentation.waitForIdleSync()
            val image = instrumentation.uiAutomation.takeScreenshot()
            exportReviewImage(context, image, "activity_help_support_live.png")
            image.recycle()
        }
    }

    private fun exportReviewImage(context: android.content.Context, bitmap: Bitmap, name: String) {
        if (android.os.Build.VERSION.SDK_INT < 29) return
        val values =
            android.content.ContentValues().apply {
                put(android.provider.MediaStore.Downloads.DISPLAY_NAME, name)
                put(android.provider.MediaStore.Downloads.MIME_TYPE, "image/png")
                put(
                    android.provider.MediaStore.Downloads.RELATIVE_PATH,
                    "Download/triptogether-ui-review",
                )
                put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
            }
        val resolver = context.contentResolver
        val uri =
            checkNotNull(
                resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            )
        resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        values.clear()
        values.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }
}
