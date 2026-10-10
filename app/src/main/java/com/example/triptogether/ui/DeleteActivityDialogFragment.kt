package com.example.triptogether.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.triptogether.R
import com.example.triptogether.ui.state.ActionState
import com.example.triptogether.ui.state.PlanViewModel
import com.example.triptogether.ui.state.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeleteActivityDialogFragment : DialogFragment() {
    private lateinit var model: PlanViewModel

    companion object {
        fun newInstance(tripId: String, createdBy: String, dayIndex: Int, activityKey: String) =
            DeleteActivityDialogFragment().apply {
                arguments =
                    Bundle().apply {
                        putString("tripId", tripId)
                        putString("createdBy", createdBy)
                        putInt("dayIndex", dayIndex)
                        putString("activityKey", activityKey)
                    }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[PlanViewModel::class.java]
        observeAction()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        model = ViewModelProvider(this)[PlanViewModel::class.java]
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_activity)
            .setMessage(R.string.delete_activity_confirm)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete, null)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? AlertDialog ?: return
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val args = requireArguments()
            model.delete(
                args.getString("createdBy")!!,
                args.getString("tripId")!!,
                args.getInt("dayIndex"),
                args.getString("activityKey")!!,
            )
        }
    }

    private fun observeAction() {
        model.action.observe(this) { state ->
            val dialog = dialog as? AlertDialog ?: return@observe
            val available = state != ActionState.Running
            isCancelable = available
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = available
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = available
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
}
