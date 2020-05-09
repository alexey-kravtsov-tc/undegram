package krafts.alex.backupgram.ui

import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.UsersRepository

class AddExamplesUseCase(
    private val sessionRepository: SessionRepository,
    private val messagesRepository: MessagesRepository,
    private val usersRepository: UsersRepository
) {
    suspend fun addExamples() {
        usersRepository.addExampleUser()
        messagesRepository.addExampleMessages()
        sessionRepository.addExampleSessions()
    }
}