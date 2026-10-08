package com.example.triptogether

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.databinding.ActivityAddTripBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.CreateTripViewModel
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.TripFilters
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddTripActivity : PlannerActivity() {
    private lateinit var binding: ActivityAddTripBinding
    private lateinit var model: CreateTripViewModel
    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                runCatching {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                }
                model.image = uri
                binding.ivTripImage.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this)[CreateTripViewModel::class.java]
        if (model.image == null)
            model.image = savedInstanceState?.getString("photo")?.let { Uri.parse(it) }
        model.image?.let { binding.ivTripImage.setImageURI(it) }
        binding.etStartDate.setOnClickListener {
            chooseDate(binding.etStartDate.text.toString()) { binding.etStartDate.setText(it) }
        }
        binding.etEndDate.setOnClickListener {
            chooseDate(binding.etEndDate.text.toString()) { binding.etEndDate.setText(it) }
        }
        binding.btnSelectImage.setOnClickListener {
            withDriveAccess { photoPicker.launch(arrayOf("image/*")) }
        }
        binding.btnSaveTrip.setOnClickListener { save() }
        model.state.observe(this) { state ->
            val saving = state is CreateTripViewModel.SaveState.Saving
            binding.btnSaveTrip.isEnabled = !saving
            binding.btnSelectImage.isEnabled = !saving
            listOf(
                    binding.etTripName,
                    binding.etCountry,
                    binding.etStartDate,
                    binding.etEndDate,
                    binding.etMembers,
                )
                .forEach { it.isEnabled = !saving }
            binding.btnSaveTrip.setText(if (saving) R.string.saving else R.string.create_trip)
            when (state) {
                is CreateTripViewModel.SaveState.Saved -> {
                    Toast.makeText(this, R.string.trip_created, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is CreateTripViewModel.SaveState.Failed ->
                    Toast.makeText(
                            this,
                            getString(state.message, state.detail.orEmpty()),
                            Toast.LENGTH_LONG,
                        )
                        .show()
                else -> Unit
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("photo", model.image?.toString())
        super.onSaveInstanceState(outState)
    }

    private fun chooseDate(value: String, callback: (String) -> Unit) {
        val date = TripFilters.date(value) ?: LocalDate.now()
        DatePickerDialog(
                this,
                { _, year, month, day ->
                    callback(
                        LocalDate.of(year, month + 1, day)
                            .format(DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.US))
                    )
                },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth,
            )
            .show()
    }

    private fun save() {
        val required =
            listOf(binding.etTripName, binding.etCountry, binding.etStartDate, binding.etEndDate)
        var valid = true
        required.forEach { field ->
            if (field.text.toString().isBlank()) {
                field.error = getString(R.string.field_required)
                valid = false
            }
        }
        if (!valid) return
        val start = TripFilters.date(binding.etStartDate.text.toString())
        val end = TripFilters.date(binding.etEndDate.text.toString())
        if (start == null || end == null || end.isBefore(start)) {
            binding.etEndDate.error = getString(R.string.date_order_error)
            return
        }
        val emails =
            binding.etMembers.text
                .toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
        if (emails.any { !Patterns.EMAIL_ADDRESS.matcher(it).matches() }) {
            binding.etMembers.error = getString(R.string.invalid_credentials)
            return
        }
        model.save(
            binding.etTripName.text.toString().trim(),
            binding.etCountry.text.toString().trim(),
            binding.etStartDate.text.toString(),
            binding.etEndDate.text.toString(),
            emails,
        )
    }
}
