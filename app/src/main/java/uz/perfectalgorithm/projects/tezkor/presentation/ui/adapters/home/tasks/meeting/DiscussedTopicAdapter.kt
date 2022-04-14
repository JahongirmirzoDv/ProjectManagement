package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.databinding.ItemMeetingProblemBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.inVisible


class DiscussedTopicAdapter(
//    private val onDeleteLocalItem: SingleBlock<DiscussedTopic>,
    private val onMoreClick: SingleBlock<DiscussedTopic>,
//    private var isEditorMode: Boolean = true
) : ListAdapter<DiscussedTopic, DiscussedTopicAdapter.VH>(ITEM_CALLBACK) {

//    fun changeMode(isEditorMode: Boolean) {
//        this.isEditorMode = isEditorMode
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMeetingProblemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemMeetingProblemBinding) :
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

        fun bind(topic: DiscussedTopic) = with(binding) {
//            if (isEditorMode) {
//                ivMore.setImageResource(R.drawable.ic_add_plus)
//                ivMore.rotation = 45f
//            } else {
//                ivMore.setImageResource(R.drawable.ic_more_meeting_problem)
//                ivMore.rotation = 0f
//            }
            tvTitle.text = topic.title
            if (topic.project != null || topic.task != null){
                ivMore.inVisible()
                tvType.show()
                cardOwner.show()
                cardPerformer.show()
                ivArrow.show()
            }else{
                ivMore.show()
                tvType.gone()
                cardOwner.gone()
                cardPerformer.gone()
                ivArrow.gone()
            }
            topic.project?.let {
                tvType.setText("Proyekt")
                when {
                    it.creator?.image != null -> imgPersonOwner.loadImageUrl(
                        it.creator.image,
                        R.drawable.ic_person
                    )
                    it.creator != null -> imgPersonOwner.setImageResource(R.drawable.ic_person)
                }
                when {
                    it.performer?.image != null -> imgPersonPerformer.loadImageUrl(
                        it.performer.image,
                        R.drawable.ic_person
                    )
                    it.performer != null -> imgPersonPerformer.setImageResource(R.drawable.ic_person)
                }

            }
            topic.task?.let {
                tvType.text = "Vazifa"
                when {
                    it.creator?.image != null -> imgPersonOwner.loadImageUrl(
                        it.creator.image,
                        R.drawable.ic_person
                    )
                    it.creator != null -> imgPersonOwner.setImageResource(R.drawable.ic_person)
                }
                when {
                    it.performer?.image != null -> imgPersonPerformer.loadImageUrl(
                        it.performer.image,
                        R.drawable.ic_person
                    )
                    it.performer != null -> imgPersonPerformer.setImageResource(R.drawable.ic_person)
                }
            }
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DiscussedTopic>() {
            override fun areItemsTheSame(
                oldItem: DiscussedTopic,
                newItem: DiscussedTopic
            ) = oldItem.id == newItem.id && oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: DiscussedTopic,
                newItem: DiscussedTopic
            ) = oldItem == newItem
        }
    }
}