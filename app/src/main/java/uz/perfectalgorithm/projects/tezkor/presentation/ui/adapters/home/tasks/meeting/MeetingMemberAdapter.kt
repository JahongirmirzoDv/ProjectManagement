package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.databinding.ItemMeetingMemberBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone


class MeetingMemberAdapter(
    private val onDescriptionClick: SingleBlock<MeetingMember>
) : ListAdapter<MeetingMember, MeetingMemberAdapter.VH>(ITEM_CALLBACK) {

    var moderatorId: ArrayList<Int> = arrayListOf()
    var listener: Listener? = null
    var lastChangeCheckPosition = -1
    var isCanChange = false

    fun changeModerator(id: Int, position: Int, isAdded: Boolean) {
        if (isAdded) moderatorId.add(id)
        else moderatorId.remove(id)
        notifyItemRangeChanged(position, itemCount)
    }

    fun changeCheck(check: Boolean, memberID: Int) {
        val item = getItem(lastChangeCheckPosition)
        if (lastChangeCheckPosition > -1 && item.id == memberID) {
            if (check) {
                item.isCheckedMember = check
            } else {
                item.isCheckedMember = check
            }
            notifyItemChanged(lastChangeCheckPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMeetingMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemMeetingMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnLongClickListener {
                val item = getItem(bindingAdapterPosition)
                listener?.onItemLongClick(
                    item,
                    item.user?.id?:0,
                    moderatorId.contains(item.user?.id ),
                    bindingAdapterPosition
                )
                true
            }
        }

        fun bind(member: MeetingMember) = with(binding) {
            tvName.text = member.user?.fullName
            member.user?.image?.let { ivProfile.loadImageUrl(it) }
            ivState.setImageResource(member.getStateIcon())
            cvDescription.isVisible = member.state == "rejected"
            cvDescription.setOnClickListener {
                onDescriptionClick(member)
            }
            cvUser.apply {
                setCardBackgroundColor(member.getBgColor())
                strokeColor = member.getStrokeColor()
            }
            if (isCanChange) {
                checkedMembers.visibility = View.VISIBLE
            } else {
                checkedMembers.visibility = View.GONE
            }

            checkedMembers.isChecked = member.isCheckedMember

            checkedMembers.setOnCheckedChangeListener { buttonView, isChecked ->
                lastChangeCheckPosition = absoluteAdapterPosition
                listener?.onItemClick(member.id?:-1, absoluteAdapterPosition, isChecked)
            }

            if (moderatorId.contains(member.user?.id)) {
                tvModerator.show()
            } else {
                tvModerator.gone()
            }
            tvModerator.setTextColor(member.getStrokeColor())
        }
    }

    interface Listener {
        fun onItemLongClick(member: MeetingMember,userId:Int, isModerator: Boolean, position: Int)
        fun onItemClick(userId:Int, position: Int, isChecked: Boolean)
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<MeetingMember>() {
            override fun areItemsTheSame(oldItem: MeetingMember, newItem: MeetingMember) =
                oldItem.user?.id == newItem.user?.id

            override fun areContentsTheSame(oldItem: MeetingMember, newItem: MeetingMember) =
                oldItem == newItem
        }
    }
}