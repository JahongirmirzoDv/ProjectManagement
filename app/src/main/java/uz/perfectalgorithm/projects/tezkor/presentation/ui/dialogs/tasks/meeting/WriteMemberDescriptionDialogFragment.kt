package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.meeting

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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.MemberStateRequest
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogWriteMemberDescriptionBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting.member.SelectMemberStateViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException

/**
 *Created by farrukh_kh on 8/31/21 1:47 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.tasks.meeting
 **/
@AndroidEntryPoint
class WriteMemberDescriptionDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogWriteMemberDescriptionBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private val memberId by lazy {
        WriteMemberDescriptionDialogFragmentArgs.fromBundle(
            requireArguments()
        ).memberId
    }
    private val viewModel by viewModels<SelectMemberStateViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogWriteMemberDescriptionBinding.inflate(layoutInflater)
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
        btnSubmit.setOnClickListener {
            if (etDescription.text.isNullOrBlank()) {
                etDescription.error = "Sababni kiriting"
            } else {
                viewModel.sendMemberState(
                    memberId ?: 0,
                    MemberStateRequest("rejected", etDescription.text.toString())
                )
            }
        }
    }

    private fun initObservers() = with(viewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            response.collect { response ->
                when (response) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.rlLoading.isVisible = false
                        handleException(response.error)
                    }
                    is DataWrapper.Loading -> binding.rlLoading.isVisible = true
                    is DataWrapper.Success -> {
                        binding.rlLoading.isVisible = false
                        sharedViewModel.setMemberStateNeedsRefresh(true)
                        findNavController().popBackStack(R.id.meetingDetailsFragment, false)
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