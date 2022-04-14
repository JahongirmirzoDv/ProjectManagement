package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.GetCommentListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.PostCommentBody
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.PostCommentResponse


interface IdeaCommentApi {

    @POST("/api/v1/idea/idea-comment/")
    suspend fun postComment(
        @Body data: PostCommentBody
    ): Response<PostCommentResponse>

    @GET("/api/v1/idea/idea-comment-list/{id}/{page}")
    suspend fun getCommentList(
        @Path("id") id: Int,
        @Path("page") page: Int,
//        @Body data: CommentListBody

    ): Response<GetCommentListResponse>
}