package com.dicoding.habitapp.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.utils.DarkMode

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            //TODO 11 : Update theme based on value in ListPreference
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .registerOnSharedPreferenceChangeListener(this)
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            val darkMode = getString(R.string.pref_key_dark)
            key?.let {
                if (it == darkMode) sharedPreferences?.let { pref ->
                    val darkModeValues = resources.getStringArray(R.array.dark_mode_value)
                    when(pref.getString(darkMode, darkModeValues[0])) {
                        darkModeValues[0] -> updateTheme(DarkMode.FOLLOW_SYSTEM.value)
                        darkModeValues[1] -> updateTheme(DarkMode.ON.value)
                        darkModeValues[2] -> updateTheme(DarkMode.OFF.value)
                        else -> updateTheme(DarkMode.FOLLOW_SYSTEM.value)
                    }
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(this)
        }
    }
}