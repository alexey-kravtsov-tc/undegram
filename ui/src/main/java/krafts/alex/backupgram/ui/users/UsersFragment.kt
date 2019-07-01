package krafts.alex.backupgram.ui.users

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_users.*
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsFragment
import krafts.alex.backupgram.ui.viewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

class UsersFragment : Fragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModel : UsersViewModel by viewModel()

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

        val reverse = PreferenceManager
            .getDefaultSharedPreferences(activity)
            .getBoolean(SettingsFragment.REVERSE_SCROLL, false)

        with(list) {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                reverse
            )
            adapter = adapt
        }
    }
}