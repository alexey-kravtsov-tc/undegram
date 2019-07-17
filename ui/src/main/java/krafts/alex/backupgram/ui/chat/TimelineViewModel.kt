package krafts.alex.backupgram.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import krafts.alex.tg.repo.SessionRepository

class TimelineViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    data class Total(
        val today: Int,
        val yesterday: Int = 0
    )

    private lateinit var data: LiveData<List<Entry>>
    private val lastUserId = -1

    var totalTime = MutableLiveData<Total>()

    fun calculateEntries(id: Int): LiveData<List<Entry>> {
        if (id != lastUserId) {

            viewModelScope.launch {
                totalTime.value = Total(
                    sessionRepository.getTodayTotal(id),
                    sessionRepository.getYesterdayTotal(id)
                )
            }

            data = sessionRepository.getSessionsForUser(id).switchMap {

                val result = MutableLiveData<List<Entry>>()
                val values = ArrayList<Entry>()

                it.forEach {
                    values.add(Entry(it.start.toInterval().toFloat(), 0F))

                    for (x in it.start.toInterval() + 1 until it.expires.toInterval()) {
                        values.add(Entry(x.toFloat(), 1F))
                    }

                    values.add(Entry(it.expires.toInterval().toFloat(), 0F))
                }

                result.value = values
                result
            }
        }
        return data
    }

    private fun Int.toInterval() = this.toLong()
}