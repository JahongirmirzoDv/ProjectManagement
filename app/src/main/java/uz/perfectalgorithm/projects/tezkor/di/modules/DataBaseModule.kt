package uz.perfectalgorithm.projects.tezkor.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.ConversationChatDatabase
import uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.dao.MessageModelDao
import javax.inject.Singleton

/**
 * Created by Davronbek Raximjanov on 16.07.2021
 **/

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Provides
    @Singleton
    fun getDataBase(): ConversationChatDatabase = ConversationChatDatabase.getDatabase()

    @Provides
    @Singleton
    fun getMessageModelDao(appDatabase: ConversationChatDatabase): MessageModelDao =
        appDatabase.messageModelDao()

}