package uz.perfectalgorithm.projects.tezkor.data.sources.local.chat

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.dao.MessageModelDao
import uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.entity.MessageModel



@Database(entities = [MessageModel::class], version = 1)
abstract class ConversationChatDatabase : RoomDatabase() {
    abstract fun messageModelDao(): MessageModelDao

    companion object {
        @Volatile
        private var INSTANCE: ConversationChatDatabase? = null

        fun getDatabase(): ConversationChatDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    App.instance.applicationContext,
                    ConversationChatDatabase::class.java,
                    "chat_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}