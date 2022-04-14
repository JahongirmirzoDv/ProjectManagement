package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.dating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingListItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemDatingTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toMeetingDate


class DatingAdapter(private val onItemClick: SingleBlock<Int>) :
    ListAdapter<DatingListItem, DatingAdapter.VH>(ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemDatingTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemDatingTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dating: DatingListItem) = with(binding) {
            root.setOnClickListener {
                onItemClick(dating.id)
            }
            tvId.text = "${R.string.meeting_text} #${dating.id}"
            tvAddress.text = dating.address
            tvPersonValue.text = dating.partnerIn ?: dating.partnerOut ?: "${R.string.unknown}"
            tvTitle.text = dating.title
            tvTime.text = dating.startTime.toMeetingDate()
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DatingListItem>() {
            override fun areItemsTheSame(oldItem: DatingListItem, newItem: DatingListItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DatingListItem, newItem: DatingListItem) =
                oldItem == newItem
        }
    }
}