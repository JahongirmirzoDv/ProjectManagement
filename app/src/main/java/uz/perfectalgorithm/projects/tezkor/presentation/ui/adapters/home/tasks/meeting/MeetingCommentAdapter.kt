package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.databinding.ItemMeetingCommentBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemMeetingProblemBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.inVisible


class MeetingCommentAdapter(
//    private val onDeleteLocalItem: SingleBlock<DiscussedTopic>,
    private val onMoreClick: SingleBlock<MeetingComment>,
//    private var isEditorMode: Boolean = true
) : ListAdapter<MeetingComment, MeetingCommentAdapter.VH>(ITEM_CALLBACK) {

//    fun changeMode(isEditorMode: Boolean) {
//        this.isEditorMode = isEditorMode
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMeetingCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemMeetingCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivMore.setOnClickListener {
//                if (isEditorMode) {
//                    onDeleteLocalItem(getItem(bindingAdapterPosition))
//                } else {
                    onMoreClick?.invoke(getItem(bindingAdapterPosition))
//                }
            }
        }

        fun bind(topic: MeetingComment) = with(binding) {
//            if (isEditorMode) {
//                ivMore.setImageResource(R.drawable.ic_add_plus)
//                ivMore.rotation = 45f
//            } else {
//                ivMore.setImageResource(R.drawable.ic_more_meeting_problem)
//                ivMore.rotation = 0f
//            }
            tvTitle.text = topic.text

        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<MeetingComment>() {
            override fun areItemsTheSame(
                oldItem: MeetingComment,
                newItem: MeetingComment
            ) = oldItem.id == newItem.id && oldItem.text == newItem.text

            override fun areContentsTheSame(
                oldItem: MeetingComment,
                newItem: MeetingComment
            ) = oldItem == newItem
        }
    }
}