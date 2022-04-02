package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.group

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.group_member_role.GroupMemberRoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemMembersGroupBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Raximjanov Davronbek on 07.10.2021 16:44
 **/

class GroupChatMembersAdapter :
    RecyclerView.Adapter<GroupChatMembersAdapter.VH>() {

    private var myList: ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem> =
        ArrayList()

    private var listenClick: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>? = null
    private var listenCheck: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>? = null

    inner class VH(val binding: ItemMembersGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {

            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                data?.let { data ->
                    txtFullName.text = data.fullName
                    if (data.image?.isNotEmpty() == true) {
                        imgPerson.loadImageUrlUniversal(data.image, R.drawable.ic_user)
                    } else {
                        imgPerson.setImageResource(R.drawable.ic_user)
                    }
                    if (data.role != GroupMemberRoleEnum.MEMBER.text) {
                        when (data.role) {
                            GroupMemberRoleEnum.CREATOR.text -> {
                                txtRole.text = App.instance.getString(R.string.creator__chat)
                            }
                            GroupMemberRoleEnum.ADMIN.text -> {
                                txtRole.text = App.instance.getString(R.string.admin__chat)
                            }
                        }
                        txtRole.visible()
                    } else {
                        txtRole.gone()
                    }
                }

                cons1.setOnClickListener {
                    listenClick?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMembersGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        listenClick = block
    }

    fun setOnCheckListener(block: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        listenCheck = block
    }

    override fun getItemCount() = myList.size

    fun getItem(position: Int) = myList[position]

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        myList.clear()
        myList.addAll(list)
        notifyDataSetChanged()
    }

}