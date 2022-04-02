package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.group.group_detail

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentGroupChatDetailBinding
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentGroupDetailEditBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.group.group_chat_detail.GroupChatDetailViewModel
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.io.File

@AndroidEntryPoint
class GroupDetailEditFragment : Fragment() {

    private var _binding: FragmentGroupDetailEditBinding? = null
    private val binding: FragmentGroupDetailEditBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")
    private val viewModel: GroupChatDetailViewModel by viewModels()

    private var file: File? = null

    private var title = ""
    private var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title") as String
            image = it.getString("image") as String
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupDetailEditBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            edtGroupTitle.setText(title)
            btnBack.setOnClickListener { findNavController().popBackStack() }

            if (image.isNotEmpty()) {
                imgGroup.visibility = View.VISIBLE
                imgGroup.loadImageUrlUniversal(image)
            } else {
                imgGroup.visibility = View.GONE
                textImage.text = if (title.isNotEmpty() && title.length>3) title.substring(0,2) else title[0].toString()
            }

            addImage.setOnClickListener {
                selectImage()
            }

            btnSave.setOnClickListener {
                val toString = edtGroupTitle.text.toString()
                if (file != null) {
                    viewModel.detailUpdateDataWithImage(toString, file!!)
                } else {
                    viewModel.detailUpdateData(toString)
                }
            }

            viewModel.detailEditLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    findNavController().popBackStack()
                }
            }
        }
    }



    private fun selectImage() {
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
                        binding.apply {
                            file = ImagePicker.getFile(data)!!
                            imgGroup.setImageURI(data?.data!!)
                            imgGroup.visibility = View.VISIBLE
                        }
                    }
                }
            }
    }


}