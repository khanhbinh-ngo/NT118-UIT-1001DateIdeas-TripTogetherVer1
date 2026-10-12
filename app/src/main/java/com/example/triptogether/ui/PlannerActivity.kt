package com.example.triptogether.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.triptogether.*
import com.example.triptogether.drive.DriveApi
import com.example.triptogether.drive.DriveSession
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.ActionViewModel
import com.example.triptogether.ui.state.observe
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Shared Android system-bar handling and navigation for the planner screens. */
open class PlannerActivity : AppCompatActivity() {
    private var pendingDriveAction: (() -> Unit)? = null
    private val driveConsent =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                pendingDriveAction = null
                toast(R.string.drive_connection_failed)
            } else
                runCatching {
                        Identity.getAuthorizationClient(this)
                            .getAuthorizationResultFromIntent(result.data)
                            .accessToken
                    }
                    .onSuccess { finishDriveConnection(it) }
                    .onFailure {
                        pendingDriveAction = null
                        toast(R.string.drive_connection_failed)
                    }
        }

    protected fun withDriveAccess(action: () -> Unit) {
        if (runCatching { DriveSession.accessToken() }.isSuccess) {
            action()
            return
        }
        if (pendingDriveAction != null) return
        pendingDriveAction = action
        val request =
            AuthorizationRequest.builder()
                .setRequestedScopes(listOf(Scope("https://www.googleapis.com/auth/drive")))
                .build()
        Identity.getAuthorizationClient(this)
            .authorize(request)
            .addOnSuccessListener { result ->
                if (result.hasResolution())
                    driveConsent.launch(
                        IntentSenderRequest.Builder(checkNotNull(result.pendingIntent)).build()
                    )
                else finishDriveConnection(result.accessToken)
            }
            .addOnFailureListener {
                pendingDriveAction = null
                toast(R.string.drive_connection_failed)
            }
    }

    private fun finishDriveConnection(token: String?) {
        if (token.isNullOrBlank()) {
            pendingDriveAction = null
            toast(R.string.drive_connection_failed)
            return
        }
        DriveSession.connect(token)
        lifecycleScope.launch {
            try {
                DriveApi().verifyFolder()
                val action = pendingDriveAction
                pendingDriveAction = null
                action?.invoke()
                toast(R.string.drive_connected)
            } catch (cancelled: CancellationException) {
                pendingDriveAction = null
                throw cancelled
            } catch (error: Exception) {
                DriveSession.clear()
                pendingDriveAction = null
                toast(
                    if (error is com.example.triptogether.drive.DrivePrivacyException)
                        R.string.drive_restricted_required
                    else R.string.drive_connection_failed
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val mode =
            getSharedPreferences("appearance", MODE_PRIVATE)
                .getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        delegate.localNightMode = mode
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onResume() {
        super.onResume()
        val mode =
            getSharedPreferences("appearance", MODE_PRIVATE)
                .getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (delegate.localNightMode != mode) delegate.localNightMode = mode
        window.decorView.post {
            val dark =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val dark =
                delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES ||
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                        Configuration.UI_MODE_NIGHT_YES
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
        val content = findViewById<ViewGroup>(android.R.id.content)
        val root = content.getChildAt(0) ?: return
        val left = root.paddingLeft
        val right = root.paddingRight
        val top = root.paddingTop
        val bottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(
                left + bars.left,
                top + bars.top,
                right + bars.right,
                bottom + maxOf(bars.bottom, keyboard.bottom),
            )
            insets
        }
        ViewCompat.requestApplyInsets(root)
        val dark =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, root).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
        findViewById<View>(R.id.btnBackScreen)?.setOnClickListener { finish() }
        findViewById<View>(R.id.btnNotifications)?.setOnClickListener {
            if (javaClass != NotificationsActivity::class.java)
                startActivity(Intent(this, NotificationsActivity::class.java))
        }
        setupNavigation()
    }

    private fun setupNavigation() {
        val destinations =
            listOf(
                Triple(R.id.homeIcon, R.id.homeIconContainer, MainActivity::class.java),
                Triple(R.id.heartIcon, R.id.heartIconContainer, FavoriteActivity::class.java),
                Triple(R.id.tripsIcon, R.id.tripsIconContainer, MyTripsActivity::class.java),
                Triple(R.id.userIcon, R.id.userIconContainer, AccountActivity::class.java),
            )
        destinations.forEach { (iconId, containerId, destination) ->
            val selected =
                javaClass == destination ||
                    (destination == AccountActivity::class.java &&
                        javaClass in
                            listOf(
                                EditProfileActivity::class.java,
                                SettingsActivity::class.java,
                                HelpSupportActivity::class.java,
                            ))
            findViewById<ImageView>(iconId)?.apply {
                setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (selected) R.color.on_brand else R.color.nav_icon,
                    )
                )
                alpha = if (selected) 1f else .75f
            }
            val label =
                when (iconId) {
                    R.id.homeIcon -> R.id.homeIconLabel
                    R.id.heartIcon -> R.id.heartIconLabel
                    R.id.tripsIcon -> R.id.tripsIconLabel
                    else -> R.id.userIconLabel
                }
            findViewById<TextView>(label)
                ?.setTextColor(
                    ContextCompat.getColor(
                        this,
                        if (selected) R.color.on_brand else R.color.nav_icon,
                    )
                )
            val action =
                View.OnClickListener {
                    if (javaClass != destination)
                        startActivity(
                            Intent(this, destination).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                        )
                }
            findViewById<View>(containerId)?.apply {
                isSelected = selected
                setOnClickListener(action)
            }
            findViewById<View>(iconId)?.apply {
                isClickable = false
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        }
    }

    protected fun toast(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun observeAction(model: ActionViewModel) {
        model.action.observe(this) { state ->
            when (state) {
                ActionState.Error -> {
                    toast(R.string.save_failed)
                    model.acknowledge()
                }
                ActionState.Done -> model.acknowledge()
                else -> Unit
            }
        }
    }

    protected fun showState(
        loading: Boolean = false,
        message: String? = null,
        retry: (() -> Unit)? = null,
    ) {
        findViewById<View>(R.id.loadingIndicator)?.visibility =
            if (loading) View.VISIBLE else View.GONE
        findViewById<View>(R.id.statePanel)?.visibility =
            if (!loading && message != null) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.stateMessage)?.text = message
        findViewById<View>(R.id.btnRetry)?.apply {
            visibility = if (retry != null) View.VISIBLE else View.GONE
            setOnClickListener { retry?.invoke() }
        }
    }
}
