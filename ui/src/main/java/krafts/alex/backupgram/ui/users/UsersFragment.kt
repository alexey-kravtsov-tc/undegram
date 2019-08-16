package krafts.alex.backupgram.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.crashlytics.android.Crashlytics
import com.kizitonwose.time.hours
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import krafts.alex.backupgram.ui.FragmentBase
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.display
import krafts.alex.backupgram.ui.viewModel
import krafts.alex.tg.repo.TgTime
import java.util.concurrent.TimeUnit

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

        viewModel.period.value?.let {
            spinner?.setupData(ArrayList((24L downTo 0L).toList()), it.startOffset, it.endOffset)
        }

        spinner?.setChartListener(this)

        viewModel.usersBySessionCount.observe(this, Observer {
            it?.let {
                placeholder.visibility = if (it.count() > 3) View.GONE else View.VISIBLE
                adapt.submitList(it)
                Crashlytics.setInt("users_count", it.count())
            }
        })

        viewModel.period.observe(this, Observer {
            it?.let {
                activity?.toolbar?.title = "Online activity" +
                    " from ${displayOffset(it.startOffset)}" +
                    if (it.endOffset == 0) {
                        " until now"
                    } else {
                        " to ${displayOffset(it.endOffset)}"
                    }
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

    private fun displayOffset(offset: Int) = (TgTime.nowInSeconds() - offset * 3600).display()

    private var job = Job()

    override fun onRangeChanged(
        hoursBeforeFrame: Int, hoursAfterFrame: Int, hoursInFrame: Int, dx: Float
    ) {
        job.cancel()
        job = GlobalScope.launch {
            delay(200)
            while (job.isActive) {
                viewModel.period.postValue(
                    UsersViewModel.Period(
                        hoursAfterFrame + hoursInFrame,
                        hoursAfterFrame
                    )
                )
                delay(1000 * 60)
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}