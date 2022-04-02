package uz.perfectalgorithm.projects.tezkor.data.sources.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.NotificationApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse
import uz.perfectalgorithm.projects.tezkor.utils.DEFAULT_PAGE_INDEX
import java.io.IOException

class NotificationPagingSource(private val notificationApi: NotificationApi) :
    PagingSource<Int, NotificationResponse.NotificationData>() {

    override fun getRefreshKey(state: PagingState<Int, NotificationResponse.NotificationData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationResponse.NotificationData> {
        val page = params.key ?: DEFAULT_PAGE_INDEX
        return try {
            val response =
                notificationApi.getNotification(page, 15).data
            LoadResult.Page(
                response.notificationsList,
                prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                nextKey = if (response.pagesCount == page) null else page
                        + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

}