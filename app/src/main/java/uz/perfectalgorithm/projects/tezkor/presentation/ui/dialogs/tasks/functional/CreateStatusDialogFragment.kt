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
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.StatusErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogCreateStatusBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional.CreateStatusViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/26/21 9:30 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.tasks
 **/
@AndroidEntryPoint
class CreateStatusDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogCreateStatusBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private val viewModel by viewModels<CreateStatusViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val status by lazy { CreateStatusDialogFragmentArgs.fromBundle(requireArguments()).status }

    @Inject
    lateinit var storage: LocalStorage

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
        _binding = FragmentDialogCreateStatusBinding.inflate(layoutInflater)
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
        if (status == null) {
            btnSubmit.text = "Qo'shish"
            tvTitle.text = "Vazifalar ustuni qo'shish"
        } else {
            btnSubmit.text = "O'zgartirish"
            tvTitle.text = "Vazifalar ustunini o'zgartirish"
            etName.setText(status?.title)
            chbIsVisible.isChecked = status?.isViewable == true
        }
        btnSubmit.setOnClickListener {
            if (etName.text.isNullOrBlank()) {
                etName.error = "Ustun nomini kiriting"
            } else {
                if (status == null) {
                    viewModel.createStatus(
                        CreateStatusRequest(
                            storage.selectedCompanyId,
                            etName.text.toString(),
                            chbIsVisible.isChecked
                        )
                    )
                } else {
                    viewModel.updateStatus(
                        status!!.id!!, CreateStatusRequest(
                            storage.selectedCompanyId,
                            etName.text.toString(),
                            chbIsVisible.isChecked
                        )
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
                        handleCustomException(dataWrapper.error) { error ->
                            when {
                                error.errors?.map { it.error }
                                    ?.contains(StatusErrorsEnum.STATUS_EXISTS.message) == true ->
                                    binding.etName.error =
                                        "Bu nomli status mavjud. Boshqa nom kiriting"
                                else -> handleException(dataWrapper.error)
                            }
                        }
                    }
                    is DataWrapper.Loading -> binding.rlLoading.isVisible = true
                    is DataWrapper.Success -> {
                        binding.rlLoading.isVisible = false
                        sharedViewModel.setFunctionalColumnNeedsRefresh(true)
//                        if (status == null) {
//                            showToast("Vazifalar ustuni muvaffaqiyatli qo'shildi")
//                        } else {
//                            showToast("Vazifalar ustuni muvaffaqiyatli o'zgartirildi")
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