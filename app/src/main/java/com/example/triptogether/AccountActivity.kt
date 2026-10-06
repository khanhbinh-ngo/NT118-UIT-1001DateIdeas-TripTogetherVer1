package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.databinding.ActivityAccountBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ProfileViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AccountActivity : PlannerActivity() {
    private lateinit var binding: ActivityAccountBinding

    private lateinit var model: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this)[ProfileViewModel::class.java]
        model.profile.observe(this) { state ->
            if (state is UiState.Content) {
                binding.accountEmail.text = state.data.email
                binding.accountName.text = state.data.name.ifBlank { getString(R.string.traveler) }
                com.example.triptogether.drive.DriveImages.load(
                    binding.accountAvatar,
                    state.data.imageUrl,
                    R.drawable.ic_ui_user,
                    circle = true,
                )
            } else if (state == UiState.Error) toast(R.string.load_error)
        }
        binding.btnAccountProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        binding.btnAccountTrips.setOnClickListener {
            startActivity(Intent(this, MyTripsActivity::class.java))
        }
        binding.btnAccountSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.btnHelpSupport.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }
        binding.btnConnectDrive.setOnClickListener { withDriveAccess { model.load() } }
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.logout_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.logout) { _, _ ->
                    AuthUI.getInstance().signOut(this).addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            toast(R.string.auth_failed)
                            return@addOnCompleteListener
                        }
                        com.example.triptogether.drive.DriveSession.clear()
                        startActivity(
                            Intent(this, WelcomeActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::model.isInitialized) model.load()
    }
}
