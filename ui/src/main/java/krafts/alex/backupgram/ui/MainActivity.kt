package krafts.alex.backupgram.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavOptions
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.settings_destination) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
