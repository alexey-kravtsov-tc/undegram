package krafts.alex.backupgram.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.entity.MessageFromUserWithEdits
import krafts.alex.tg.repo.MessagesRepository

class ChatViewModel(
    private val messagesRepository: MessagesRepository,
    val settings: SettingsRepository
) : ViewModel() {

    private lateinit var messages: LiveData<PagedList<MessageFromUserWithEdits>>
    private var lastChatId = -1L

    fun pagedListForChat(id: Long): LiveData<PagedList<MessageFromUserWithEdits>> {
        if (id != lastChatId) {
            messages = Transformations.switchMap(settings.hideEdited) { edited ->
                LivePagedListBuilder(messagesRepository.getRemovedForChat(id, edited), 10).build()
            }
            lastChatId = id
        }
        return messages
    }

    fun deleteMessagePermanently(message: MessageFromUserWithEdits) {
        messagesRepository.deletePermanently(message.messageId)
    }
}