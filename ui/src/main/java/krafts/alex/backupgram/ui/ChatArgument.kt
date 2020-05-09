package krafts.alex.backupgram.ui

import android.os.Parcel
import android.os.Parcelable

data class ChatArgument(
    val chatId: Long,
    val title: String?,
    val imagePath: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(chatId)
        parcel.writeString(title)
        parcel.writeString(imagePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatArgument> {
        override fun createFromParcel(parcel: Parcel): ChatArgument {
            return ChatArgument(parcel)
        }

        override fun newArray(size: Int): Array<ChatArgument?> {
            return arrayOfNulls(size)
        }
    }
}