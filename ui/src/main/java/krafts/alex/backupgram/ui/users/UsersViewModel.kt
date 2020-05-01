package krafts.alex.backupgram.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import krafts.alex.tg.entity.UserWithSessions
import krafts.alex.tg.repo.SessionRepository

class UsersViewModel(sessionRepository: SessionRepository) : ViewModel() {

    val period = MutableLiveData<Period>(Period(4, 0))

    val usersBySessionCount: LiveData<PagedList<UserWithSessions>> =
        period.switchMap {

            LivePagedListBuilder(
                sessionRepository.getUsersBySessionCount(it.startOffset, it.endOffset),
                20
            ).build()
        }

    data class Period(val startOffset: Int, val endOffset: Int)
}