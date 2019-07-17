package krafts.alex.backupgram.ui.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.entity.ChatWithLastMessage
import krafts.alex.tg.repo.MessagesRepository

class ChatListViewModel(
    messagesRepository: MessagesRepository,
    settings: SettingsRepository
) : ViewModel() {

    val lastMessagesPerChat: LiveData<List<ChatWithLastMessage>> = Transformations
        .switchMap(settings.hideEdited) { edited ->
            messagesRepository.getAllRemoved(edited)
        }
}
