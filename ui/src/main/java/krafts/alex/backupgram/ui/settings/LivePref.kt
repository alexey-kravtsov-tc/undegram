package krafts.alex.backupgram.ui.settings

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.crashlytics.android.Crashlytics

sealed class LivePref<T>(
    protected val sharedPrefs: SharedPreferences,
    protected val key: String,
    private val getValueFromPreferences: SharedPreferences.() -> T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            if (key == this.key) {
                value = getValueFromPreferences(pref)
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
        LivePref<Boolean>(sharedPrefs, key, {
            val value = getBoolean(key, false)
            Crashlytics.setBool(key, value)
            value
        }) {

        fun switch() {
            value?.let {
                sharedPrefs.edit().putBoolean(key, !it).apply()
            }
        }
    }

    class Text(sharedPrefs: SharedPreferences, key: String) :
        LivePref<String>(sharedPrefs, key, { getString(key, "") ?: "" })
}

