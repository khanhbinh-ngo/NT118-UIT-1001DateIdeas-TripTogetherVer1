package com.example.triptogether

import androidx.fragment.app.FragmentFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triptogether.model.ActivityItem
import com.example.triptogether.ui.AddActivityDialogFragment
import com.example.triptogether.ui.DeleteActivityDialogFragment
import com.example.triptogether.ui.EditActivityDialogFragment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialogRestorationTest {
    @Test
    fun itineraryDialogsCanBeRecreatedByAndroidWithTheirSavedArguments() {
        val dialogs =
            listOf(
                AddActivityDialogFragment.newInstance("trip", "owner", 2),
                EditActivityDialogFragment.newInstance(
                    "trip",
                    "owner",
                    2,
                    "activity",
                    ActivityItem("09:30", "☕", "Coffee"),
                ),
                DeleteActivityDialogFragment.newInstance("trip", "owner", 2, "activity"),
            )
        dialogs.forEach { original ->
            val restored =
                FragmentFactory()
                    .instantiate(original.javaClass.classLoader!!, original.javaClass.name)
            restored.arguments = android.os.Bundle(original.requireArguments())
            assertEquals("trip", restored.requireArguments().getString("tripId"))
            assertEquals("owner", restored.requireArguments().getString("createdBy"))
            assertEquals(2, restored.requireArguments().getInt("dayIndex"))
            if (original is EditActivityDialogFragment) {
                assertEquals("09:30", restored.requireArguments().getString("time"))
                assertEquals("Coffee", restored.requireArguments().getString("description"))
            }
        }
    }
}
