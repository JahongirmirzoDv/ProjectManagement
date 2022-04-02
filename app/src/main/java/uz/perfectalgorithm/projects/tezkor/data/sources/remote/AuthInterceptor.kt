package uz.perfectalgorithm.projects.tezkor.data.sources.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.AuthApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.RefreshTokenRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.RefreshTokenResponse
import javax.inject.Inject
import javax.inject.Provider

class AuthInterceptor @Inject constructor(
    private val authApi: Provider<AuthApi>,
    private val storage: LocalStorage
) : Authenticator {
    private var flag = true
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            if (flag) {
                flag = false
                val updateToken = getUpdateToken()
                if (updateToken.isSuccessful) {
                    val newToken = updateToken.body()!!.access
                    storage.token = newToken
                    flag = true
                    response.request.newBuilder().removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newToken").build()
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private suspend fun getUpdateToken(): retrofit2.Response<RefreshTokenResponse> =
        authApi.get().refreshToken(RefreshTokenRequest(storage.refreshToken))

//    private suspend fun getUpdateToken(): retrofit2.Response<RefreshTokenResponse>? {
//        return try {
//            val response = authApi.get().refreshToken(RefreshTokenRequest(storage.refreshToken))
//            if (response.code() == 401) {
//                null
//            } else {
//                response
//            }
//        } catch (e: IOException) {
//            null
//        }
//    }
}