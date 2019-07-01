package krafts.alex.backupgram.ui.users

import androidx.lifecycle.ViewModel
import krafts.alex.tg.repo.SessionRepository

class UsersViewModel(sessionRepository: SessionRepository) : ViewModel() {

    val usersBySessionCount = sessionRepository.getUsersBySessionCount()

}