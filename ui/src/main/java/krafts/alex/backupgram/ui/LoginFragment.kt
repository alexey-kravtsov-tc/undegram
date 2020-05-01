package krafts.alex.backupgram.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineExceptionHandler
import krafts.alex.tg.AuthOk
import krafts.alex.tg.EnterCode
import krafts.alex.tg.EnterPassword
import krafts.alex.tg.EnterPhone
import krafts.alex.tg.TgClient
import org.kodein.di.generic.instance
import kotlin.coroutines.CoroutineContext

class LoginFragment : FragmentBase() {

    private val viewModel: LoginViewModel by viewModel()
    
    private val client: TgClient by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snackbar = Snackbar.make(view, "Logged in!", Snackbar.LENGTH_LONG)
        phone_enter_form.isVisible = false
        code_enter_form.isVisible = false
        password_enter_form.isVisible = false
        val controller = findNavController(view)

        viewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            progress?.isVisible = state is LoginProgressState.Loading
            errorView?.isVisible = state is LoginProgressState.Error
            if (state is LoginProgressState.Error) {
                errorView.text = state.message
            }
            (state as? LoginProgressState.HasState)?.authState?.let{
                when (it) {
                    EnterPhone -> phone_enter_form?.visibility = View.VISIBLE

                    is EnterPassword -> {
                        progress?.isVisible = false
                        password_enter_form?.visibility = View.VISIBLE
                        takeLook?.isVisible = false
                        password?.hint =
                            "${getString(R.string.prompt_password)} (${it.hint})"
                        log("Enter password step")
                    }

                    EnterCode -> {
                        code_enter_form?.visibility = View.VISIBLE
                        takeLook?.isVisible = false
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
                }}
        })

        button_send_phone.setOnClickListener {
            viewModel.sendPhone(phone.text.toString())
            phone_enter_form.isVisible = false
            log("Find phone")
        }
        button_send_code.setOnClickListener {
            viewModel.sendCode(code.text.toString())
            code_enter_form.isVisible = false
            log("Send code")
        }
        button_send_password.setOnClickListener {
            viewModel.sendPassword(password.text.toString())
            password_enter_form.isVisible = false
            log("Send password")
        }
        takeLook?.setOnClickListener {
            controller.popBackStack(R.id.messages_destination, false)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.button_login?.isVisible = false
        activity?.bottom_nav?.isVisible = false
    }

    override fun onDetach() {
        super.onDetach()
        activity?.bottom_nav?.visibility = View.VISIBLE
        if (!client.haveAuthorization) {
            activity?.button_login?.visibility = View.VISIBLE
        }
    }
}

fun onError(func: (Throwable) -> Unit) = CoroutineExceptionHandler { _, e -> func(e) }

infix fun CoroutineContext.onError(function: (Throwable) -> Unit): CoroutineContext =
    this + CoroutineExceptionHandler { _, e -> function(e) }