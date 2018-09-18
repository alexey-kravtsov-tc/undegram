package krafts.alex.backupgram.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import krafts.alex.tg.*

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)

        phone_enter_form.visibility = View.GONE
        code_enter_form.visibility = View.GONE
        password_enter_form.visibility = View.GONE

        TgEvent.listen<EnterPhone>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            phone_enter_form.visibility = View.VISIBLE
        }
        TgEvent.listen<EnterPassword>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            password_enter_form.visibility = View.VISIBLE
        }
        TgEvent.listen<EnterCode>().observeOn(AndroidSchedulers.mainThread()).subscribe{
            code_enter_form.visibility = View.VISIBLE
        }
        TgEvent.listen<AuthOk>().observeOn(AndroidSchedulers.mainThread()).subscribe{
            Toast.makeText(this, "Already logged in!!", Toast.LENGTH_LONG).show()
        }


        val client = TgClient(applicationContext)

        button_send_phone.setOnClickListener {
            client.sendPhone(phone.text.toString())
            phone_enter_form.visibility = View.GONE
        }
        button_send_code.setOnClickListener {
            client.sendCode(code.text.toString())
            code_enter_form.visibility = View.GONE
        }
        button_send_password.setOnClickListener {
            client.sendPassword(password.text.toString())
            password_enter_form.visibility = View.GONE
        }

        super.onCreate(savedInstanceState)

    }
}