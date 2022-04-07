package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationCountResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse

/***
 * Bu company qismi uchun api
 */

interface NotificationApi {

    @GET("/api/v1/company/notifications/")
    suspend fun getNotification(
        @Query("page") page: Int,
        @Query("limit") size: Int
    ): NotificationResponse.Result


    @GET("/api/v1/company/notifications-count/")
    suspend fun getNotificationCount(
        @Query("date") date: String,
    ): Response<NotificationCountResponse>


}