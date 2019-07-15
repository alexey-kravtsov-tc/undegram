package krafts.alex.backupgram.ui.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.entity.Message
import krafts.alex.tg.repo.MessagesRepository

class ChatListViewModel(
    messagesRepository: MessagesRepository,
    settings: SettingsRepository
) : ViewModel() {

    var lastMessagesPerChat: LiveData<List<Message>> = Transformations
        .switchMap(settings.hideEdited) { edited ->
            //TODO move dao logic here MediatorLiveData<List<Message>>
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(messagesRepository.getAllRemoved(edited))
            }
        }
}
