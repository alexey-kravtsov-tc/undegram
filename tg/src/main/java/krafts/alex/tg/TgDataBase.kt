package krafts.alex.tg

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import krafts.alex.tg.dao.MessagesDao
import krafts.alex.tg.dao.UsersDao
import krafts.alex.tg.entity.Message
import krafts.alex.tg.entity.User

@Database(entities = [Message::class, User::class], version = 3, exportSchema = false)
abstract class TgDataBase : RoomDatabase() {

    abstract fun messages(): MessagesDao

    abstract fun users(): UsersDao

    companion object {
        /**
         * The only instance
         */
        private var sInstance: TgDataBase? = null

        /**
         * Gets the singleton instance of TgDataBase.
         *
         * @param context The context.
         * @return The singleton instance of TgDataBase.
         */
        @Synchronized
        fun getInstance(context: Context): TgDataBase {
            if (sInstance == null) {
                sInstance = Room
                    .databaseBuilder(context.applicationContext, TgDataBase::class.java, "data")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return sInstance!!
        }
    }
}
