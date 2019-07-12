package krafts.alex.backupgram.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import krafts.alex.tg.entity.User
import krafts.alex.tg.repo.SessionRepository

class UsersViewModel(sessionRepository: SessionRepository) : ViewModel() {

    val usersBySessionCount: MutableLiveData<List<User>> = MutableLiveData()

    init {
        viewModelScope.launch {
            usersBySessionCount.value = sessionRepository.getUsersBySessionCount()
        }
    }
}