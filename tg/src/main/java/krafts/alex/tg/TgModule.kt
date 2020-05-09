package krafts.alex.tg

import android.content.Context
import krafts.alex.tg.repo.ChatRepository
import krafts.alex.tg.repo.EditRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.SessionRepositoryImpl
import krafts.alex.tg.repo.UsersRepository
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

object TgModule {
    fun resolve(context: Context) = Kodein.Module(name = "tg") {
        bind() from eagerSingleton { TgDataBase.getInstance(context) }
        bind() from singleton { instance<TgDataBase>().sessions() }
        bind() from singleton { instance<TgDataBase>().users() }
        bind() from singleton { instance<TgDataBase>().chats() }
        bind() from singleton { instance<TgDataBase>().messages() }
        bind() from singleton { instance<TgDataBase>().edits() }
        bind<SessionRepository>() with singleton {
            SessionRepositoryImpl(instance(), instance())
        }
        bind() from singleton { MessagesRepository(
            messagesDao = instance(),
            editRepository = instance(),
            tgClient = instance()
        ) }
        bind() from singleton { EditRepository(instance()) }
        bind() from singleton { UsersRepository(instance(), instance()) }
        bind() from singleton { ChatRepository(instance(), instance()) }
        bind() from singleton { TelegramCollectors(
            messagesRepository = instance(),
            usersRepository = instance(),
            chatRepository = instance(),
            sessionRepository = instance()
        ) }
        bind() from singleton { TgClient(instance()) }
    }
}