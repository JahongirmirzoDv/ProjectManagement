package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemUserDataBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone


class UserDataAdapter(
    private val onDeleteClick: SingleBlock<PersonData>? = null
) : ListAdapter<PersonData, UserDataAdapter.VH>(ITEM_CALLBACK) {

    var isEditorMode = false
    var isCheckedMode = false
    var listener: Listener? = null
    var moderatorId: ArrayList<Int> = arrayListOf()
    var oldModeratorPosition: Int = -1
    var lastChangeCheckPosition = -1

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
        ItemUserDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemUserDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgDelete.setOnClickListener {
                if (bindingAdapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                if (isEditorMode) {
                    onDeleteClick?.invoke(getItem(bindingAdapterPosition))
                }
            }

            if (isCheckedMode) {
                binding.checkedMembers.visibility = View.VISIBLE
            } else {
                binding.checkedMembers.visibility = View.GONE
            }

            binding.checkedMembers.setOnCheckedChangeListener { buttonView, isChecked ->
                lastChangeCheckPosition = absoluteAdapterPosition
                listener?.onItemClick(getItem(absoluteAdapterPosition).id?:-1, absoluteAdapterPosition, isChecked)
            }

            binding.rootL.setOnLongClickListener {
                if (bindingAdapterPosition == RecyclerView.NO_POSITION) return@setOnLongClickListener true
                val item = getItem(bindingAdapterPosition)
                listener?.itemLongClick(item,item.id, moderatorId.contains(item.id), bindingAdapterPosition)
                true
            }
        }

        fun bind(person: PersonData) = with(binding) {
            if (moderatorId.contains(person.id)) tvModerator.show()
            else tvModerator.gone()
            tvPersonName.text = person.fullName
            person.image?.let { participantImageAvatar.loadImageUrl(it) }

        }
    }

    interface Listener {
        fun itemLongClick(data: PersonData,userId:Int, isModerator: Boolean, position: Int)
        fun onItemClick(userId:Int, position: Int, isChecked: Boolean)
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<PersonData>() {
            override fun areItemsTheSame(oldItem: PersonData, newItem: PersonData) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PersonData, newItem: PersonData) =
                oldItem == newItem

        }
    }
}