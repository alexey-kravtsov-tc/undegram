package krafts.alex.backupgram.ui.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.entity.ChatWithLastMessage
import krafts.alex.tg.repo.MessagesRepository

class ChatListViewModel(
    messagesRepository: MessagesRepository,
    settings: SettingsRepository
) : ViewModel() {

    val lastMessagesPerChat: LiveData<PagedList<ChatWithLastMessage>> = Transformations
        .switchMap(settings.hideEdited) { edited ->
            LivePagedListBuilder(messagesRepository.getAllRemoved(edited), 10).build()
        }

}
