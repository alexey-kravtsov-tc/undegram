package krafts.alex.backupgram.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import krafts.alex.backupgram.ui.FragmentBase
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.viewModel

class UsersFragment : FragmentBase(), TimeLineSpinnerListener {


    private val viewModel: UsersViewModel by viewModel()

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

        spinner?.setupData(ArrayList((24L downTo 0L).toList()))
        spinner?.setChartListener(this)

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

    private var job = Job()

    override fun onRangeChanged(
        hoursBeforeFrame: Int, hoursAfterFrame: Int, hoursInFrame: Int, dx: Float
    ) {
        job.cancel()
        job = GlobalScope.launch {
            delay(200)
            viewModel.period.postValue(
                UsersViewModel.Period(
                    hoursAfterFrame + hoursInFrame,
                    hoursAfterFrame
                )
            )
        }
    }
}