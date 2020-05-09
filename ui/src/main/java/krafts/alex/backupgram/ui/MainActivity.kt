package krafts.alex.backupgram.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.AuthOk
import krafts.alex.tg.TgClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware,
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override val kodein: Kodein by closestKodein()

    private val settings: SettingsRepository by instance()

    private val client: TgClient by instance()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings.darkMode.observe(this, Observer { dark ->
            delegate.localNightMode = if (dark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        })

        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        button_login.visibility = if (!client.haveAuthorization) {
            View.VISIBLE
        } else {
            View.GONE
        }
        navController.addOnDestinationChangedListener { _, _, _ ->
            nav_host_fragment.view?.let { hideKeyboard(it) }
        }

        (bottom_nav)?.let {
            NavigationUI.setupWithNavController(it, navController)
        }

        val barConf = AppBarConfiguration.Builder(
            setOf(
                R.id.messages_destination,
                R.id.users_destination,
                R.id.settings_destination
            )
        ).build()
        NavigationUI.setupActionBarWithNavController(this, navController, barConf)

        button_login.setOnClickListener {
            navController.navigate(R.id.login_destination)
        }

        client.loginState.observe(this, Observer {
            if (it is AuthOk) button_login?.visibility = View.GONE
        })

        Fabric.with(this, Crashlytics())
    }

    override fun onStart() {
        if (!client.haveAuthorization) {
            navController.navigate(R.id.login_destination)
        }
        super.onStart()
    }

    private fun hideKeyboard(view: View) {
        currentFocus?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as?
                InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        if (pref.key == "vpn")
            navController.navigate(R.id.vpn_destination)
        return true
    }

    override fun onSupportNavigateUp(): Boolean = NavigationUI.navigateUp(
        Navigation.findNavController(this, R.id.nav_host_fragment), null
    )
}