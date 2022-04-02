package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others

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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DeleteDialogEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.FolderErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.StatusErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogDeleteBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.DeleteViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack

/**
 *Created by farrukh_kh on 8/9/21 9:09 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.others
 **/
@AndroidEntryPoint
class DeleteDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogDeleteBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val args by navArgs<DeleteDialogFragmentArgs>()
    private val viewModel by viewModels<DeleteViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

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
        _binding = FragmentDialogDeleteBinding.inflate(layoutInflater)
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
        btnDelete.setOnClickListener {
            when (args.type) {
                DeleteDialogEnum.TASK -> viewModel.deleteTask(args.id)
                DeleteDialogEnum.PROJECT -> viewModel.deleteProject(args.id)
                DeleteDialogEnum.GOAL -> viewModel.deleteGoal(args.id)
                DeleteDialogEnum.MEETING -> viewModel.deleteMeeting(args.id)
                DeleteDialogEnum.DATING -> viewModel.deleteDating(args.id)
                DeleteDialogEnum.QUICK_PLAN -> viewModel.deleteQuickPlan(args.id)
                DeleteDialogEnum.TACTIC_PLAN -> viewModel.deleteTacticPlan(args.id)
                DeleteDialogEnum.TASK_STATUS -> viewModel.deleteTaskStatus(args.id)
                DeleteDialogEnum.PROJECT_STATUS -> viewModel.deleteProjectStatus(args.id)
                DeleteDialogEnum.TASK_FOLDER -> viewModel.deleteTaskFolder(args.id)
            }
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.response.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.rlLoading.isVisible = false
                        viewModel.clearResponse()
                        handleCustomException(dataWrapper.error) { error ->
                            when {
                                error.errors?.map { it.error }
                                    ?.contains(StatusErrorsEnum.STATUS_IS_NOT_EMPTY.message) == true ->
                                    when (args.type) {
                                        DeleteDialogEnum.TASK_STATUS -> makeErrorSnack("Bu statusda vazifalar mavjud")
                                        DeleteDialogEnum.PROJECT_STATUS -> makeErrorSnack("Bu statusda proyektlar mavjud")
                                        else -> makeErrorSnack("Bu statusda ma'lumotlar mavjud")
                                    }
                                error.errors?.map { it.error }
                                    ?.contains(FolderErrorsEnum.FOLDER_IS_NOT_EMPTY.message) == true ->
                                    makeErrorSnack("Bu guruhda vazifalar mavjud")
                                error.errors?.map { it.error }
                                    ?.contains(StatusErrorsEnum.STATUS_IS_DEFAULT.message) == true ->
                                    makeErrorSnack("Kompaniya yaratilganda yaratilgan statuslarni o'chirish mumkin emas")
                                else -> handleException(dataWrapper.error)
                            }
                        }
                    }
                    is DataWrapper.Loading -> binding.rlLoading.isVisible = true
                    is DataWrapper.Success -> {
                        binding.rlLoading.isVisible = false
                        when (args.type) {
                            DeleteDialogEnum.TASK -> sharedViewModel.setTaskDeleted(true)
                            DeleteDialogEnum.PROJECT -> sharedViewModel.setProjectDeleted(true)
                            DeleteDialogEnum.GOAL -> sharedViewModel.setGoalDeleted(true)
                            DeleteDialogEnum.MEETING -> sharedViewModel.setMeetingDeleted(true)
                            DeleteDialogEnum.DATING -> sharedViewModel.setDatingDeleted(true)
                            DeleteDialogEnum.QUICK_PLAN -> sharedViewModel.setQuickPlanDeleted(
                                true
                            )
                            DeleteDialogEnum.TACTIC_PLAN ->
                                sharedViewModel.setTacticPlanDeleted(true)
                            DeleteDialogEnum.TASK_STATUS ->
                                sharedViewModel.setFunctionalColumnNeedsRefresh(true)
                            DeleteDialogEnum.PROJECT_STATUS ->
                                sharedViewModel.setProjectColumnNeedsRefresh(true)
                            DeleteDialogEnum.TASK_FOLDER ->
                                sharedViewModel.setFunctionalGroupNeedsRefresh(true)
                        }
                        makeSuccessSnack("Muvaffaqiyatli o'chirildi")
                        viewModel.clearResponse()
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