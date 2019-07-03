package krafts.alex.backupgram.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SettingsRepository(context: Context) {

    private val sharedPrefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    val hideEdited = bool(SettingsFragment.HIDE_EDIT)

    val reverseScroll = bool(SettingsFragment.REVERSE_SCROLL)

    val animations = bool(SettingsFragment.ANIMATIONS)

    private fun bool(key: String) = LivePref.Bool(sharedPrefs, key)

}