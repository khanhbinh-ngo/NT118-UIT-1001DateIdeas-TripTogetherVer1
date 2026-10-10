package com.example.triptogether.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.R
import com.example.triptogether.databinding.DialogAddActivityBinding
import com.example.triptogether.model.ActivityItem
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.PlanViewModel
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.InputValidation
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class ActivityEditorDialogFragment : DialogFragment() {
    private var binding: DialogAddActivityBinding? = null
    private lateinit var model: PlanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[PlanViewModel::class.java]
        observeAction()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val form = DialogAddActivityBinding.inflate(layoutInflater)
        binding = form
        model = ViewModelProvider(this)[PlanViewModel::class.java]
        val args = requireArguments()
        val editing = args.containsKey("activityKey")
        form.etTime.setText(
            savedInstanceState?.getString("draftTime") ?: args.getString("time", "")
        )
        form.etEmoji.setText(
            savedInstanceState?.getString("draftEmoji") ?: args.getString("emoji", "")
        )
        form.etDescription.setText(
            savedInstanceState?.getString("draftDescription") ?: args.getString("description", "")
        )
        form.btnEnter.setOnClickListener {
            val time = form.etTime.text.toString().trim()
            val description = form.etDescription.text.toString().trim()
            val emoji = form.etEmoji.text.toString().trim()
            if (!InputValidation.validTime(time)) {
                form.etTime.error = getString(R.string.invalid_time)
                return@setOnClickListener
            }
            if (description.isBlank() || emoji.isBlank()) {
                form.etDescription.error = getString(R.string.field_required)
                return@setOnClickListener
            }
            model.save(
                args.getString("createdBy")!!,
                args.getString("tripId")!!,
                args.getInt("dayIndex"),
                args.getString("activityKey"),
                ActivityItem(time.padStart(5, '0'), emoji, description),
            )
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (editing) R.string.edit_activity else R.string.add_activity)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun observeAction() {
        model.action.observe(this) { state ->
            val form = binding ?: return@observe
            val available = state != ActionState.Running
            isCancelable = available
            (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_NEGATIVE)?.isEnabled = available
            listOf(form.btnEnter, form.etTime, form.etEmoji, form.etDescription).forEach {
                it.isEnabled = available
            }
            when (state) {
                ActionState.Done -> {
                    model.acknowledge()
                    parentFragmentManager.setFragmentResult("planChanged", Bundle())
                    dismiss()
                }
                ActionState.Error -> {
                    Toast.makeText(context, R.string.save_failed, Toast.LENGTH_SHORT).show()
                    model.acknowledge()
                }
                else -> Unit
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding?.let {
            outState.putString("draftTime", it.etTime.text.toString())
            outState.putString("draftEmoji", it.etEmoji.text.toString())
            outState.putString("draftDescription", it.etDescription.text.toString())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
