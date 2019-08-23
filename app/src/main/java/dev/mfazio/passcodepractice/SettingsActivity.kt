package dev.mfazio.passcodepractice

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var prefs: SharedPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            activity?.let { activity ->
                this.prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            }

            mapOf(
                successesResetPreferenceKey to successSharedPreferenceKey,
                ohNosResetPreferenceKey to ohNoSharedPreferenceKey,
                clearsResetPreferenceKey to clearsSharedPreferenceKey
            ).forEach { (prefKey, sharedPrefKey) ->
                findPreference<Preference>(prefKey)?.setOnPreferenceClickListener {
                    resetCounts(sharedPrefKey)
                    Toast.makeText(activity, "Count reset!", Toast.LENGTH_SHORT).show()
                    true
                }
            }

            findPreference<Preference>(allCountsResetPreference)?.setOnPreferenceClickListener {
                listOf(
                    successSharedPreferenceKey,
                    ohNoSharedPreferenceKey,
                    clearsSharedPreferenceKey
                ).forEach { resetCounts(it) }

                Toast.makeText(activity, "All counts reset!", Toast.LENGTH_SHORT).show()

                true
            }
        }

        private fun resetCounts(key: String) {
            prefs.edit()?.let { editor ->
                editor.putInt(key, 0)
                editor.apply()
            }
        }
    }
}