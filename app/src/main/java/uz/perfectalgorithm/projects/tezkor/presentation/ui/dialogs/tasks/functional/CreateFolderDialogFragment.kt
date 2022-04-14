package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.functional

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.folder.CreateFolderRequest
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogCreateFolderBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional.CreateFolderViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException

@AndroidEntryPoint
class CreateFolderDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogCreateFolderBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private val viewModel by viewModels<CreateFolderViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val folder by lazy { CreateFolderDialogFragmentArgs.fromBundle(requireArguments()).folder }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogCreateFolderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
    }

    private fun initViews() = with(binding) {
        if (folder == null) {
            btnSubmit.setText(R.string.add)
            tvTitle.setText(R.string.add_works)
        } else {
            btnSubmit.setText(R.string.edit__)
            tvTitle.setText(R.string.add_works)
            etName.setText(folder?.title)
        }
        btnSubmit.setOnClickListener {
            if (etName.text.isNullOrBlank()) {
                etName.error = resources.getString(R.string.error_group_title)
            } else {
                if (folder == null) {
                    viewModel.createFolder(CreateFolderRequest(etName.text.toString()))
                } else {
                    viewModel.updateFolder(
                        folder!!.id ?: 0,
                        CreateFolderRequest(etName.text.toString())
                    )
                }
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.response.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.rlLoading.isVisible = false
                        handleException(dataWrapper.error)
                    }
                    is DataWrapper.Loading -> binding.rlLoading.isVisible = true
                    is DataWrapper.Success -> {
                        binding.rlLoading.isVisible = false
                        sharedViewModel.setFunctionalGroupNeedsRefresh(true)
//                        if (folder == null) {
//                            showToast("Vazifalar guruhi muvaffaqiyatli qo'shildi")
//                        } else {
//                            showToast("Vazifalar guruhi muvaffaqiyatli o'zgartirildi")
//                        }
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}