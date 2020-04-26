package krafts.alex.backupgram.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import krafts.alex.backupgram.ui.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
    }

    companion object {
        const val SERVICE_ENABLED = "service_active"
        const val DARK_KEY = "dark_theme"
        const val HIDE_EDIT = "hide_edits"
        const val REVERSE_SCROLL = "reverse_scrolling"
        const val ANIMATIONS = "animations"
    }
}
