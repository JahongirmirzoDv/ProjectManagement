package uz.perfectalgorithm.projects.tezkor.domain.home.notification

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local.notification.LastUpdateDate
import uz.perfectalgorithm.projects.tezkor.data.sources.local.notification.NotificationDao
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.NotificationApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationCountResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.source.NotificationPagingSource
import uz.perfectalgorithm.projects.tezkor.utils.DEFAULT_PAGE_SIZE
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi,
    private val storage: LocalStorage,
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getNotification(): Flow<PagingData<NotificationResponse.NotificationData>> {
        notificationDao.setDate(
            LastUpdateDate(
                companyId = storage.selectedCompanyId,
                date = DateTime.now().toString(Formatter.NOTIFICATION_PATTERN)
            )
        )
        return Pager(
            PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = { NotificationPagingSource(api) }).flow
    }

    override fun getNotificationCount(): Flow<Result<NotificationCountResponse>> =
        flow {
            try {
                val date = notificationDao.getDate(storage.selectedCompanyId)
                val response = api.getNotificationCount(date)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Result.success(it))
                    }
                } else {
                    emit(Result.failure<NotificationCountResponse>(HttpException(response)))
                }
            } catch (e: Exception) {
                emit(Result.failure<NotificationCountResponse>(e))
            }
        }
}
