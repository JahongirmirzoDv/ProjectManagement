package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.offers

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogShowImageOfferBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

class ShowImageDialog(
    private val imageUrl: String,
) : DialogFragment() {

    private var _binding: DialogShowImageOfferBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var listenSend: SingleBlock<PersonalChatMessageListResponse.MessageDataItem>? =
        null

    private var closeClick: EmptyBlock? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = DialogShowImageOfferBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.MDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            imgSend.loadImageUrl(imageUrl)
            btnBack.setOnClickListener {
                dismiss()
            }
        }
    }
}