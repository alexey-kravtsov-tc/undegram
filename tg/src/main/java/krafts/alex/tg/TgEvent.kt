package krafts.alex.tg

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object EnterPhone
object EnterCode
object EnterPassword
object AuthOk

object TgEvent {

    val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    inline fun <reified T> listen(): Observable<T> = publisher.ofType(T::class.java)

}

