package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.group.group_conversation

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentGroupConversationBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.GroupConversationAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.*
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.personal.personal_conversation.PersonalConversationFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.group.group_conversation.GroupConversationViewModel
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.inVisible
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData
import java.io.File

@AndroidEntryPoint
class GroupConversationFragment : Fragment() {
    /**Guruh chatList oynasi:
     * tayyor:Yozishmalar,junatmalar(foto), habarni o'chirish, o'zgartirish,habarga repluy qilish,
     * guruh malumotlari (detail page) ga kirish
     *
     * **/

    private var _binding: FragmentGroupConversationBinding? = null
    private val binding: FragmentGroupConversationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: GroupConversationViewModel by viewModels()

    private var sendAction = "none" // delete , edit , reply, none

    private lateinit var mAdapter: GroupConversationAdapter

    private var myFile: File? = null

    private var booleanImage = false
    private var groupChatData: ChatGroupListResponse.GroupChatDataItem? = null

    private var replyMessage: GroupChatSendMessageResponse.MessageDataItem? = null
    private var editedMessage: GroupChatSendMessageResponse.MessageDataItem? = null
    private var deletedMessage: GroupChatSendMessageResponse.MessageDataItem? = null
    private var sendFileMessage: GroupChatSendMessageResponse.MessageDataItem? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupConversationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()
        loadObservers()
        loadViews()
    }

    override fun onResume() {
        super.onResume()
        checkForTaskSuccess()
    }

    private fun checkForTaskSuccess() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<Boolean>("taskSuccessful")?.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.mMessageList.clear()
                    viewModel.page = 0
                    viewModel.getMessages()
                }
            }
        }
    }

    private fun loadObservers() {
        if (viewModel.mMessageList.isEmpty()) {
            viewModel.getMessages()
        } else {
            mAdapter.submitMessages(viewModel.mMessageList)
        }
        viewModel.getDetailData()
        viewModel.connectSocketAndComeResponses()

        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.messageListLiveData.observe(viewLifecycleOwner, messageListObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorLivedata)
        viewModel.detailLiveData.observe(viewLifecycleOwner, detailLiveObserver)

        viewModel.sendMessageLiveData.observe(viewLifecycleOwner, sendMessageObserver)
        viewModel.updateMessageLiveData.observe(viewLifecycleOwner, updateMessageObserver)
        viewModel.deleteMessagesLiveData.observe(viewLifecycleOwner, deleteMessagesObserver)
        viewModel.uploadFileLiveData.observe(viewLifecycleOwner, uploadFileObserver)
    }

    private val messageObserver = Observer<MessageData> {

    }

    private val errorLivedata = Observer<Throwable> {

    }

    @SuppressLint("NotifyDataSetChanged")
    private val messageListObserver =
        Observer<List<GroupChatSendMessageResponse.MessageDataItem>> { newMessages ->
            if (newMessages != null) {
//                viewModel.mMessageList.clear()
                if (viewModel.mMessageList.isEmpty()) {
                    viewModel.mMessageList.addAll(newMessages)
                } else {
                    if (!viewModel.mMessageList.containsAll(newMessages)) {
                        viewModel.mMessageList.addAll(newMessages)
                    }
                }
                mAdapter.submitMessages(viewModel.mMessageList)

                if (viewModel.page == 1) {
                    binding.rvMessages.scrollToPosition(0)
                    binding.rvMessages.isNestedScrollingEnabled = false
                }
                mAdapter.notifyDataSetChanged()
            }
        }

    private val sendMessageObserver =
        Observer<GroupChatSendMessageResponse.MessageDataItem> { sendMessage ->

            if (sendMessage.files?.isNotEmpty() === true && sendMessage.creator?.id == viewModel.getUserId()) {
                viewModel.mMessageList.removeAt(0)
            }

            var tempList = ArrayList<GroupChatSendMessageResponse.MessageDataItem>()
            tempList.addAll(viewModel.mMessageList)
            viewModel.mMessageList.clear()
            viewModel.mMessageList.add(sendMessage)
            viewModel.mMessageList.addAll(tempList)
            binding.rvMessages.scrollToPosition(0)
            mAdapter.notifyDataSetChanged()
        }

    private val detailLiveObserver =
        Observer<GroupChatDetailResponse.ResponseData> { data ->

            binding.apply {
                txtDefaultImg.text = data?.title?.get(0).toString()
                imgGroup.gone()
                data?.image?.let {
                    imgGroup.visible()
                    txtDefaultImg.gone()
                    imgGroup.loadImageUrlUniversal(
                        it,
                        R.drawable.back_button_blue
                    )
                }
                txtGroupTitle.text = data?.title
                txtCountMembers.text = data?.members?.size.toString()
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private val deleteMessagesObserver = Observer<List<Int>> {
        var delMessage: GroupChatSendMessageResponse.MessageDataItem? = null
        val deletedMessageId = it[0]
        if (deletedMessageId > 0) {
            viewModel.mMessageList.forEach lit@{ a ->
                if (a.id == deletedMessageId) {
                    delMessage = a
                    return@lit
                }
            }
            viewModel.mMessageList.remove(delMessage)
            mAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val updateMessageObserver =
        Observer<GroupChatSendMessageResponse.MessageDataItem> { updateMessage ->
            var count = 0
            viewModel.mMessageList.forEach lit@{ a ->
                if (a.id == updateMessage.id) {
                    viewModel.mMessageList[count] = updateMessage
                    return@lit
                }
                count++
            }
            mAdapter.notifyDataSetChanged()
        }

    private val uploadFileObserver = Observer<ChatFileUploadResponse.FileUploadData> {
        val fileIds = ArrayList<Int>()
        it.id?.let { it1 ->
            fileIds.add(it1)
            sendFileMessage(files = fileIds)
        }
    }

    private val progressObserver = Observer<Boolean> { isProgress ->
        if (isProgress) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private fun loadViews() {
        initRecyclerView()
        initForSendMessage()
        loadData()
    }

    private fun loadData() {
        binding.apply {
            groupChatData = viewModel.getCurrentGroupData()

            if (groupChatData != null) {
                btnBack.setOnClickListener {
                    findNavController().navigateUp()
                    hideKeyboard()
                }

                btnFile.setOnClickListener {
                    selectImage()
                }

                btnCancelReply.setOnClickListener {
                    default()
                }

                btnEditCancel.setOnClickListener {
                    default()
                }

                btnGoToDetail.setOnClickListener {
//                    viewModel.mMessageList.clear()
                    findNavController().navigate(
                        GroupConversationFragmentDirections.actionGroupConversationFragmentToGroupChatDetailFragment()
                    )
                }
                if (groupChatData?.isChannel == true && !viewModel.isOwner()) {
                    clSend.inVisible()
                    tvNotPermission.show()
                } else {
                    clSend.show()
                    tvNotPermission.gone()
                }
            }
        }
    }

    private fun selectImage() {
        booleanImage = false
        ImagePicker.with(requireActivity())
            .saveDir(
                File(
                    requireActivity().getExternalFilesDir(null)?.absolutePath,
                    "ProjectManagement"
                )
            )
            .maxResultSize(1080, 1920)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        myFile = ImagePicker.getFile(data)!!
                        data?.data?.let { showImageSendDialog(it) }
                    }
                }
            }
    }

    private fun showImageSendDialog(uri: Uri) {
        viewModel.getCurrentGroupData().title?.let {
            val sendImageMsgDialog =
                SendImageMsgGroupDialog(
                    uri,
                    viewModel.getUserId(),
                    it
                )

            sendImageMsgDialog?.sendClickListener { sendImageData ->
                sendFileMessage = sendImageData
                mAdapter.sendMessage(sendFileMessage!!)
                myFile?.let { viewModel.uploadFile(file = it) }
                binding.rvMessages.scrollToPosition(0)
                mAdapter.notifyDataSetChanged()
            }

            sendImageMsgDialog?.closeClickListener {

            }

            sendImageMsgDialog?.show(
                (requireActivity() as HomeActivity).supportFragmentManager,
                "Send Image Dialog"
            )
        }
    }

    private fun initRecyclerView() {
        if (viewModel.mMessageList.isEmpty()) {
            mAdapter = GroupConversationAdapter(
                userId = viewModel.getUserId()
            )
        }
        binding.rvMessages.adapter = mAdapter
        mAdapter.setOnclickListener { messageDataItem, i, view ->
            val menu = PopupMenu(requireContext(), view)
            if (messageDataItem?.creator?.id == viewModel.getUserId())
                if (messageDataItem.task == null && messageDataItem.meeting == null && messageDataItem.note == null) {
                    menu.inflate(R.menu.message_options_menu)
                } else {
                    menu.inflate(R.menu.message_options_menu_for_task)
                }
            else {
                if (messageDataItem?.task == null && messageDataItem?.meeting == null && messageDataItem?.note == null) {
                    menu.inflate(R.menu.message_options_menu_receiver)
                } else {
                    menu.inflate(R.menu.message_options_menu_receiver_for_task)
                }
            }

            menu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.reply_message_xd -> {
                        messageDataItem?.let { openReplyMessage(it) }
                    }
                    R.id.edit_message_xd -> {
                        messageDataItem?.let { openEditMessage(it) }
                    }
                    R.id.delete_message_xd -> {
                        messageDataItem?.let { deleteMessage(it) }
                    }
                    R.id.set_to_task_message_xd -> {
                        val bundle = Bundle()
                        bundle.putString("titleTask", messageDataItem?.message)
                        bundle.putInt("messageID", messageDataItem?.id?:-1)
                        findNavController().navigate(R.id.createTaskFragment, bundle)
                    }
                    R.id.set_to_meeting_message_xd -> {
                        val bundle = Bundle()
                        bundle.putString("titleTask", messageDataItem?.message)
                        bundle.putInt("messageID", messageDataItem?.id?:-1)
                        findNavController().navigate(R.id.createMeetingFragment, bundle)
                    }
                    R.id.set_to_note_message_xd -> {
                        val bundle = Bundle()
                        bundle.putString("titleTask", messageDataItem?.message)
                        bundle.putInt("messageID", messageDataItem?.id?:-1)
                        findNavController().navigate(R.id.navigation_create_note, bundle)
                    }
                }
                true
            }
            menu.show()
        }

        mAdapter.getNextDataCallback {
            viewModel.getMessages()
        }

        mAdapter.visibleToBottomCallback {
            if (it > 14) {
                binding.btnToBottom.visible()
            } else {
                binding.btnToBottom.gone()
            }
        }

        binding.btnToBottom.setOnClickListener {
            binding.rvMessages.smoothScrollToPosition(0)
            mAdapter.notifyDataSetChanged()
        }

        mAdapter.onOpenImageClickCallback { imageMessage ->
            showImageShowDialog(imageMessage)
        }

    }

    private fun showImageShowDialog(messageData: GroupChatSendMessageResponse.MessageDataItem) {
        val showImageMsgDialog =
            ShowImageMsgGroupDialog(
                messageData = messageData
            )
        showImageMsgDialog.show(
            (requireActivity() as HomeActivity).supportFragmentManager,
            "Show Image Dialog"
        )

        showImageMsgDialog.deleteClickListener {
            val listD = ArrayList<Int>()
            it.id?.let { it1 -> listD.add(it1) }
            viewModel.deleteMessages(listD)
        }
    }

    private fun openReplyMessage(messageData: GroupChatSendMessageResponse.MessageDataItem) {
        sendAction = "reply"
        replyMessage = messageData
        binding.clReplyMessage.visible()
        binding.txtReplyMessage.text = messageData.message
    }

    private fun openEditMessage(
        messageData: GroupChatSendMessageResponse.MessageDataItem,
    ) {
        sendAction = "edit"
        editedMessage = messageData
        binding.clEditMessage.visible()
        binding.etMessage.setText(editedMessage?.message)
        binding.txtEditedMessage.text = messageData.message
    }

    private fun deleteMessage(
        messageData: GroupChatSendMessageResponse.MessageDataItem,
    ) {
        deletedMessage = messageData
        val delList = ArrayList<Int>()
        delList.add(messageData.id!!)

        val deleteMessageDialog = DeleteMessageDialog(requireActivity())
        deleteMessageDialog.deleteClickListener {
            sendAction = "delete"
            viewModel.deleteMessages(delList)
        }

        deleteMessageDialog.show()

    }

    private fun initForSendMessage() {
        with(binding) {
            etMessage.addTextChangedListener {
                if (it.toString().isNotEmpty()) {
//                    imgMicrophone.visibility = View.GONE
                    btnFile.visibility = View.GONE
                    imgSendMessage.visibility = View.VISIBLE
                } else {
//                    imgMicrophone.visibility = View.VISIBLE
                    btnFile.visibility = View.VISIBLE
                    imgSendMessage.visibility = View.GONE
                }
            }

            imgSendMessage.setOnClickListener {
                when (sendAction) {
                    "edit" -> {
                        updateMessage()
                    }
                    else -> {
                        sendMessage()
                    }
                }
                default()
                binding.etMessage.setText("")
            }
        }
    }

    private fun sendMessage(files: List<Int> = listOf()) {
        viewModel.sendMessage(
            messageText = binding.etMessage.text.toString().trim(),
            answerFor = replyMessage?.id,
            files = if (files.isNotEmpty()) files else null
        )

        default()

    }

    private fun updateMessage() {
        editedMessage?.id?.let { messageID ->
            viewModel.updateMessage(
                newMessage = binding.etMessage.text.toString().trim(),
                editedMessageId = messageID
            )
        }
        default()
    }

    private fun sendFileMessage(files: List<Int> = listOf()) {
        viewModel.sendMessage(
            messageText = sendFileMessage?.message ?: "",
            files = if (files.isNotEmpty()) files else null, answerFor = replyMessage?.id
        )

        default()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSocket()
    }

    fun default() {
        editedMessage = null
        replyMessage = null
        deletedMessage = null
        myFile = null
        sendFileMessage = null
        sendAction = "none"
        binding.clReplyMessage.gone()
        binding.clEditMessage.gone()
    }

}