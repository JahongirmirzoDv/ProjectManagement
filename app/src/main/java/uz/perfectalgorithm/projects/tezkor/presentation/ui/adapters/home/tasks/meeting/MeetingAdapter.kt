package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingListItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemMeetingTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toMeetingDate

/**
 *Created by farrukh_kh on 7/24/21 10:55 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.tasks.meeting
 **/
class MeetingAdapter(private val onItemClick: SingleBlock<Int>) :
    ListAdapter<MeetingListItem, MeetingAdapter.VH>(ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMeetingTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemMeetingTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meeting: MeetingListItem) = with(binding) {
            root.setOnClickListener {
                onItemClick(meeting.id)
            }
            tvId.text = "Majlis #${meeting.id}"
            tvAddress.text = meeting.address
            tvLeaderValue.text = meeting.creator
            tvParticipantsValue.text = meeting.membersCount.toString()
            tvTitle.text = meeting.title
            tvTime.text = meeting.startTime.toMeetingDate()
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<MeetingListItem>() {
            override fun areItemsTheSame(oldItem: MeetingListItem, newItem: MeetingListItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MeetingListItem, newItem: MeetingListItem) =
                oldItem == newItem

        }
    }
}