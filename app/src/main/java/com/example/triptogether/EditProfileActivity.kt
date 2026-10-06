package com.example.triptogether

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.databinding.ActivityEditProfileBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.ProfileViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe

class EditProfileActivity : PlannerActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var model: ProfileViewModel
    private var loaded = false
    private var savingProfile = false
    private val picker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                runCatching {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                }
                model.image = uri
                binding.ivProfileImage.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this)[ProfileViewModel::class.java]
        loaded = savedInstanceState?.getBoolean("loaded") ?: false
        savingProfile = savedInstanceState?.getBoolean("savingProfile") ?: false
        if (model.image == null)
            model.image = savedInstanceState?.getString("photo")?.let(Uri::parse)
        binding.btnCancelProfile.setOnClickListener { finish() }
        binding.btnChooseImage.setOnClickListener {
            withDriveAccess { picker.launch(arrayOf("image/*")) }
        }
        binding.btnChangePassword.setOnClickListener {
            savingProfile = false
            model.resetPassword()
        }
        binding.btnSaveProfile.setOnClickListener {
            val first = binding.etFirstName.text.toString().trim()
            if (first.isBlank()) binding.etFirstName.error = getString(R.string.field_required)
            else {
                savingProfile = true
                model.save(
                    first,
                    binding.etLastName.text.toString().trim(),
                    binding.etRole.text.toString().trim(),
                )
            }
        }
        model.profile.observe(this) { state ->
            when (state) {
                is UiState.Content -> {
                    binding.profileEmail.setText(state.data.email)
                    binding.btnSaveProfile.isEnabled = model.action.value != ActionState.Running
                    if (!loaded) {
                        binding.etFirstName.setText(state.data.firstName)
                        binding.etLastName.setText(state.data.lastName)
                        binding.etRole.setText(state.data.role)
                        loaded = true
                    }
                    if (model.image == null)
                        com.example.triptogether.drive.DriveImages.load(
                            binding.ivProfileImage,
                            state.data.imageUrl,
                            R.drawable.ic_ui_user,
                            circle = true,
                        )
                    else binding.ivProfileImage.setImageURI(model.image)
                }
                UiState.Error -> toast(R.string.load_error)
                UiState.Loading -> Unit
            }
        }
        model.action.observe(this) { state ->
            val available = state != ActionState.Running
            binding.btnSaveProfile.isEnabled = available && model.profile.value is UiState.Content
            listOf(
                    binding.btnChooseImage,
                    binding.btnChangePassword,
                    binding.etFirstName,
                    binding.etLastName,
                    binding.etRole,
                    binding.btnCancelProfile,
                )
                .forEach { it.isEnabled = available }
            binding.btnSaveProfile.setText(
                if (available) R.string.save_changes else R.string.saving
            )
            when (state) {
                ActionState.Done -> {
                    model.acknowledge()
                    if (savingProfile) finish() else toast(R.string.reset_sent)
                }
                ActionState.Error -> {
                    toast(R.string.save_failed)
                    model.acknowledge()
                }
                else -> Unit
            }
        }
        if (model.profile.value == UiState.Loading) model.load()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("loaded", loaded)
        outState.putBoolean("savingProfile", savingProfile)
        outState.putString("photo", model.image?.toString())
        super.onSaveInstanceState(outState)
    }
}
