package krafts.alex.backupgram.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_login.*
import krafts.alex.tg.*

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone_enter_form.visibility = View.VISIBLE
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
            Snackbar.make(view, "Logged in!", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            findNavController().navigate(R.id.action_messages)
        }

        button_send_phone.setOnClickListener {
            BackApp.client.sendPhone(phone.text.toString())
            phone_enter_form.visibility = View.GONE
        }
        button_send_code.setOnClickListener {
            BackApp.client.sendCode(code.text.toString())
            code_enter_form.visibility = View.GONE
        }
        button_send_password.setOnClickListener {
            BackApp.client.sendPassword(password.text.toString())
            password_enter_form.visibility = View.GONE
        }
    }

}