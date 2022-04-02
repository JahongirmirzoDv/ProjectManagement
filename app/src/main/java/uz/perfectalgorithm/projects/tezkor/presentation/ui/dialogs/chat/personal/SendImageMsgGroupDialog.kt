package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogSendImageMsgBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUriSendImg

class SendImageMsgGroupDialog(
    private val uri: Uri,
    private val userId: Int,
    private val title: String
) : DialogFragment() {

    private var _binding: DialogSendImageMsgBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var listenSend: SingleBlock<GroupChatSendMessageResponse.MessageDataItem>? =
        null

    private var closeClick: EmptyBlock? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        _binding = DialogSendImageMsgBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.MDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            imgSend.loadImageUriSendImg(uri)
            txtReceiverName.text = title

            btnSend.setOnClickListener {
                val sendFileMessage =
                    GroupChatSendMessageResponse.MessageDataItem(
                        id = -1,
                        creator = GroupChatSendMessageResponse.Creator(
                            id = userId,
                            image = null,
                            fullName = null
                        ),
                        localImgUri = uri,
                        message = etMessage.text.toString().trim(),
                        files = listOf()
                    )

                listenSend?.invoke(sendFileMessage)
                dismiss()
            }

            btnBack.setOnClickListener {
                closeClick?.invoke()
                dismiss()
            }
        }
    }


    fun sendClickListener(f: SingleBlock<GroupChatSendMessageResponse.MessageDataItem>) {
        listenSend = f
    }

    fun closeClickListener(f: EmptyBlock) {
        closeClick = f
    }

}