package krafts.alex.backupgram.ui.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.entity.Message
import krafts.alex.tg.repo.MessagesRepository

class ChatListViewModel(
    messagesRepository: MessagesRepository,
    settings: SettingsRepository
) : ViewModel() {

    val lastMessagesPerChat: LiveData<List<Message>> = Transformations
        .switchMap(settings.hideEdited) { edited ->
            //TODO move dao logic here MediatorLiveData<List<Message>>
            messagesRepository.getAllRemoved(edited) }

}