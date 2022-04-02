package uz.perfectalgorithm.projects.tezkor.domain.home.idea_comment

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.*

/**
 * Created by Jasurbek Kurganbaev on 04.08.2021 13:42
 **/
interface IdeaCommentRepository {

    val emptyCommentsListener: ((Boolean) -> Unit)?


    fun postComment(data: PostCommentBody): Flow<Result<PostCommentResponse>>

//    fun getCommentList(data: CommentListBody): Flow<Result<GetCommentListResponse>>

    fun getCommentList(scope: CoroutineScope): Flow<PagingData<CommentData>>
}