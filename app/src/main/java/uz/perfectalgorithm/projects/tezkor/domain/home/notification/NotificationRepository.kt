package uz.perfectalgorithm.projects.tezkor.domain.home.notification

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationCountResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse

interface NotificationRepository {

    fun getNotification(): Flow<PagingData<NotificationResponse.NotificationData>>

    fun getNotificationCount(): Flow<Result<NotificationCountResponse>>

}