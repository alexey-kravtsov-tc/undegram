package krafts.alex.backupgram.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import krafts.alex.backupgram.ui.chat.TimelineViewModel
import krafts.alex.backupgram.ui.chat.ChatViewModel
import krafts.alex.backupgram.ui.chatList.ChatListViewModel
import krafts.alex.backupgram.ui.users.UsersViewModel
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.instanceOrNull
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ViewModelFactory(private val injector: DKodein) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return injector.instanceOrNull<ViewModel>(tag = modelClass.simpleName) as T?
            ?: modelClass.newInstance()
    }

    companion object {
        val viewModelModule = Kodein.Module(name = "viewModelModule") {
            bind<ViewModelProvider.Factory>() with singleton { ViewModelFactory(kodein.direct) }
            bindViewModel<UsersViewModel>() with provider { UsersViewModel(instance()) }
            bindViewModel<ChatListViewModel>() with provider {
                ChatListViewModel(instance(), instance())
            }
            bindViewModel<ChatViewModel>() with provider {
                ChatViewModel(instance(), instance(), instance())
            }
            bindViewModel<TimelineViewModel>() with provider {
                TimelineViewModel(instance())
            }
            bindViewModel<LoginViewModel>() with provider {
                LoginViewModel(instance())
            }
        }

        private inline fun <reified T : ViewModel> Kodein.Builder.bindViewModel(
            overrides: Boolean? = null
        ): Kodein.Builder.TypeBinder<T> {
            return bind<T>(T::class.java.simpleName, overrides)
        }
    }
}

inline fun <reified VM : ViewModel, T> T.viewModel(): Lazy<VM> where T : KodeinAware, T : Fragment {
    return lazy { ViewModelProviders.of(this, direct.instance()).get(VM::class.java) }
}
