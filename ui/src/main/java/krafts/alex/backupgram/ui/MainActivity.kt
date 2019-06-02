package krafts.alex.backupgram.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import krafts.alex.backupgram.ui.messages.MessagesFragment
import krafts.alex.tg.AuthOk
import krafts.alex.tg.TgEvent
import krafts.alex.tg.entity.Message

class MainActivity : AppCompatActivity(), MessagesFragment.OnListFragmentInteractionListener {

    override fun onListFragmentInteraction(item: Message?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var authOk = false

    init {
        TgEvent.listen<AuthOk>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            authOk = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)



        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        if (!authOk) {
            navController.navigate(R.id.login_destination)
        }


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

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

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
