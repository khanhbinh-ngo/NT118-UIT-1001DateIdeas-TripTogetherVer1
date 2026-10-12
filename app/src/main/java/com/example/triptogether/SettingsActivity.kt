package com.example.triptogether

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.triptogether.databinding.ActivitySettingsBinding
import com.example.triptogether.ui.PlannerActivity

class SettingsActivity : PlannerActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val preferences = getSharedPreferences("appearance", MODE_PRIVATE)
        val mode = preferences.getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val group = binding.themeGroup
        group.check(
            when (mode) {
                AppCompatDelegate.MODE_NIGHT_NO -> R.id.themeLight
                AppCompatDelegate.MODE_NIGHT_YES -> R.id.themeDark
                else -> R.id.themeSystem
            }
        )
        group.setOnCheckedChangeListener { _, selected ->
            val selectedMode =
                when (selected) {
                    R.id.themeLight -> AppCompatDelegate.MODE_NIGHT_NO
                    R.id.themeDark -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            preferences.edit().putInt("nightMode", selectedMode).apply()
            AppCompatDelegate.setDefaultNightMode(selectedMode)
            delegate.localNightMode = selectedMode
        }
    }
}
