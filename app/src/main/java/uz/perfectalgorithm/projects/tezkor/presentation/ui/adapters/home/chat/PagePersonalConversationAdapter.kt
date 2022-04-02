/*
package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.*
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil.getDateDay
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil.getTimeFormatFromDate
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.ThreeBlock
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.MESSAGE_TYPE_SERVICE
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUri
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlN
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible

*/
/**
 * Created by Davronbek Raximjanov on 25.09.2021 21:43
 **//*


class PagePersonalConversationAdapter(
    private val userId: Int,
    private val userName: String,
    private val receiverName: String
) : PagingDataAdapter<PersonalChatMessageListResponse.MessageDataItem, RecyclerView.ViewHolder>(
    IDEA_COMMENT_ITEM_CALLBACK
) {

    private var listenClick: ThreeBlock<PersonalChatMessageListResponse.MessageDataItem?, Int, View>? =
        null

    private var listenClickOpenImage: SingleBlock<PersonalChatMessageListResponse.MessageDataItem?>? =
        null

    private var listenGoToPosition: DoubleBlock<Int, Int>? =
        null

    private var listenGoToNullPosition: SingleBlock<Int>? = null

    private var listenUpdateTaskIsDone: DoubleBlock<Int, Boolean>? =
        null

    companion object {
        var IDEA_COMMENT_ITEM_CALLBACK =
            object : DiffUtil.ItemCallback<PersonalChatMessageListResponse.MessageDataItem>() {
                override fun areItemsTheSame(
                    oldItem: PersonalChatMessageListResponse.MessageDataItem,
                    newItem: PersonalChatMessageListResponse.MessageDataItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: PersonalChatMessageListResponse.MessageDataItem,
                    newItem: PersonalChatMessageListResponse.MessageDataItem
                ): Boolean {
                    return newItem.id == oldItem.id &&
                            newItem.message == oldItem.message &&
                            newItem.messageType == oldItem.messageType &&
                            newItem.isRead == oldItem.isRead &&
                            newItem.creator == oldItem.creator
                }
            }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.messageType == MESSAGE_TYPE_SERVICE) {
            0
        } else if (getItem(position)?.task?.id != null && getItem(position)?.creator?.id == userId!!) {
            3
        } else if (getItem(position)?.task?.id != null && getItem(position)?.creator?.id != userId) {
            4
        } else if (getItem(position)?.files?.isNotEmpty() == true && getItem(position)?.creator?.id == userId || getItem(
                position
            )?.localImgUri != null && getItem(position)?.creator?.id == userId
        ) {
            5
        } else if (getItem(position)?.files?.isNotEmpty() == true && getItem(position)?.creator?.id != userId || getItem(
                position
            )?.localImgUri != null && getItem(position)?.creator?.id != userId
        ) {
            6
        } else when (getItem(position)?.creator?.id) {
            userId -> {
                1
            }
            else -> {
                2
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding = ItemChatOtherMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatOtherMessageHolder(binding)
            }
            1 -> {
                val binding = ItemChatSendedMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatSentMessageHolder(binding)
            }
            2 -> {
                val binding = ItemChatReceivedMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatReceivedMessageHolder(binding)
            }
            3 -> {
                val binding = ItemChatSendedTaskMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatSendTaskMessageHolder(binding)
            }
            4 -> {
                val binding = ItemChatReceivedTaskMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatReceivedTaskMessageHolder(binding)
            }
            5 -> {
                val binding = ItemChatSendedImgMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatSentImgMessageHolder(binding)
            }
            else -> {
                val binding = ItemChatReceivedImgMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ChatReceivedImgMessageHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position)?.messageType == MESSAGE_TYPE_SERVICE) {
            val viewholder = holder as PagePersonalConversationAdapter.ChatOtherMessageHolder
            viewholder.bind(position)
        } else if (getItem(position)?.task?.id != null && userId == getItem(position)?.creator?.id) {
            val viewholder = holder as PagePersonalConversationAdapter.ChatSendTaskMessageHolder
            viewholder.bind(position)
        } else if (getItem(position)?.task?.id != null && userId != getItem(position)?.creator?.id) {
            val viewholder = holder as PagePersonalConversationAdapter.ChatReceivedTaskMessageHolder
            viewholder.bind(position)
        } else if (getItem(position)?.files?.isNotEmpty() == true && getItem(position)?.creator?.id == userId || getItem(
                position
            )?.localImgUri != null && getItem(position)?.creator?.id == userId
        ) {
            val viewholder = holder as PagePersonalConversationAdapter.ChatSentImgMessageHolder
            viewholder.bind(position)
        } else if (getItem(position)?.files?.isNotEmpty() == true && getItem(position)?.creator?.id != userId || getItem(
                position
            )?.localImgUri != null && getItem(position)?.creator?.id != userId
        ) {
            val viewholder = holder as PagePersonalConversationAdapter.ChatReceivedImgMessageHolder
            viewholder.bind(position)
        } else {
            when (getItem(position)?.creator?.id) {
                userId -> {
                    val viewholder = holder as PagePersonalConversationAdapter.ChatSentMessageHolder
                    viewholder.bind(position)
                }
                else -> {
                    val viewholder = holder as PagePersonalConversationAdapter.ChatReceivedMessageHolder
                    viewholder.bind(position)
                }
            }
        }
    }

    inner class ChatOtherMessageHolder(val binding: ItemChatOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {
                txtDate.text = messageData?.timeCreated
                if (messageData?.creator?.id == userId) {
                    txtMessage.text =
                        "$userName ${App.instance.getString(R.string.create_msg_chat_title)}"
                } else {
                    txtMessage.text =
                        "$receiverName ${App.instance.getString(R.string.create_msg_chat_title)}"
                }
            }
        }
    }

    inner class ChatSentMessageHolder(val binding: ItemChatSendedMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {


                if (position != itemCount - 1) {
                    val dateNew = messageData?.timeCreated?.getDateDay()
                    val dateOld = getItem(position + 1)?.timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                txtMessage.text = messageData?.message

                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                }

                if (messageData?.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData?.answerFor.message

                   */
/* if (messageData?.answerFor.chatPersonalMessageCreator == userId) {
                        txtReplySender.text = userName
                    } else {
                        txtReplySender.text = receiverName
                    }*//*


                    if (messageData?.answerFor.files != null && !messageData?.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData?.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                } else {
                    llReply.gone()
                }

                llReply.setOnClickListener {
                    messageData?.answerFor?.id?.let { it1 ->
                        listenGoToPosition?.invoke(
                            it1,
                            position
                        )
                    }
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }
            }
        }
    }

    inner class ChatSentImgMessageHolder(val binding: ItemChatSendedImgMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {

                if (position != itemCount - 1) {
                    val dateNew = messageData?.timeCreated?.getDateDay()
                    val dateOld = getItem(position + 1)?.timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                txtMessage.text = messageData?.message
                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()
                txtTimeXy.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                    imgReadUnreadXy.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                    imgReadUnreadXy.setImageResource(R.drawable.ic_unread_message)
                }

                if (messageData?.message != null && messageData?.message.isNotEmpty()) {
                    llMsgText.visible()
                    llTimeXy.gone()
                } else {
                    llMsgText.gone()
                    llTimeXy.visible()
                }

                if (messageData?.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData?.answerFor.message

                 */
/*   if (messageData?.answerFor.chatPersonalMessageCreator == userId) {
                        txtReplySender.text = userName
                    } else {
                        txtReplySender.text = receiverName
                    }
*//*

                    if (messageData?.answerFor.files != null && !messageData?.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData?.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                } else {
                    llReply.gone()
                }

                try {
                    progressBar.visible()
                    imgSendPhoto.gone()
                    messageData?.localImgUri?.let {
                        imgLocalPhoto.loadImageUri(it)
                        viewTrs.visible()
                        listenGoToNullPosition?.invoke(0)
                    }

                    messageData?.files?.get(0)?.file?.let { imgUrl ->
                        imgSendPhoto.loadImageUrlN(imgUrl)
                        progressBar.gone()
                        imgSendPhoto.visible()
                        viewTrs.gone()
                    }

                    imgSendPhoto.setOnClickListener {
                        listenClickOpenImage?.invoke(messageData)
                    }
                    imgSendPhoto.setOnLongClickListener {
                        listenClick?.invoke(messageData, position, cvRoot)
                        true
                    }

                } catch (e: Exception) {
                    myLogD(e.toString())
                }

                llReply.setOnClickListener {
                    messageData?.answerFor?.id?.let { it1 ->
                        listenGoToPosition?.invoke(
                            it1,
                            position
                        )
                    }
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }
            }
        }
    }

    inner class ChatReceivedMessageHolder(val binding: ItemChatReceivedMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {

                if (position != itemCount - 1) {
                    val dateNew = messageData?.timeCreated?.getDateDay()
                    val dateOld = getItem(position + 1)?.timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                txtMessage.text = messageData?.message

                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData.answerFor.message

                    if (messageData.answerFor.files != null && !messageData.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                    if (messageData.answerFor.chatPersonalMessageCreator == userId) {
                        txtReplySender.text = userName
                    } else {
                        txtReplySender.text = receiverName
                    }

                } else {
                    llReply.gone()
                }

                llReply.setOnClickListener {
                    messageData?.answerFor?.id?.let { it1 ->
                        listenGoToPosition?.invoke(
                            it1,
                            position
                        )
                    }
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }
            }
        }
    }

    inner class ChatReceivedImgMessageHolder(val binding: ItemChatReceivedImgMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {

                if (position != itemCount - 1) {
                    val dateNew = messageData?.timeCreated?.getDateDay()
                    val dateOld = getItem(position + 1)?.timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                txtMessage.text = messageData?.message

                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()
                txtTimeXy.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.message != null && messageData?.message.isNotEmpty()) {
                    llMsgText.visible()
                    llTimeXy.gone()
                } else {
                    llMsgText.gone()
                    llTimeXy.visible()
                }

                if (messageData?.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData?.answerFor.message

                    if (messageData?.answerFor.files != null && !messageData?.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData?.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                    if (messageData?.answerFor.chatPersonalMessageCreator == userId) {
                        txtReplySender.text = userName
                    } else {
                        txtReplySender.text = receiverName
                    }

                } else {
                    llReply.gone()
                }

                try {
                    progressBar.visible()
                    messageData?.files?.get(0)?.file?.let { imgUrl ->
                        imgSendPhoto.loadImageUrlN(imgUrl)
                        progressBar.gone()
                        imgSendPhoto.visible()
                    }
                    imgSendPhoto.setOnClickListener {
                        listenClickOpenImage?.invoke(messageData)
                    }
                    imgSendPhoto.setOnLongClickListener {
                        listenClick?.invoke(messageData, position, cvRoot)
                        true
                    }


                } catch (e: Exception) {
                    myLogD(e.toString())
                }

                llReply.setOnClickListener {
                    messageData?.answerFor?.id?.let { it1 ->
                        listenGoToPosition?.invoke(
                            it1,
                            position
                        )
                    }
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }
            }
        }
    }

    inner class ChatSendTaskMessageHolder(val binding: ItemChatSendedTaskMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {

                if (position != itemCount - 1) {
                    val dateNew = messageData?.timeCreated?.getDateDay()
                    val dateOld = getItem(position + 1)?.timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                myLogD(
                    "message = ${messageData?.message}  id = ${messageData?.id}",
                    "TAG_M"
                )

                messageData?.task?.leaderImage?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }
                messageData?.task?.performerImage?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }

                txtTaskTitle.text = messageData?.task?.title

                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                }

                try {

                } catch (e: Exception) {
                    myLogD(e.toString())
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }
            }
        }
    }

    inner class ChatReceivedTaskMessageHolder(val binding: ItemChatReceivedTaskMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            binding.apply {

                if (position != itemCount - 1) {
                    val dateNew = messageData?.timeCreated?.getDateDay()
                    val dateOld = getItem(position + 1)?.timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                messageData?.task?.leaderImage?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }
                messageData?.task?.performerImage?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }

                txtTaskTitle.text = messageData?.message

                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                }

                try {

                } catch (e: Exception) {
                    myLogD(e.toString())
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }

            }
        }
    }

    fun setOnclickListener(f: ThreeBlock<PersonalChatMessageListResponse.MessageDataItem?, Int, View>?) {
        listenClick = f
    }

}
*/
