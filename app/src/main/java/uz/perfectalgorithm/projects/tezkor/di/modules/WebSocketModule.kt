package uz.perfectalgorithm.projects.tezkor.di.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.BASE_CHAT_URL
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.SOCKET
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.SOCKET_ALL
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Davronbek Raximjanov on 13-Apr-21
 **/

@Module
@InstallIn(SingletonComponent::class)
class WebSocketModule {

    @Provides
    @Singleton
    @Named(SOCKET)
    fun socketClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    @Provides
    @Named(SOCKET_ALL)
    fun socketAllRequest(localStorage: LocalStorage): Request =
        Request.Builder()
            .url("$BASE_CHAT_URL/api/v1/ws/consumer?token=${localStorage.token}")
            .build()

    /**
     * ws://68.183.222.195/api/v1/ws/consumer?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjM1NjgxNTg3LCJqdGkiOiJlYTE2ZWVmYjdlM2I0NDg2YmIxNThkMzdiZWUzYmJkZCIsInVzZXJfaWQiOjI0fQ.KQY5MJDZBS-ubdfNM0GVgPVcrJ6fcrDWbk1ZaloGzxk
     */


    @Provides
    @Singleton
    fun getGson(): Gson = Gson()

}