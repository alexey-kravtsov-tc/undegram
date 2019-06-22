package krafts.alex.backupgram.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
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
            phone_enter_form?.visibility = View.VISIBLE
        }
        TgEvent.listen<EnterPassword>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            progress?.visibility = View.GONE
            password_enter_form?.visibility = View.VISIBLE
        }
        TgEvent.listen<EnterCode>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            progress?.visibility = View.GONE
            code_enter_form?.visibility = View.VISIBLE
        }
        TgEvent.listen<AuthOk>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Snackbar.make(view, "Logged in!", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            findNavController(view).navigate(R.id.action_messages)
        }

        button_send_phone.setOnClickListener {
            BackApp.loginClient?.sendPhone(phone.text.toString())
            phone_enter_form.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }
        button_send_code.setOnClickListener {
            BackApp.loginClient?.sendCode(code.text.toString())
            code_enter_form.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }
        button_send_password.setOnClickListener {
            BackApp.loginClient?.sendPassword(password.text.toString())
            password_enter_form.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.button_login?.visibility = View.GONE
        activity?.bottom_nav?.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        activity?.bottom_nav?.visibility = View.VISIBLE
        if (BackApp.loginClient?.haveAuthorization == false) {
            activity?.button_login?.visibility = View.VISIBLE
        }
    }

}