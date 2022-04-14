package uz.perfectalgorithm.projects.tezkor.domain.home.idea_comment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.datasource.comment.CommentDataSource
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.IdeaCommentApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.*
import javax.inject.Inject


class IdeaCommentRepositoryImpl @Inject constructor(
    private val ideaCommentApi: IdeaCommentApi,
    private val storage: LocalStorage
) : IdeaCommentRepository {

    private val config = PagingConfig(pageSize = 10)


    override var emptyCommentsListener: ((Boolean) -> Unit)? = null


    override fun postComment(data: PostCommentBody): Flow<Result<PostCommentResponse>> = flow {
        try {
            val response = ideaCommentApi.postComment(data)
            if (response.code() == 201) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<PostCommentResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else emit(Result.failure<PostCommentResponse>(HttpException(response)))
        } catch (e: Exception) {
            emit(Result.failure<PostCommentResponse>(e))
        }
    }


    /*    override fun getCommentList(data: CommentListBody): Flow<Result<GetCommentListResponse>> =
        flow {
            try {
                val response = ideaCommentApi.getCommentList(data)
                if (response.code() == 200) response.body()?.let {
                    if (it.data != null) {
                        emit(Result.success(it))
                    } else {
                        emit(
                            Result.failure<GetCommentListResponse>(
                                Exception(
                                    App.instance.resources.getString(
                                        R.string.error
                                    )
                                )
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                emit(Result.failure<GetCommentListResponse>(e))
            }
        }*/

    /*override fun getCommentList(scope: CoroutineScope): Flow<PagingData<CommentData>> {
        Pager(config) {
            CommentDataSource(ideaCommentApi, storage).apply {
                emptyFun { emptyCommentsListener?.invoke(it) }
            }
        }.flow.cachedIn(scope)

    }*/

    override fun getCommentList(
        scope: CoroutineScope,
    ): Flow<PagingData<CommentData>> =
        Pager(config) {
            CommentDataSource(ideaCommentApi, storage).apply {
                emptyFun { emptyCommentsListener?.invoke(it) }
            }
        }.flow.cachedIn(scope)

}