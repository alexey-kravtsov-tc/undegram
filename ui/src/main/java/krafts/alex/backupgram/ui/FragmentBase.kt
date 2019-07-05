package krafts.alex.backupgram.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import krafts.alex.backupgram.ui.settings.SettingsRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

open class FragmentBase : Fragment(), KodeinAware {

    private val name = this::class.java.simpleName

    override val kodein: Kodein by closestKodein()

    protected val analytics: FirebaseAnalytics by instance()

    protected val settings: SettingsRepository by instance()

    protected fun log(string: String) = Crashlytics.log(string)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { analytics.setCurrentScreen(it, name, null) }
        log("onViewCreated $name")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy $name")
    }

    override fun onStop() {
        super.onStop()
        log("onStop $name")
    }

}