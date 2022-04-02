package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.CauseCommentsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemCauseCommentBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadPictureUrl

class CauseCommentAdapter(private val context: Context) :
    ListAdapter<CauseCommentsResponse, CauseCommentAdapter.VH>(diffUtil) {


    private val inflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        ItemCauseCommentBinding.inflate(inflater, parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = currentList[position]

        holder.itemCauseCommentBinding.txtMessageContent.text = itemData.comment
        holder.itemCauseCommentBinding.txtWriterName.text =
            "${itemData.lastName} ${itemData.firstName}"

        holder.itemCauseCommentBinding.txtMessageTime.text =
            itemData.createdAt

        itemData.creatorImage?.let { holder.itemCauseCommentBinding.imgPersonPhoto.loadPictureUrl(it) }
    }

    class VH(val itemCauseCommentBinding: ItemCauseCommentBinding) :
        RecyclerView.ViewHolder(itemCauseCommentBinding.root)

}

private val diffUtil = object : DiffUtil.ItemCallback<CauseCommentsResponse>() {
    override fun areItemsTheSame(
        oldItem: CauseCommentsResponse,
        newItem: CauseCommentsResponse,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: CauseCommentsResponse,
        newItem: CauseCommentsResponse,
    ): Boolean = oldItem == newItem

}
