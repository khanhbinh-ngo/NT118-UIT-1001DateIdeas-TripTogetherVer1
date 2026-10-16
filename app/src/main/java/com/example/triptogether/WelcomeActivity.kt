package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import com.example.triptogether.data.AuthRepository
import com.example.triptogether.databinding.ActivityWelcomeBinding
import com.example.triptogether.ui.PlannerActivity

/** Entry point for choosing an authentication flow. Existing sessions still sync their profile. */
class WelcomeActivity : PlannerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reviewOnly = BuildConfig.DEBUG && intent.getBooleanExtra("reviewAuth", false)
        if (!reviewOnly && AuthRepository().currentUser != null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChooseLogin.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java).putExtra("reviewAuth", reviewOnly)
            )
        }
        binding.btnChooseRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java).putExtra("reviewAuth", reviewOnly)
            )
        }
    }
}
