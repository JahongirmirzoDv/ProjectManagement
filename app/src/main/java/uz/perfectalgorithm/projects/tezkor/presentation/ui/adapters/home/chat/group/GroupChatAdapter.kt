package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.MESSAGE_TYPE_SERVICE
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.inVisible
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Davronbek Raximjanov on 19.07.2021 11:56
 **/

class GroupChatAdapter(private val userId: Int) :
    RecyclerView.Adapter<GroupChatAdapter.VH>() {

    private val mList = ArrayList<ChatGroupListResponse.GroupChatDataItem>()

    private var listenerCallback: SingleBlock<ChatGroupListResponse.GroupChatDataItem>? = null
    private var listenDeleteClick: SingleBlock<ChatGroupListResponse.GroupChatDataItem>? = null


    inner class VH(private val binding: ItemGroupChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            binding.apply {
                val data = mList[absoluteAdapterPosition]
                txtTitle.text = data.title

                if (data.image != null || data.image == "") {
                    txtDefaultImg.gone()
                    imgGroup.visible()
                    data.image?.let { imgGroup.loadImageUrlUniversal(it, R.drawable.ic_user) }
                } else {
                    txtDefaultImg.visible()
                    txtDefaultImg.text = data.title.toString().trim()?.get(0).toString()
                    imgGroup.gone()
                }

                if (data.lastMessage?.messageType == MESSAGE_TYPE_SERVICE) {
                    txtLastMessageSender.text = ""
                    txtLastMessage.text =
                        "${data.lastMessage.creator?.fullName} ${App.instance.getString(R.string.create_msg_group_chat_title)}"

                } else {
                    if (data.lastMessage?.creator?.id == userId) {
                        txtLastMessageSender.text = App.instance.getString(R.string.i_am)
                    } else {
                        txtLastMessageSender.text = data.lastMessage?.creator?.fullName
                    }

                    txtLastMessage.text = data.lastMessage?.message
                }
                data.unreadMessages?.let {
                    if (data.unreadMessages > 0) {
                        txtUnreadCount.text = data.unreadMessages.toString()
                    } else {
                        txtUnreadCount.inVisible()
                    }
                }

                if (data.lastMessage?.creator?.id != userId) {
                    if (data.unreadMessages!! > 0) {
                        txtUnreadCount.visible()
                        txtUnreadCount.text = data.unreadMessages.toString()
                    } else {
                        txtUnreadCount.gone()
                    }
                    imgReadUnread.gone()

                } else {
                    imgReadUnread.visible()
                    txtUnreadCount.gone()

                    if (data.lastMessage.isRead == true)
                        imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                    else {
                        imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                    }
                }

                frontLayout.setOnClickListener {
                    listenerCallback?.invoke(data)
                }

                foregroundLayout.setOnClickListener {
                    listenDeleteClick?.invoke(data)
                    rootSwRl.close(false)
                }


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGroupChatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnclickListener(block: SingleBlock<ChatGroupListResponse.GroupChatDataItem>) {
        listenerCallback = block
    }

    fun setOnDeleteClickListener(block: SingleBlock<ChatGroupListResponse.GroupChatDataItem>) {
        listenDeleteClick = block
    }

    fun submitList(list: List<ChatGroupListResponse.GroupChatDataItem>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = mList.size

}