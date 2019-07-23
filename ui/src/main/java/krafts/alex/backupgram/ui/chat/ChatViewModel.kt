package krafts.alex.backupgram.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.entity.MessageFromUserWithEdits
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.UsersRepository

class ChatViewModel(
    private val messagesRepository: MessagesRepository,
    private val usersRepository: UsersRepository,
    val settings: SettingsRepository
) : ViewModel() {

    private lateinit var messages: LiveData<PagedList<MessageFromUserWithEdits>>
    private var lastChatId = -1L

    fun getDataForChat(id: Long) {
        if (id != lastChatId) {

            messages = Transformations.switchMap(settings.hideEdited) { edited ->
                LivePagedListBuilder(messagesRepository.getRemovedForChat(id, edited), 10).build()
            }
            lastChatId = id
        }
    }

    fun pagedListForChat(): LiveData<PagedList<MessageFromUserWithEdits>> {
        return messages
    }

    fun deleteMessagePermanently(message: MessageFromUserWithEdits) {
        messagesRepository.deletePermanently(message.messageId)
    }

    fun getUserUrl(id: Int): LiveData<String?> = liveData {
        emit(
            usersRepository.getAsync(id)?.userName
                ?.takeIf { it.isNotEmpty() }
                ?.let { "https://telegram.me/$it" }
        )
    }
}