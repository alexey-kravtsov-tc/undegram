package krafts.alex.backupgram.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import krafts.alex.backupgram.ui.settings.SettingsFragment
import krafts.alex.tg.AuthOk
import krafts.alex.tg.TgEvent
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        delegate.localNightMode = if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(SettingsFragment.DARK_KEY, false)
        ) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, _, _ ->
            nav_host_fragment.view?.let { hideKeyboard(it) }
        }

        (bottom_nav)?.let {
            NavigationUI.setupWithNavController(it, navController)
            NavigationUI.setupActionBarWithNavController(this, navController)
        }
        if (BackApp.loginClient?.haveAuthorization == false) {
            navController.navigate(R.id.login_destination)
            button_login.visibility = View.VISIBLE
        } else {
            BackApp.startService(applicationContext)
            button_login.visibility = View.GONE
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        button_login.setOnClickListener {
            navController.navigate(R.id.login_destination)
        }

        TgEvent.listen<AuthOk>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            BackApp.startService(applicationContext)
            button_login.visibility = View.GONE
        }
        Fabric.with(this, Crashlytics())
    }

    private fun hideKeyboard(view: View) {
        currentFocus?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as?
                InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSupportNavigateUp(): Boolean = NavigationUI.navigateUp(
        Navigation.findNavController(this, R.id.nav_host_fragment), null
    )
}
