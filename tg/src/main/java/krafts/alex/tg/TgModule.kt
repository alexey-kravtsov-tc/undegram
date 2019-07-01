package krafts.alex.tg

import android.content.Context
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.SessionRepositoryImpl
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
        bind<SessionRepository>() with singleton {
            SessionRepositoryImpl(instance(), instance())
        }
    }
}