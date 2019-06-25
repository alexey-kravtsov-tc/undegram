package krafts.alex.backupgram.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import krafts.alex.tg.AuthOk
import krafts.alex.tg.EnterCode
import krafts.alex.tg.EnterPassword
import krafts.alex.tg.EnterPhone
import krafts.alex.tg.TgEvent

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snackbar = Snackbar.make(view, "Logged in!", Snackbar.LENGTH_LONG)
        phone_enter_form.visibility = View.VISIBLE
        code_enter_form.visibility = View.GONE
        password_enter_form.visibility = View.GONE
        val controller = findNavController(view)

        TgEvent.listen<EnterPhone>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            phone_enter_form?.visibility = View.VISIBLE
        }
        TgEvent.listen<EnterPassword>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            progress?.visibility = View.GONE
            password_enter_form?.visibility = View.VISIBLE
            goBack?.visibility = View.GONE
        }
        TgEvent.listen<EnterCode>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            progress?.visibility = View.GONE
            code_enter_form?.visibility = View.VISIBLE
            goBack?.visibility = View.GONE
        }
        TgEvent.listen<AuthOk>().observeOn(AndroidSchedulers.mainThread()).subscribe {
            snackbar.show()
            hideKeyboard(view)
            controller.popBackStack(R.id.messages_destination, false)
            activity?.bottom_nav?.visibility = View.VISIBLE
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
        goBack?.setOnClickListener {
            hideKeyboard(view)
            controller.popBackStack(R.id.messages_destination, false)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.button_login?.visibility = View.GONE
        activity?.bottom_nav?.visibility = View.GONE
    }

    private fun hideKeyboard(view: View) {
        this.activity?.currentFocus?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as?
                InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.bottom_nav?.visibility = View.VISIBLE
        if (BackApp.loginClient?.haveAuthorization == false) {
            activity?.button_login?.visibility = View.VISIBLE
        }
    }

}