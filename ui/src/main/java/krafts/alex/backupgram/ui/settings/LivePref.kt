package krafts.alex.backupgram.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager

abstract class LivePref<T>(
    open val sharedPrefs: SharedPreferences,
    open val key: String,
    open val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key, defValue)
            }
        }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class BooleanLivePref(
    override val sharedPrefs: SharedPreferences,
    override val key: String,
    override val defValue: Boolean
) : LivePref<Boolean>(sharedPrefs, key, defValue) {

    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean =
        sharedPrefs.getBoolean(key, defValue)
}

class SettingsRepo(context: Context) {
    private val sharedPrefs: SharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)

    val hideEdited = BooleanLivePref(sharedPrefs, SettingsFragment.HIDE_EDIT, false)
    val reverseSroll = BooleanLivePref(sharedPrefs, SettingsFragment.REVERSE_SCROLL, false)
}