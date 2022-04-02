package uz.perfectalgorithm.projects.tezkor.data.sources.datasource.comment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.IdeaCommentApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.CommentData
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

/**
 * Created by Jasurbek Kurganbaev on 05.08.2021 11:48
 **/

class CommentDataSource(
    private val commentApi: IdeaCommentApi,
    private val storage: LocalStorage,
) : PagingSource<Int, CommentData>() {
    private var emptyListener: ((Boolean) -> Unit)? = null

    override fun getRefreshKey(state: PagingState<Int, CommentData>): Int? {
        timberLog("getRefreshKey = ${state.anchorPosition}")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentData> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = commentApi.getCommentList(
                page = nextPageNumber,
                id = storage.ideaId,
            )
            emptyListener?.invoke(response.body()?.data?.commentsList!!.isEmpty())
            LoadResult.Page(
                data = response.body()?.data?.commentsList!!,
                prevKey = if (nextPageNumber > 1) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.body()?.data?.allCommentsCount!!)
                    nextPageNumber + 1 else null
            
            )
        } catch (e: Exception) {
            LoadResult.Error(Exception(App.instance.resources.getString(R.string.connection_retry)))
        }
    }

    fun emptyFun(f: (Boolean) -> Unit) {
        emptyListener = f
    }
}