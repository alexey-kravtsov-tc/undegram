package krafts.alex.backupgram.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import krafts.alex.tg.entity.UserWithSessions
import krafts.alex.tg.repo.SessionRepository

class UsersViewModel(sessionRepository: SessionRepository) : ViewModel() {

    val usersBySessionCount: LiveData<PagedList<UserWithSessions>> =
        LivePagedListBuilder(sessionRepository.getUsersBySessionCount(), 20).build()

}