package com.example.triptogether

import android.os.Bundle
import android.util.Patterns
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.databinding.ActivityForgotPasswordBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.AuthViewModel
import com.example.triptogether.ui.state.observe

class ForgotPasswordActivity : PlannerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val model = ViewModelProvider(this)[AuthViewModel::class.java]
        if (savedInstanceState == null) binding.resetEmail.setText(intent.getStringExtra("email"))
        binding.btnResetPassword.setOnClickListener {
            val email = binding.resetEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                binding.resetEmail.error = getString(R.string.invalid_email)
            else model.resetPassword(email)
        }
        model.action.observe(this) { state ->
            binding.btnResetPassword.isEnabled = state != ActionState.Running
            if (state == ActionState.Done || state == ActionState.Error) {
                binding.resetStatus.setText(
                    if (state == ActionState.Done) R.string.reset_sent else R.string.reset_failed
                )
                model.acknowledge()
            }
        }
    }
}
