package krafts.alex.backupgram.ui.users

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_users.list
import kotlinx.android.synthetic.main.fragment_users.placeholder
import krafts.alex.backupgram.ui.FragmentBase
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.backupgram.ui.viewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class UsersFragment : FragmentBase() {

    private val viewModel : UsersViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_users, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings.animations.observe(this, Observer { animate ->
            sharedElementReturnTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)?.apply { duration = 200 }
                ?.takeIf { animate == true }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        val adapt = UsersAdapter()

        viewModel.usersBySessionCount?.observe(this, Observer {
            it?.let {
                placeholder.visibility = if (it.count() > 3) View.GONE else View.VISIBLE
                adapt.submitList(it)
                Crashlytics.setInt("users_count", it.count())
            }
        })

        list?.adapter = adapt

        list?.viewTreeObserver?.addOnDrawListener {
            startPostponedEnterTransition()
        }

        settings.reverseScroll.observe(this, Observer { reverse ->
            (list?.layoutManager as? LinearLayoutManager)?.reverseLayout = reverse
        })
    }
}