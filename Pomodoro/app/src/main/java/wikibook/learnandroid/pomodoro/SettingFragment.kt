package wikibook.learnandroid.pomodoro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingFragment : PreferenceFragmentCompat() {
    // (1)
    companion object {
        val SETTING_PREF_FILENAME = "pomodoro_setting"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // (2)
        (activity as AppCompatActivity).supportActionBar?.hide()

        // (3)
        preferenceManager.sharedPreferencesName = SETTING_PREF_FILENAME

        // (4)
        addPreferencesFromResource(R.xml.pomodoro_preferences)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {}
}
