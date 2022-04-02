package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.ImageStatusEnum
import uz.perfectalgorithm.projects.tezkor.databinding.DialogShowAvatarImgBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlAllAvatar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlShowImg
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class ShowAvatarImageDialog(
    private val avatarImageUrl: String,
    private val workerFullName: String,
    private val pictureStatus: String
) : DialogFragment() {

    private var _binding: DialogShowAvatarImgBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var boolVisible: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = DialogShowAvatarImgBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.MDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            txtDate.text = ""
            txtSenderName.text = workerFullName
            when (pictureStatus) {
                ImageStatusEnum.USER_AVATAR.text -> {
                    avatarImageUrl.let {
                        imgShow.loadImageUrlAllAvatar(
                            it,
                            R.drawable.ic_user
                        )
                    }
                }
                ImageStatusEnum.WORKER_AVATAR.text -> {
                    avatarImageUrl.let {
                        imgShow.loadImageUrlShowImg(
                            it,
                            R.drawable.ic_user
                        )
                    }
                }
            }

            btnBack.setOnClickListener {
                dismiss()
            }

            imgShow.setOnClickListener {
                changeVisibility()
            }
        }
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

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}