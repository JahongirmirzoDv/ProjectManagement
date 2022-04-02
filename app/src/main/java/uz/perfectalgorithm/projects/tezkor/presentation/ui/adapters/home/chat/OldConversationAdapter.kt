/*
package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.*
import uz.perfectalgorithm.projects.tezkor.utils.CHAT_CONSTS.MESSAGE_TYPE_SERVICE_RESPONSE
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.ThreeBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUri
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlN
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible

class OldConversationAdapter(
    private val userId: Int,
    private val userName: String,
    private val receiverName: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messages: MutableList<PersonalChatMessageListResponse.MessageDataItem> =
        mutableListOf()

    private var oldMessageId = 0

    private var listenClick: ThreeBlock<PersonalChatMessageListResponse.MessageDataItem, Int, View>? =
        null

    private var listenClickOpenImage: SingleBlock<PersonalChatMessageListResponse.MessageDataItem>? =
        null

    private var listenGoToPosition: DoubleBlock<Int, Int>? =
        null

    private var listenGoToNullPosition: SingleBlock<Int>? = null

    private var listenGetNextData: SingleBlock<Int>? = null

    private var listenUpdateTaskIsDone: DoubleBlock<Int, Boolean>? =
        null

    fun submitMessages(messages: List<PersonalChatMessageListResponse.MessageDataItem>) {
        this.messages =
            messages as MutableList<PersonalChatMessageListResponse.MessageDataItem>
        notifyDataSetChanged()
    }

    /////////////////////////////////////////////////////

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].messageType == MESSAGE_TYPE_SERVICE_RESPONSE) {
            0
        } else if (messages[position].shortTask?.id != null && messages[position].chatPersonalMessageCreator == userId) {
            3
        } else if (messages[position].shortTask?.id != null && messages[position].chatPersonalMessageCreator != userId) {
            4
        } else if (messages[position].files?.isNotEmpty() == true && messages[position].chatPersonalMessageCreator == userId || messages[position].localImgUri != null && messages[position].chatPersonalMessageCreator == userId) {
            5
        } else if (messages[position].files?.isNotEmpty() == true && messages[position].chatPersonalMessageCreator != userId || messages[position].localImgUri != null && messages[position].chatPersonalMessageCreator != userId) {
            6
        } else when (messages[position].chatPersonalMessageCreator) {
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

        if (messages[position].messageType == MESSAGE_TYPE_SERVICE_RESPONSE) {
            val viewholder = holder as ChatOtherMessageHolder
            viewholder.bind(position)
        } else if (messages[position].shortTask?.id != null && userId == messages[position].chatPersonalMessageCreator) {
            val viewholder = holder as ChatSendTaskMessageHolder
            viewholder.bind(position)
        } else if (messages[position].shortTask?.id != null && userId != messages[position].chatPersonalMessageCreator) {
            val viewholder = holder as ChatReceivedTaskMessageHolder
            viewholder.bind(position)
        } else if (messages[position].files?.isNotEmpty() == true && messages[position].chatPersonalMessageCreator == userId || messages[position].localImgUri != null && messages[position].chatPersonalMessageCreator == userId) {
            val viewholder = holder as ChatSentImgMessageHolder
            viewholder.bind(position)
        } else if (messages[position].files?.isNotEmpty() == true && messages[position].chatPersonalMessageCreator != userId || messages[position].localImgUri != null && messages[position].chatPersonalMessageCreator != userId) {
            val viewholder = holder as ChatReceivedImgMessageHolder
            viewholder.bind(position)
        } else {
            when (messages[position].chatPersonalMessageCreator) {
                userId -> {
                    val viewholder = holder as ChatSentMessageHolder
                    viewholder.bind(position)
                }
                else -> {
                    val viewholder = holder as ChatReceivedMessageHolder
                    viewholder.bind(position)
                }
            }
        }
    }

    inner class ChatOtherMessageHolder(val binding: ItemChatOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = messages[position]
            binding.apply {
                txtDate.text = messageData.timeCreated
                if (messageData.chatPersonalMessageCreator == userId) {
                    txtMessage.text =
                        "$userName ${App.instance.getString(R.string.create_msg_chat_title)}"
                } else {
                    txtMessage.text =
                        "$receiverName ${App.instance.getString(R.string.create_msg_chat_title)}"
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }

                myLogD(
                    "message = ${messageData.message}  id = ${messageData.id}",
                    "TAG_M"
                )
            }
        }
    }

    inner class ChatSentMessageHolder(val binding: ItemChatSendedMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageData = messages[position]
            binding.apply {

                if (position != messages.size - 1) {
                    val dateNew = messageData.timeCreated?.getDateDay()
                    val dateOld = messages[position + 1].timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }

                txtMessage.text = messageData.message

                txtTime.text = messageData.timeCreated?.getTimeFormatFromDate()

                if (messageData.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                }

                if (messageData.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData.answerFor.message

                    if (messageData.answerFor.chatPersonalMessageCreator == userId) {
                        txtReplySender.text = userName
                    } else {
                        txtReplySender.text = receiverName
                    }

                    if (messageData.answerFor.files != null && !messageData.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                } else {
                    llReply.gone()
                }

                llReply.setOnClickListener {
                    messageData.answerFor?.id?.let { it1 ->
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
            val messageData = messages[position]
            binding.apply {

                if (position != messages.size - 1) {
                    val dateNew = messageData.timeCreated?.getDateDay()
                    val dateOld = messages[position + 1].timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }

                txtMessage.text = messageData.message
                txtTime.text = messageData.timeCreated?.getTimeFormatFromDate()
                txtTimeXy.text = messageData.timeCreated?.getTimeFormatFromDate()

                if (messageData.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                    imgReadUnreadXy.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                    imgReadUnreadXy.setImageResource(R.drawable.ic_unread_message)
                }

                if (messageData.message != null && messageData.message.isNotEmpty()) {
                    llMsgText.visible()
                    llTimeXy.gone()
                } else {
                    llMsgText.gone()
                    llTimeXy.visible()
                }

                if (messageData.answerFor != null) {
                    llReply.visible()
                    txtReplyMsg.text = messageData.answerFor.message

                    if (messageData.answerFor.chatPersonalMessageCreator == userId) {
                        txtReplySender.text = userName
                    } else {
                        txtReplySender.text = receiverName
                    }

                    if (messageData.answerFor.files != null && !messageData.answerFor.files.isNullOrEmpty()) {
                        imgReplyPhoto.visible()
                        imgReplyPhoto.loadImageUrl(messageData.answerFor.files[0].file)
                    } else {
                        imgReplyPhoto.gone()
                    }

                } else {
                    llReply.gone()
                }

                try {
                    progressBar.visible()
                    imgSendPhoto.gone()
                    messageData.localImgUri?.let {
                        imgLocalPhoto.loadImageUri(it)
                        viewTrs.visible()
                        listenGoToNullPosition?.invoke(0)
                    }

                    messageData.files?.get(0)?.file?.let { imgUrl ->
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
                    messageData.answerFor?.id?.let { it1 ->
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
            val messageData = messages[position]
            binding.apply {

                if (position != messages.size - 1) {
                    val dateNew = messageData.timeCreated?.getDateDay()
                    val dateOld = messages[position + 1].timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }

                txtMessage.text = messageData.message

                txtTime.text = messageData.timeCreated?.getTimeFormatFromDate()

                if (messageData.answerFor != null) {
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
                    messageData.answerFor?.id?.let { it1 ->
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
            val messageData = messages[position]
            binding.apply {

                if (position != messages.size - 1) {
                    val dateNew = messageData.timeCreated?.getDateDay()
                    val dateOld = messages[position + 1].timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }

                txtMessage.text = messageData.message

                txtTime.text = messageData.timeCreated?.getTimeFormatFromDate()
                txtTimeXy.text = messageData.timeCreated?.getTimeFormatFromDate()

                if (messageData.message != null && messageData.message.isNotEmpty()) {
                    llMsgText.visible()
                    llTimeXy.gone()
                } else {
                    llMsgText.gone()
                    llTimeXy.visible()
                }

                if (messageData.answerFor != null) {
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

                try {
                    progressBar.visible()
                    messageData.files?.get(0)?.file?.let { imgUrl ->
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
                    messageData.answerFor?.id?.let { it1 ->
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
            val messageData = messages[position]
            binding.apply {

                if (position != messages.size - 1) {
                    val dateNew = messageData.timeCreated?.getDateDay()
                    val dateOld = messages[position + 1].timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }

                myLogD(
                    "message = ${messageData.message}  id = ${messageData.id}",
                    "TAG_M"
                )

                messageData.shortTask?.creator?.image?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }
                messageData.shortTask?.performer?.image?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }

                txtTaskTitle.text = messageData.shortTask?.title

                txtTime.text = messageData.timeCreated?.getTimeFormatFromDate()

                if (messageData.isRead == true) {
                    imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                } else {
                    imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                }

                try {

                } catch (e: Exception) {
                    myLogD(e.toString())
                }

                checkIsDone.isChecked = messageData.shortTask?.isDone == true

                checkIsDone.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (isChecked != messageData.shortTask!!.isDone) {
                        messageData.shortTask.id?.let {
                            listenUpdateTaskIsDone?.invoke(
                                it,
                                isChecked
                            )
                        }
                    }
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
            val messageData = messages[position]
            binding.apply {

                if (position != messages.size - 1) {
                    val dateNew = messageData.timeCreated?.getDateDay()
                    val dateOld = messages[position + 1].timeCreated?.getDateDay()

                    if (dateOld != dateNew) {
                        consDate.visible()
                        txtDate.text = dateNew
                    } else {
                        consDate.gone()
                    }
                }

                if (position == (messages.size - 1) && messageData.id != oldMessageId) {
                    listenGetNextData?.invoke(200)
                    oldMessageId = messageData.id!!
                    myLogD(
                        "MessagePosition = $position  message = ${messageData.message}  id = ${messageData.id}",
                        "TAG_M"
                    )
                }


                messageData.shortTask?.creator?.image?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }
                messageData.shortTask?.performer?.image?.let {
                    imgCreator.loadImageUrl(
                        it,
                        R.drawable.ic_user
                    )
                }

                txtTaskTitle.text = messageData.message

                txtTime.text = messageData.timeCreated?.getTimeFormatFromDate()

                if (messageData.isRead == true) {
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

                checkIsDone.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (isChecked != messageData.shortTask!!.isDone) {
                        messageData.shortTask.id?.let {
                            listenUpdateTaskIsDone?.invoke(
                                it,
                                isChecked
                            )
                        }
                    }
                }

            }
        }
    }


    override fun getItemCount(): Int = messages.size

    fun sendMessage(message: PersonalChatMessageListResponse.MessageDataItem) {
        val tempList = ArrayList<PersonalChatMessageListResponse.MessageDataItem>()
        tempList.addAll(messages)
        messages.clear()
        messages.add(message)
        messages.addAll(tempList)
        notifyDataSetChanged()
    }

    fun updateImgMessage(
        position: Int,
        messageDataItem: PersonalChatMessageListResponse.MessageDataItem,
        rv: RecyclerView
    ) {
        messages[position] = messageDataItem
        notifyDataSetChanged()
        rv.scrollToPosition(position)
    }

    fun updateMessage(
        position: Int,
        messageDataItem: PersonalChatMessageListResponse.MessageDataItem,
    ) {
        messages[position] = messageDataItem
        notifyDataSetChanged()
    }

    fun setOnclickListener(f: ThreeBlock<PersonalChatMessageListResponse.MessageDataItem, Int, View>) {
        listenClick = f
    }

    fun setGoToPositionListener(f: DoubleBlock<Int, Int>) {
        listenGoToPosition = f
    }

    fun getNextDataCallback(f: SingleBlock<Int>) {
        listenGetNextData = f
    }

    fun goToNullPositionCallback(f: SingleBlock<Int>) {
        listenGoToNullPosition = f
    }

    fun onOpenImageClickCallback(f: SingleBlock<PersonalChatMessageListResponse.MessageDataItem>) {
        listenClickOpenImage = f
    }

    fun updateTaskMessageIsDone(f: DoubleBlock<Int, Boolean>) {
        listenUpdateTaskIsDone = f
    }

}
*/
