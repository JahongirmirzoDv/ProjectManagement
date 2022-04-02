package uz.perfectalgorithm.projects.tezkor.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalDatabase
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local.calendar.dao.EventDao
import uz.perfectalgorithm.projects.tezkor.data.sources.local.notification.NotificationDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Singleton
    fun getLocalStorage(): LocalStorage = LocalStorage.instance

    @Provides
    @Singleton
    fun getLocalDatabase(
        @ApplicationContext context: Context,
    ): LocalDatabase =
        Room.databaseBuilder(context, LocalDatabase::class.java, "MyLocal.db")
            .allowMainThreadQueries()
//            .addMigrations(MIGRATION_1_2)
//            .addMigrations(MIGRATION_2_3)
//            .addMigrations(MIGRATION_3_4)
//            .addMigrations(MIGRATION_4_5)
//            .addMigrations(MIGRATION_5_6)
//            .addMigrations(MIGRATION_6_7)
//            .addMigrations(MIGRATION_7_8)
            .build()

    @Provides
    @Singleton
    fun getEventDao(
        localDatabase: LocalDatabase
    ):  EventDao = localDatabase.eventDao()

    @Provides
    @Singleton
    fun getNotificationDao(
        localDatabase: LocalDatabase
    ): NotificationDao = localDatabase.notificationDao()


    //    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL("ALTER TABLE events ADD COLUMN repeat_interval INTEGER NOT NULL DEFAULT 0")
//            }
//        }
//    }
//
//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL(
//                    "CREATE TABLE IF NOT EXISTS `last_update_date`(" +
//                            "`id_notification` INTEGER NOT NULL , " +
//                            "`company_id` INTEGER NOT NULL , " +
//                            "`date` TEXT NOT NULL, PRIMARY KEY(`id_notification`))"
//                )
//            }
//        }
//    }
//    private val MIGRATION_2_3 = object : Migration(2, 3) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL("ALTER TABLE events ADD COLUMN repetition_exception TEXT NOT NULL Default ''")
//            }
//        }
//    }
//    private val MIGRATION_3_4 = object : Migration(3, 4) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL("ALTER TABLE events ADD COLUMN repeat TEXT NOT NULL Default ''")
//            }
//        }
//    }
//
//    private val MIGRATION_4_5 = object : Migration(4, 5) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL("ALTER TABLE events ADD COLUMN company_id INT NOT NULL Default 0")
//            }
//        }
//    }
//
//    private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("DROP TABLE local_date")
//        }
//    }
//    private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL("ALTER TABLE events ADD COLUMN reminder INT NOT NULL Default 0")
//            }
//        }
//    }
//    private val MIGRATION_7_8: Migration = object : Migration(7, 8) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.apply {
//                execSQL("ALTER TABLE events ADD COLUMN until_date TEXT NOT NULL DEFAULT ''")
//            }
//        }
//    }
}