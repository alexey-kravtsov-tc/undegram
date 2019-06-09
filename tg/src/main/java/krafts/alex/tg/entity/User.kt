package krafts.alex.tg.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity
data class User(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val notifyDelete: Boolean?,
    val notifyOnline: Boolean?,
    @Embedded val photoBig: File?
) {

    override fun toString(): String {
        return "$firstName $lastName"
    }

    companion object {

        fun fromTg(usr: TdApi.User) = User(
            id = usr.id,
            firstName = usr.firstName,
            lastName = usr.lastName,
            phoneNumber = usr.phoneNumber,
            photoBig = usr.profilePhoto?.let { File.fromTg(it.big) },
            notifyDelete = false,
            notifyOnline = false
        )
    }
}

