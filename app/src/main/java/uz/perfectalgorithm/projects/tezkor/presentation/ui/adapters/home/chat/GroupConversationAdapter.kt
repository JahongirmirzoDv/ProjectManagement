package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.databinding.*
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil.getDateDay
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil.getTimeFormatFromDate
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.ThreeBlock
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.MESSAGE_TYPE_SERVICE
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUri
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlN
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD

class GroupConversationAdapter(
    private val userId: Int,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val storage by lazy { LocalStorage.instance }

    var messages: MutableList<GroupChatSendMessageResponse.MessageDataItem?> =
        mutableListOf()
    private var oldMessageId = 0

    private var listenClick: ThreeBlock<GroupChatSendMessageResponse.MessageDataItem?, Int, View>? =
        null

    private var listenClickOpenImage: SingleBlock<GroupChatSendMessageResponse.MessageDataItem>? =
        null

    private var listenGoToPosition: DoubleBlock<Int, Int>? =
        null

    private var listenGoToNullPosition: SingleBlock<Int>? = null

    private var listenUpdateTaskIsDone: DoubleBlock<Int, Boolean>? =
        null

    private var listenGetNextData: SingleBlock<Int>? = null

    private var listenVisibleToBottom: SingleBlock<Int>? = null


    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.messageType == MESSAGE_TYPE_SERVICE) {
            0
        } else if ((getItem(position)?.task?.id != null || getItem(position)?.meeting?.id != null|| getItem(position)?.note?.id != null) && getItem(position)?.creator?.id == userId!!) {
            3
        } else if ((getItem(position)?.task?.id != null || getItem(position)?.meeting?.id != null|| getItem(position)?.note?.id != null) && getItem(position)?.creator?.id != userId) {
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
                val binding = ItemChatReceivedMsgGroupBinding.inflate(
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
                val binding = ItemChatReceivedImgMsgGroupBinding.inflate(
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
            val viewholder = holder as GroupConversationAdapter.ChatOtherMessageHolder
            viewholder.bind(position)
        } else if ((getItem(position)?.task?.id != null || getItem(position)?.meeting?.id != null|| getItem(position)?.note?.id != null) && userId == getItem(position)?.creator?.id) {
            val viewholder = holder as GroupConversationAdapter.ChatSendTaskMessageHolder
            viewholder.bind(position)
        } else if ((getItem(position)?.task?.id != null || getItem(position)?.meeting?.id != null|| getItem(position)?.note?.id != null) && userId != getItem(position)?.creator?.id) {
            val viewholder = holder as GroupConversationAdapter.ChatReceivedTaskMessageHolder
            viewholder.bind(position)
        } else if (getItem(position)?.files?.isNotEmpty() == true && getItem(position)?.creator?.id == userId || getItem(
                position
            )?.localImgUri != null && getItem(position)?.creator?.id == userId
        ) {
            val viewholder = holder as GroupConversationAdapter.ChatSentImgMessageHolder
            viewholder.bind(position)
        } else if (getItem(position)?.files?.isNotEmpty() == true && getItem(position)?.creator?.id != userId || getItem(
                position
            )?.localImgUri != null && getItem(position)?.creator?.id != userId
        ) {
            val viewholder = holder as GroupConversationAdapter.ChatReceivedImgMessageHolder
            viewholder.bind(position)
        } else {
            when (getItem(position)?.creator?.id) {
                userId -> {
                    val viewholder = holder as GroupConversationAdapter.ChatSentMessageHolder
                    viewholder.bind(position)
                }
                else -> {
                    val viewholder =
                        holder as GroupConversationAdapter.ChatReceivedMessageHolder
                    viewholder.bind(position)
                }
            }
        }
    }

    inner class ChatOtherMessageHolder(val binding: ItemChatOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)

            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else {}

            listenVisibleToBottom?.invoke(position)




            binding.apply {
                txtDate.text = messageData?.timeCreated
                txtMessage.text =
                    "${messageData?.creator?.fullName} ${App.instance.getString(R.string.create_msg_chat_title)}"
            }
        }
    }

    inner class ChatSentMessageHolder(val binding: ItemChatSendedMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else{

            }

            listenVisibleToBottom?.invoke(position)

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

                    txtReplySender.text = messageData?.answerFor.creator?.fullName.toString()

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
            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else {}

            listenVisibleToBottom?.invoke(position)

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

                    txtReplySender.text = messageData?.answerFor.creator?.fullName.toString()


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
                        messageData?.let { it1 -> listenClickOpenImage?.invoke(it1) }
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

    inner class ChatReceivedMessageHolder(val binding: ItemChatReceivedMsgGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else {}

            listenVisibleToBottom?.invoke(position)

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

                if (messageData?.creator?.id == getItem(absoluteAdapterPosition + 1)?.creator?.id) {
                    txtSenderFullName.gone()
                } else {
                    txtSenderFullName.visible()
                    txtSenderFullName.text = messageData?.creator?.fullName.toString()
                }

                if (messageData?.creator?.id == getItem(absoluteAdapterPosition - 1)?.creator?.id) {
                    cvSenderImg.inVisible()
                } else {
                    cvSenderImg.visible()
                    messageData?.creator?.image?.let {
                        imgSender.loadImageUrlUniversal(
                            it,
                            R.drawable.ic_user
                        )
                    }
                }

                if (messageData?.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData.answerFor.message

                    if (messageData.answerFor.files != null && !messageData.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                    txtReplySender.text = messageData?.answerFor.creator?.fullName.toString()


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

    inner class ChatReceivedImgMessageHolder(val binding: ItemChatReceivedImgMsgGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else {}

            listenVisibleToBottom?.invoke(position)

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

                if (messageData?.creator?.id == getItem(absoluteAdapterPosition - 1)?.creator?.id) {
                    cvSenderImg.inVisible()
                } else {
                    cvSenderImg.visible()
                    messageData?.creator?.image?.let {
                        imgSender.loadImageUrlUniversal(
                            it,
                            R.drawable.ic_user
                        )
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
                    txtReplySender.text = messageData?.answerFor.creator?.fullName.toString()

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
                        messageData?.let { it1 -> listenClickOpenImage?.invoke(it1) }
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
            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else {}

            listenVisibleToBottom?.invoke(position)

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

                if (messageData?.meeting?.id != null) {
                    cvRoot.setCardBackgroundColor(Color.parseColor("#364F6B"))
                    txtStatus.text = (messageData.meeting.status?:"not_started").translateStatusMeeting(storage.lan)
                    txtStatus.setTextColor(Color.WHITE)
                    txtTaskTitle.setTextColor(Color.WHITE)
                    txtTime.setTextColor(Color.WHITE)
                    imgReadUnread.setTint(Color.WHITE)
                } else if (messageData?.note?.id != null) {
                    cvRoot.setCardBackgroundColor(Color.parseColor("#BE9460"))
                    txtStatus.text = (messageData.note.status?:true).translateStatusNote(storage.lan)
                    txtStatus.setTextColor(Color.WHITE)
                    txtTaskTitle.setTextColor(Color.WHITE)
                    txtTime.setTextColor(Color.WHITE)
                    imgReadUnread.setTint(Color.WHITE)
                } else {
                    cvRoot.setCardBackgroundColor(Color.parseColor("#F3C966"))
                    txtStatus.text = (messageData?.task?.status?:"new").translateStatusTask(storage.lan)
                    txtStatus.setTextColor(Color.parseColor("#364F6B"))
                    txtTaskTitle.setTextColor(Color.parseColor("#364F6B"))
                    txtTime.setTextColor(Color.parseColor("#364F6B"))
                    imgReadUnread.setTint(Color.parseColor("#364F6B"))
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

    inner class ChatReceivedTaskMessageHolder(val binding: ItemChatReceivedTaskMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = getItem(absoluteAdapterPosition)
            if (position == (messages.size - 1) && messageData?.id != oldMessageId) {
                listenGetNextData?.invoke(200)
                oldMessageId = messageData?.id!!
            } else {}

            listenVisibleToBottom?.invoke(position)

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

                if (messageData?.meeting?.id != null) {
                    cvRoot.setCardBackgroundColor(Color.parseColor("#364F6B"))
                    txtStatus.text = (messageData.meeting.status?:"not_started").translateStatusMeeting(storage.lan)
                    txtStatus.setTextColor(Color.WHITE)
                    txtTaskTitle.setTextColor(Color.WHITE)
                    txtTime.setTextColor(Color.WHITE)
                    imgReadUnread.setTint(Color.WHITE)
                } else if (messageData?.note?.id != null) {
                    cvRoot.setCardBackgroundColor(Color.parseColor("#BE9460"))
                    txtStatus.text = (messageData.note.status?:true).translateStatusNote(storage.lan)
                    txtStatus.setTextColor(Color.WHITE)
                    txtTaskTitle.setTextColor(Color.WHITE)
                    txtTime.setTextColor(Color.WHITE)
                    imgReadUnread.setTint(Color.WHITE)
                } else {
                    cvRoot.setCardBackgroundColor(Color.parseColor("#F3C966"))
                    txtStatus.text = (messageData?.task?.status?:"new").translateStatusTask(storage.lan)
                    txtStatus.setTextColor(Color.parseColor("#364F6B"))
                    txtTaskTitle.setTextColor(Color.parseColor("#364F6B"))
                    txtTime.setTextColor(Color.parseColor("#364F6B"))
                    imgReadUnread.setTint(Color.parseColor("#364F6B"))
                }

                txtTaskTitle.text = messageData?.message

                txtTime.text = messageData?.timeCreated?.getTimeFormatFromDate()

                if (messageData?.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                }

                cvRoot.setOnLongClickListener {
                    listenClick?.invoke(messageData, position, cvRoot)
                    true
                }

            }
        }
    }


    fun getItem(position: Int): GroupChatSendMessageResponse.MessageDataItem? {
        return if (position < messages.size&&position>-1) {
            messages[position]
        } else {
            null
        }
    }


    /** OutSide Actions **/

    fun submitMessages(messages: List<GroupChatSendMessageResponse.MessageDataItem>) {
        this.messages =
            messages as MutableList<GroupChatSendMessageResponse.MessageDataItem?>
        notifyDataSetChanged()
    }

    fun sendMessage(message: GroupChatSendMessageResponse.MessageDataItem) {
        val tempList = ArrayList<GroupChatSendMessageResponse.MessageDataItem?>()
        tempList.addAll(messages)
        messages.clear()
        messages.add(message)
        messages.addAll(tempList)
        notifyDataSetChanged()
    }

    fun updateImgMessage(
        position: Int,
        messageDataItem: GroupChatSendMessageResponse.MessageDataItem,
        rv: RecyclerView
    ) {
        messages[position] = messageDataItem
        notifyDataSetChanged()
        rv.scrollToPosition(position)
    }

    fun updateMessage(
        position: Int,
        messageDataItem: GroupChatSendMessageResponse.MessageDataItem,
    ) {
        messages[position] = messageDataItem
        notifyDataSetChanged()
    }


    fun setOnclickListener(f: ThreeBlock<GroupChatSendMessageResponse.MessageDataItem?, Int, View>?) {
        listenClick = f
    }

    fun setGoToPositionListener(f: DoubleBlock<Int, Int>) {
        listenGoToPosition = f
    }

    fun getNextDataCallback(f: SingleBlock<Int>) {
        listenGetNextData = f
    }

    fun visibleToBottomCallback(f: SingleBlock<Int>) {
        listenVisibleToBottom = f
    }

    fun goToNullPositionCallback(f: SingleBlock<Int>) {
        listenGoToNullPosition = f
    }

    fun onOpenImageClickCallback(f: SingleBlock<GroupChatSendMessageResponse.MessageDataItem>) {
        listenClickOpenImage = f
    }

    fun updateTaskMessageIsDone(f: DoubleBlock<Int, Boolean>) {
        listenUpdateTaskIsDone = f
    }

}
