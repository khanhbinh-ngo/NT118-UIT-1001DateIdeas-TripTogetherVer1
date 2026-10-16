package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.example.triptogether.data.AuthRepository
import com.example.triptogether.databinding.ActivityLoginBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.AuthViewModel
import com.example.triptogether.ui.state.observe
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

abstract class AuthFormActivity : PlannerActivity() {
    protected abstract val registering: Boolean
    private lateinit var binding: ActivityLoginBinding

    private val auth = AuthRepository()
    private lateinit var model: AuthViewModel
    private val reviewOnly
        get() = BuildConfig.DEBUG && intent.getBooleanExtra("reviewAuth", false)

    private var busy = false
    private val googleLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            setBusy(false)
            if (result.resultCode == RESULT_OK) model.completeSignIn()
            else if (result.idpResponse != null) error(getString(R.string.auth_failed))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = androidx.lifecycle.ViewModelProvider(this)[AuthViewModel::class.java]
        model.action.observe(this) { state ->
            setBusy(state == ActionState.Running)
            if (state == ActionState.Done) {
                model.acknowledge()
                enterApp()
            } else if (state == ActionState.Error) {
                error(getString(model.failureMessage))
                if (auth.currentUser != null) binding.btnSignIn.setText(R.string.retry_profile_sync)
                model.acknowledge()
            }
        }
        renderMode()
        binding.btnToggleRegister.setOnClickListener {
            val destination =
                if (registering) LoginActivity::class.java else RegisterActivity::class.java
            startActivity(Intent(this, destination).putExtra("reviewAuth", reviewOnly))
            finish()
        }
        binding.btnForgotPassword.setOnClickListener {
            startActivity(
                Intent(this, ForgotPasswordActivity::class.java)
                    .putExtra("email", input(R.id.authEmail))
            )
        }
        binding.btnSignIn.setOnClickListener { submit() }
        binding.btnBackScreen.setOnClickListener {
            startActivity(
                Intent(this, WelcomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("reviewAuth", reviewOnly)
                }
            )
            finish()
        }
        binding.btnGoogleSignIn.setOnClickListener {
            if (reviewOnly) return@setOnClickListener
            setBusy(true)
            googleLauncher.launch(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                    .setTheme(R.style.Theme_TripTogether)
                    .build()
            )
        }
        listOf(R.id.authEmail, R.id.authPassword, R.id.authName, R.id.authConfirm).forEach { id ->
            inputField(id).doAfterTextChanged { error(null) }
        }
        if (!reviewOnly && auth.currentUser != null) model.completeSignIn()
    }

    private fun input(id: Int) = inputField(id).text.toString().trim()

    private fun inputField(id: Int): EditText =
        when (id) {
            R.id.authEmail -> binding.authEmail
            R.id.authPassword -> binding.authPassword
            R.id.authName -> binding.authName
            R.id.authConfirm -> binding.authConfirm
            else -> throw IllegalArgumentException("Unknown authentication field")
        }

    private fun renderMode() {
        binding.authTitle.setText(
            if (registering) R.string.register_title else R.string.login_title
        )
        binding.authSubtitle.setText(
            if (registering) R.string.register_hint else R.string.login_hint
        )
        binding.btnSignIn.setText(if (registering) R.string.sign_up else R.string.sign_in)
        binding.btnToggleRegister.setText(
            if (registering) R.string.back_to_login else R.string.create_account
        )
        listOf(binding.registerNameGroup, binding.registerConfirmGroup).forEach {
            it.visibility = if (registering) View.VISIBLE else View.GONE
        }
        binding.btnForgotPassword.visibility = if (registering) View.GONE else View.VISIBLE
        error(null)
    }

    private fun submit() {
        if (busy) return
        if (!reviewOnly && auth.currentUser != null) {
            model.completeSignIn()
            return
        }
        val email = input(R.id.authEmail)
        val password = binding.authPassword.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isBlank()) {
            error(getString(R.string.invalid_credentials))
            return
        }
        if (registering) {
            if (input(R.id.authName).isBlank()) {
                error(getString(R.string.empty_name))
                return
            }
            if (password.length < 6) {
                error(getString(R.string.password_short))
                return
            }
            if (password != binding.authConfirm.text.toString()) {
                error(getString(R.string.password_mismatch))
                return
            }
        }
        if (reviewOnly) return
        model.submit(if (registering) input(R.id.authName) else null, email, password)
    }

    private fun error(message: String?) {
        binding.authError.apply {
            text = message
            visibility = if (message == null) View.GONE else View.VISIBLE
        }
    }

    private fun setBusy(value: Boolean) {
        busy = value
        binding.authProgress.visibility = if (value) View.VISIBLE else View.GONE
        listOf(
                binding.btnBackScreen,
                binding.btnSignIn,
                binding.btnGoogleSignIn,
                binding.btnToggleRegister,
                binding.btnForgotPassword,
                binding.authEmail,
                binding.authPassword,
                binding.authName,
                binding.authConfirm,
            )
            .forEach { it.isEnabled = !value }
    }

    private fun enterApp() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}
