package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/*data class TaskCommentData(

	@field:SerializedName("TaskCommentData")
	val taskCommentData: List<TaskCommentDataItem?>? = null
)*/

data class TaskCommentDataItem(

	@field:SerializedName("task")
	val task: Int? = null,

	@field:SerializedName("replied_comments")
	val repliedComments: List<Any>? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("avatar")
	val avatar: String? = null,

	@field:SerializedName("commenter")
	val commenter: String? = null
)
data class TaskCommentPagingData(
    val count:Int,
    val results:List<TaskCommentData>
)
@Parcelize
data class TaskCommentData(

    @field:SerializedName("task")
    val task: Int? = null,

    @field:SerializedName("replied_comments")
    val repliedComments: List<TaskCommentData>? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("avatar")
    val avatar: String? = null,

    @field:SerializedName("commenter")
    val commenter: String? = null
) : Parcelable
