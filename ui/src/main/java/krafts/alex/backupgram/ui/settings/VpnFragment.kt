package krafts.alex.backupgram.ui.settings

import android.os.Bundle
import com.takisoft.preferencex.PreferenceFragmentCompat
import krafts.alex.backupgram.ui.R

class VpnFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_vpn, rootKey)
    }
}