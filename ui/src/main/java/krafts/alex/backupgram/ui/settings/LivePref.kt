package krafts.alex.backupgram.ui.settings

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

sealed class LivePref<T>(
    private val sharedPrefs: SharedPreferences,
    private val key: String,
    private val getValueFromPreferences: SharedPreferences.() -> T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) {
                value = getValueFromPreferences(sharedPrefs)
            }
        }

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(sharedPrefs)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    class Bool(sharedPrefs: SharedPreferences, key: String) :
        LivePref<Boolean>(sharedPrefs, key, { getBoolean(key, false) })

    class Text(sharedPrefs: SharedPreferences, key: String) :
        LivePref<String>(sharedPrefs, key, { getString(key, "") ?: "" })
}
