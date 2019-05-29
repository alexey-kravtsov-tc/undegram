package krafts.alex.tg.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity
data class User(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    @Embedded val photoBig: File?
) {

    companion object {

        fun fromTg(usr: TdApi.User) = User(
            id = usr.id,
            firstName = usr.firstName,
            lastName = usr.lastName,
            phoneNumber = usr.phoneNumber,
            photoBig = usr.profilePhoto?.let { File.fromTg(it.big) }
        )
    }
}

