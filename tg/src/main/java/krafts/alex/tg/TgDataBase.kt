package krafts.alex.tg

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import krafts.alex.tg.dao.ChatDao
import krafts.alex.tg.dao.MessagesDao
import krafts.alex.tg.dao.UsersDao
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.Message
import krafts.alex.tg.entity.User
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import krafts.alex.tg.dao.SessionsDao
import krafts.alex.tg.entity.Session

@Database(
    entities = [Message::class, User::class, Chat::class, Session::class],
    version = 5,
    exportSchema = false
)
abstract class TgDataBase : RoomDatabase() {

    abstract fun messages(): MessagesDao

    abstract fun users(): UsersDao

    abstract fun chats(): ChatDao

    abstract fun sessions(): SessionsDao

    companion object {
        /**
         * The only instance
         */
        private var sInstance: TgDataBase? = null

        private val chat_migration: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Chat` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `fileId` INTEGER, `localPath` TEXT, `downloaded` INTEGER, PRIMARY KEY(`id`))"
                )
            }
        }

        private val session_migration: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Session` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `start` INTEGER NOT NULL, `expires` INTEGER NOT NULL)"
                )
            }
        }

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
                    .addMigrations(chat_migration, session_migration)
                    .allowMainThreadQueries()
                    .build()
            }
            return sInstance!!
        }
    }
}
