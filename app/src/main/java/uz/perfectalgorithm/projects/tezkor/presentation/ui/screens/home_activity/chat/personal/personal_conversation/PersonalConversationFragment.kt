package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.personal.personal_conversation

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPersonalConversationBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.PersonalConversationAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.DeleteMessageDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.SendImageMsgDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.ShowImageMsgDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.personal.personal_conversation.PersonalConversationViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person.AllStaffSelectViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultData
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultList
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.mardex.model.results.local.MessageData
import java.io.File

@AndroidEntryPoint
class PersonalConversationFragment : Fragment() {

    /**Shaxsiy suhbat oynasi (личный чат)
     * tayyor.
     * **/

    private var _binding: FragmentPersonalConversationBinding? = null
    private val binding: FragmentPersonalConversationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private var sendAction = "none" // delete , edit , reply, none

    private val viewModel: PersonalConversationViewModel by viewModels()

    private lateinit var mAdapter: PersonalConversationAdapter

    private var myFile: File? = null

    private var booleanImage = true

    private var replyMessage: PersonalChatMessageListResponse.MessageDataItem? = null
    private var editedMessage: PersonalChatMessageListResponse.MessageDataItem? = null
    private var deletedMessage: PersonalChatMessageListResponse.MessageDataItem? = null
    private var sendFileMessage: PersonalChatMessageListResponse.MessageDataItem? = null

    private val viewModelPerform by viewModels<AllStaffSelectViewModel>()
    private var personData: PersonData? = null
    private var selectionList: List<AllWorkersShort>? = null

    private var isFirstScroll = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalConversationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
        hideBottomMenu()
        hideAppBar()
    }

    override fun onResume() {
        super.onResume()
        checkForTaskSuccess()
    }

    private fun checkForTaskSuccess() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<Boolean>("taskSuccessful")?.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.page = 0
                    viewModel.getMessages()
                }
            }
        }
    }

    private fun loadObservers() {
        viewModel.getMessages()
        viewModel.getReceiverData()
        viewModel.connectSocketAndComeResponses()

        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.messageListLiveData.observe(viewLifecycleOwner, messageListObserver)

        viewModel.sendMessageLiveData.observe(viewLifecycleOwner, sendMessageObserver)
        viewModel.updateMessageLiveData.observe(viewLifecycleOwner, updateMessageObserver)
        viewModel.deleteMessagesLiveData.observe(viewLifecycleOwner, deleteMessagesObserver)
        viewModel.uploadFileLiveData.observe(viewLifecycleOwner, uploadFileObserver)
        viewModel.receiverUserDataLiveData.observe(viewLifecycleOwner, receiverUserDataObserver)

        if (selectionList.isNullOrEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModelPerform.workers.collect {
                    when (it) {
                        is DataWrapper.Empty -> Unit
                        is DataWrapper.Error -> {

                        }
                        is DataWrapper.Loading -> {

                        }
                        is DataWrapper.Success -> {
                            selectionList = ArrayList(it.data)
                            val currentReceiverUser = viewModel.currentReceiverUser
                            selectionList?.forEachIndexed { index, allWorkersShort ->
                                if (currentReceiverUser?.id == allWorkersShort.id) {
                                    personData = PersonData(allWorkersShort.id?:0, allWorkersShort.fullName?:"", allWorkersShort.image, allWorkersShort.position, allWorkersShort.leader)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val messageObserver = Observer<MessageData> {

    }

    private val receiverUserDataObserver = Observer<AllWorkersResponse.DataItem> {
        viewModel.currentReceiverUser = it
        myLogD("Dda", "TAG_R")
    }

    @SuppressLint("NotifyDataSetChanged")
    private val messageListObserver =
        Observer<List<PersonalChatMessageListResponse.MessageDataItem>> { newMessages ->

            viewModel.mMessageList.clear()
            viewModel.mMessageList.addAll(newMessages)
            mAdapter.submitMessages(viewModel.mMessageList)

            if (viewModel.page == 1) {
                binding.rvMessages.scrollToPosition(0)
                binding.rvMessages.isNestedScrollingEnabled = false
            }
            mAdapter.notifyDataSetChanged()
        }

    private val sendMessageObserver =
        Observer<PersonalChatMessageListResponse.MessageDataItem> { sendMessage ->
            if (sendMessage.files?.isNotEmpty() === true && sendMessage.creator?.id == viewModel.getUserId()) {
                viewModel.mMessageList.removeAt(0)

            }
            var tempList = ArrayList<PersonalChatMessageListResponse.MessageDataItem>()
            tempList.addAll(viewModel.mMessageList)
            viewModel.mMessageList.clear()
            viewModel.mMessageList.add(sendMessage)
            viewModel.mMessageList.addAll(tempList)
            binding.rvMessages.scrollToPosition(0)
            mAdapter.notifyDataSetChanged()
        }

    /*
    * object : LinearLayoutManager(this){ override fun canScrollVertically(): Boolean { return false } }
    * */

    @SuppressLint("NotifyDataSetChanged")
    private val deleteMessagesObserver = Observer<List<Int>> {
        var delMessage: PersonalChatMessageListResponse.MessageDataItem? = null
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
        Observer<PersonalChatMessageListResponse.MessageDataItem> { updateMessage ->
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
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
                hideKeyboard()
            }

            binding.txtReceiverName.text = viewModel.receiverShortData().fullName

            viewModel.receiverShortData().image?.let {
                if (it == "") {
                    txtDefaultImg.text =
                        if (viewModel.receiverShortData().fullName.isNullOrEmpty()) "" else viewModel.receiverShortData().fullName?.get(
                            0
                        ).toString()
                    imgReceiver.gone()
                } else {
                    binding.imgReceiver.loadImageUrlUniversal(
                        it,
                        R.drawable.ic_user
                    )
                    txtDefaultImg.gone()
                    imgReceiver.visible()
                }
            }

            txtReceiverName.setOnClickListener {
                navigateDetailToFragment()
            }

            imgReceiver.setOnClickListener {
                navigateDetailToFragment()
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
        val sendImageMsgDialog =
            viewModel.receiverShortData().fullName?.let {
                SendImageMsgDialog(
                    uri,
                    viewModel.getUserId(),
                    it
                )
            }

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

    private fun navigateDetailToFragment() {

        viewModel.currentReceiverUser?.let {
            findNavController().navigate(
                PersonalConversationFragmentDirections.actionPersonalConversationFragmentToWorkerDetailFragment(
//                    workerData = it
                    it.id ?: 0
                )
            )
            viewModel.disconnectSocket()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        mAdapter = PersonalConversationAdapter(
            userId = viewModel.getUserId(),
            userName = viewModel.getUserFullName(),
            receiverName = viewModel.receiverShortData().fullName?:""
        )


        binding.rvMessages.adapter = mAdapter
/*
        val mLayoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            true,
        ).apply { stackFromEnd = false }
*/


        mAdapter.setOnclickListener { messageDataItem, i, view ->

            val menu = PopupMenu(requireContext(), view)
            if (messageDataItem?.creator?.id == viewModel.getUserId())
                if (messageDataItem.task == null) {
                    menu.inflate(R.menu.message_options_menu)
                } else {
                    menu.inflate(R.menu.message_options_menu_for_task)
                }
            else {
                if (messageDataItem?.task == null) {
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
                        bundle.putParcelable("personData", personData)
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
            myLogD("pos: $it", "TAGYH")
            if (it > 14) {
                binding.btnToBottom.visible()
            } else {
                binding.btnToBottom.gone()
            }
        }

        binding.btnToBottom.setOnClickListener {
            binding.rvMessages.scrollToPosition(0)
            binding.btnToBottom.gone()
            mAdapter.notifyDataSetChanged()
        }

        mAdapter.onOpenImageClickCallback { imageMessage ->
            showImageShowDialog(imageMessage)
        }

        mAdapter.setGlideListener {
            if (isFirstScroll) {
                binding.rvMessages.scrollToPosition(0)
                isFirstScroll = false
            }
            myLogD("Scroll to position 0", "TAG187")
        }
    }

    private fun showImageShowDialog(messageData: PersonalChatMessageListResponse.MessageDataItem) {
        val showImageMsgDialog =
            ShowImageMsgDialog(
                messageData = messageData,
                senderFullName = ""
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

    private fun openReplyMessage(messageData: PersonalChatMessageListResponse.MessageDataItem) {
        sendAction = "reply"
        replyMessage = messageData
        binding.clReplyMessage.visible()
        binding.txtReplyMessage.text = messageData.message
    }

    private fun openEditMessage(
        messageData: PersonalChatMessageListResponse.MessageDataItem,
    ) {
        sendAction = "edit"
        editedMessage = messageData
        binding.clEditMessage.visible()
        binding.etMessage.setText(editedMessage?.message)
        binding.txtEditedMessage.text = messageData.message
    }

    private fun deleteMessage(
        messageData: PersonalChatMessageListResponse.MessageDataItem,
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

        binding.progressBar.progressLoader.gone()

        default()
    }

    @SuppressLint("NotifyDataSetChanged")
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

