package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogShowImageMsgBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.personal.personal_conversation.ShowImageMsgViewModel
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlShowImg
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class ShowImageMsgGroupDialog(
    private val messageData: GroupChatSendMessageResponse.MessageDataItem,
) : DialogFragment() {

    private var _binding: DialogShowImageMsgBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: ShowImageMsgViewModel by viewModels()
    private var listenDelete: SingleBlock<GroupChatSendMessageResponse.MessageDataItem>? =
        null

    private var boolVisible: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = DialogShowImageMsgBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.MDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            txtDate.text = messageData.timeCreated
            txtSenderName.text = messageData.creator?.fullName
            messageData.files?.get(0)?.file?.let {
                imgSend.loadImageUrlShowImg(
                    it,
                    R.drawable.ic_img_business_decisions_intro
                )
            }

            btnMore.setOnClickListener {
                llMore.visible()
            }

            imgSend.setOnClickListener {
                llMore.gone()
            }

            appBarView.setOnClickListener {
                llMore.gone()
            }

            clDetail.setOnClickListener {
                llMore.gone()
            }

            btnShowInChat.setOnClickListener {
                llMore.gone()
                dismiss()
            }

            btnDelete.setOnClickListener {
                llMore.gone()
                openDeleteDialog()
            }

            btnBack.setOnClickListener {
                llMore.gone()
                dismiss()
            }

            btnSave.setOnClickListener {
                llMore.gone()
                messageData.files?.get(0)?.file?.let { it1 -> viewModel.downloadImage(it1) }
            }
            imgSend.setOnClickListener {
                changeVisibility()
                llMore.gone()
            }

            if (viewModel.getUserId() == messageData.creator?.id) {
                btnDelete.visible()
            } else {
                btnDelete.gone()
            }

        }

        loadObservers()
    }

    private fun changeVisibility() {
        boolVisible = !boolVisible
        if (boolVisible) {
            binding.appBarView.visible()
            binding.clDetail.visible()
        } else {
            binding.appBarView.gone()
            binding.clDetail.gone()
        }
    }


    private fun loadObservers() {
        viewModel.downloadFileLiveData.observe(viewLifecycleOwner, downloadFileObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
    }

    private val downloadFileObserver = Observer<String> {
        makeSuccessSnack(getString(R.string.saved_image_file))
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    private fun openDeleteDialog() {
        myLogD("LOG!&*(", "LOG18")
        val deleteMessageDialog = DeleteMessageDialog(requireActivity())
        deleteMessageDialog.deleteClickListener {
            listenDelete?.invoke(messageData)
            dismiss()
        }
        deleteMessageDialog.show()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    fun deleteClickListener(f: SingleBlock<GroupChatSendMessageResponse.MessageDataItem>) {
        listenDelete = f
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}