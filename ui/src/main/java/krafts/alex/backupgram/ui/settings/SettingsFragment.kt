package krafts.alex.backupgram.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import krafts.alex.backupgram.ui.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
    }
}