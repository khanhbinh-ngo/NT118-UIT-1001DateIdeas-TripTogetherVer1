package com.example.triptogether.ui

import android.os.Bundle

class AddActivityDialogFragment : ActivityEditorDialogFragment() {
    companion object {
        fun newInstance(tripId: String, createdBy: String, dayIndex: Int) =
            AddActivityDialogFragment().apply {
                arguments =
                    Bundle().apply {
                        putString("tripId", tripId)
                        putString("createdBy", createdBy)
                        putInt("dayIndex", dayIndex)
                    }
            }
    }
}
