package krafts.alex.tg.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import krafts.alex.tg.entity.Edit

@Dao
interface EditsDao {

    @Insert
    fun add(edit: Edit)

    @Query("SELECT * from Edit where messageId = :id ORDER BY date DESC")
    fun getByMessageId(id: Long): LiveData<List<Edit>>

}