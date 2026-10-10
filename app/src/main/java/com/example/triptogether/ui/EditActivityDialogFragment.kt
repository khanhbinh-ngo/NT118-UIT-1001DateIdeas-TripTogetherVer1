package com.example.triptogether.ui

import android.os.Bundle
import com.example.triptogether.model.ActivityItem

class EditActivityDialogFragment : ActivityEditorDialogFragment() {
    companion object {
        fun newInstance(
            tripId: String,
            createdBy: String,
            dayIndex: Int,
            activityKey: String,
            currentActivity: ActivityItem,
        ) =
            EditActivityDialogFragment().apply {
                arguments =
                    Bundle().apply {
                        putString("tripId", tripId)
                        putString("createdBy", createdBy)
                        putInt("dayIndex", dayIndex)
                        putString("activityKey", activityKey)
                        putString("time", currentActivity.time)
                        putString("emoji", currentActivity.emoji)
                        putString("description", currentActivity.description)
                    }
            }
    }
}
