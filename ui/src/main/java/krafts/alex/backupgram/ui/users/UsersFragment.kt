package krafts.alex.backupgram.ui.users

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_users.list
import kotlinx.android.synthetic.main.fragment_users.placeholder
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsRepo
import krafts.alex.backupgram.ui.viewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class UsersFragment : Fragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModel : UsersViewModel by viewModel()

    private val settings: SettingsRepo by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_users, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = UsersAdapter(emptyList())

        viewModel.usersBySessionCount?.observe(this, Observer {
            it?.let {
                adapt.setAll(it)
                placeholder.visibility = if (it.count() > 3) View.GONE else View.VISIBLE
            }
        })

        list?.adapter = adapt

        settings.reverseSroll.observe(this, Observer { reverse ->
            list?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, reverse)
        })
    }
}