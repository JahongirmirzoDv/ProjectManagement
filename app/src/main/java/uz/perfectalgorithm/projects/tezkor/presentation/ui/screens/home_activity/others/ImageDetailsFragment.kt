package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentImageDetailsBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapter.Companion.VIEW_TYPE_IMAGE
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadPictureUrl

/**
 *Created by farrukh_kh on 9/3/21 11:29 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.others
 **/
@AndroidEntryPoint
class ImageDetailsFragment : Fragment() {

    private var _binding: FragmentImageDetailsBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private val image by lazy { ImageDetailsFragmentArgs.fromBundle(requireArguments()).file }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomMenu()
        hideAppBar()

        initViews()
        setClickListeners()
    }

    private fun initViews() = with(binding) {
        tvTitle.text = image.title

        if (image.viewType == VIEW_TYPE_IMAGE) {
            pvImage.loadPictureUrl(image.path)
        } else {
            pvImage.setImageURI(image.uriFile)
        }
    }

    private fun setClickListeners() = with(binding) {
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        ivSave.setOnClickListener {
            // TODO: 9/3/21 save to gallery
//            viewModel.downloadImage(image.path)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}