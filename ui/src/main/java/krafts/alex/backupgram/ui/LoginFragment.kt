package krafts.alex.backupgram.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import krafts.alex.tg.AuthOk
import krafts.alex.tg.EnterCode
import krafts.alex.tg.EnterPassword
import krafts.alex.tg.EnterPhone

class LoginFragment : FragmentBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snackbar = Snackbar.make(view, "Logged in!", Snackbar.LENGTH_LONG)
        phone_enter_form.visibility = View.VISIBLE
        code_enter_form.visibility = View.GONE
        password_enter_form.visibility = View.GONE
        val controller = findNavController(view)

        BackApp.client.authState.observe(viewLifecycleOwner, Observer {
            when (it) {
                EnterPhone -> phone_enter_form?.visibility = View.VISIBLE

                is EnterPassword -> {
                    progress?.visibility = View.GONE
                    password_enter_form?.visibility = View.VISIBLE
                    goBack?.visibility = View.GONE
                    password?.hint =
                        "${getString(R.string.prompt_password)} (${it.hint})"
                    log("Enter password step")
                }

                EnterCode -> {
                    progress?.visibility = View.GONE
                    code_enter_form?.visibility = View.VISIBLE
                    goBack?.visibility = View.GONE
                    log("Enter code step")
                }

                AuthOk -> {
                    snackbar.show()
                    controller.popBackStack(
                        R.id.messages_destination,
                        false
                    )
                    activity?.bottom_nav?.visibility = View.VISIBLE
                    analytics.logEvent(
                        FirebaseAnalytics.Event.LOGIN,
                        null
                    )
                    log("Auth ok step")
                }
            }
        })

        button_send_phone.setOnClickListener {
            BackApp.client?.sendPhone(phone.text.toString())
            phone_enter_form.visibility = View.GONE
            progress.visibility = View.VISIBLE
            log("Find phone")
        }
        button_send_code.setOnClickListener {
            BackApp.client?.sendCode(code.text.toString())
            code_enter_form.visibility = View.GONE
            progress.visibility = View.VISIBLE
            log("Send code")
        }
        button_send_password.setOnClickListener {
            BackApp.client?.sendPassword(password.text.toString())
            password_enter_form.visibility = View.GONE
            progress.visibility = View.VISIBLE
            log("Send password")
        }
        goBack?.setOnClickListener {
            controller.popBackStack(R.id.messages_destination, false)
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
        if (!BackApp.client?.haveAuthorization) {
            activity?.button_login?.visibility = View.VISIBLE
        }
    }
}