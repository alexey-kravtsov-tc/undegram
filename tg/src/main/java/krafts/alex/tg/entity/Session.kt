package krafts.alex.tg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userId: Int,
    val start: Int,
    val expires: Int
) {

    companion object {

        fun fromTg(id: Int, start: Int, expires: Int) = Session(
            id = 0,
            userId = id,
            start = start,
            expires = expires
        )
    }
}