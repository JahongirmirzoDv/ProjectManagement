package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DeleteDialogEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentQuickPlanDetailUpdateBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_plans.QuickPlanDetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_plans.QuickPlanDetailUpdateViewModelFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_plans.provideFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.CONVERTED_DATE
import uz.perfectalgorithm.projects.tezkor.utils.CONVERTED_TITLE
import uz.perfectalgorithm.projects.tezkor.utils.adding.showDatePickerDialog
import uz.perfectalgorithm.projects.tezkor.utils.adding.toUiDate
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideQuickButton
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/19/21 8:53 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.quick_plans
 **/
/**
 * Tezkor reja detail-edit oynasi
 */
@AndroidEntryPoint
class QuickPlanDetailUpdateFragment : Fragment() {

    private var _binding: FragmentQuickPlanDetailUpdateBinding? = null
    private val binding get() = _binding!!
    private val quickPlanId by lazy { QuickPlanDetailUpdateFragmentArgs.fromBundle(requireArguments()).quickPlanId }
    private val isQuickTask by lazy { QuickPlanDetailUpdateFragmentArgs.fromBundle(requireArguments()).isQuickTask }
    private var isEditorMode = false
    private var originalData: QuickPlan? = null

    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    @Inject
    lateinit var viewModelFactory: QuickPlanDetailUpdateViewModelFactory
    private val viewModel by viewModels<QuickPlanDetailUpdateViewModel> {
        provideFactory(
            viewModelFactory,
            quickPlanId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickPlanDetailUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAppBar()
        hideBottomMenu()
        hideQuickButton()

        initViews()
        setObservers()
    }

    private fun initViews() = with(binding) {
        btnToTask.setOnClickListener {
            findNavController().navigate(
                R.id.quickPlanDetailUpdateFragment_to_createTaskFragment,
                bundleOf(
                    CONVERTED_DATE to tvDate.text.toString(),
                    "titleTask" to etTitle.text.toString(),
                    "quickTask" to "$isQuickTask",
                    "quickTaskID" to "$quickPlanId"
                )
            )
        }
        btnDelete.setOnClickListener {
            findNavController().navigate(
                QuickPlanDetailUpdateFragmentDirections.toDeleteDialogFragment(
                    DeleteDialogEnum.QUICK_PLAN,
                    quickPlanId
                )
            )
        }
        ivEdit.setOnClickListener {
            if (isEditorMode) {
                if (etTitle.text.isNullOrBlank()) {
                    etTitle.error = "Reja matnini kiriting"
                } else {
                    viewModel.updateQuickPlan(
                        UpdateQuickPlanRequest(
                            title = etTitle.text.toString(),
                            date = tvDate.text.toString(),
                            isDone = chbIsDone.isChecked
                        )
                    )
                }
            } else {
                switchMode()
            }
        }
        ivBack.setOnClickListener {
            if (isEditorMode) switchMode() else findNavController().navigateUp()
        }
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        dateLayout.setOnClickListener {
            if (isEditorMode) {
                showDatePickerDialog(LocalDate.parse(tvDate.text.toString(), formatter)) { date ->
                    tvDate.text = date.toUiDate()
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.quickPlan.simpleCollect(
            this@QuickPlanDetailUpdateFragment,
            binding.pbLoading
        ) { quickPlan ->
            originalData = quickPlan
            setDataToViews(quickPlan)
            binding.pbLoading.isVisible = false
            if (isEditorMode) switchMode()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isQuickPlanDeleted.collect { isChanged ->
                if (isChanged) {
                    findNavController().navigateUp()
                    sharedViewModel.setQuickPlanDeleted(false)
                }
            }
        }
    }

    private fun switchMode() = with(binding) {
        isEditorMode = !isEditorMode
        listOf(etTitle, chbIsDone).forEach { it.isEnabled = isEditorMode }
        ivEdit.setImageResource(if (isEditorMode) R.drawable.ic_checedk_in_create_project else R.drawable.ic_circle_edit)
        if (!isEditorMode) originalData?.let { setDataToViews(it) }
    }

    private fun setDataToViews(quickPlan: QuickPlan) = with(binding) {
        etTitle.setText(quickPlan.title)
        chbIsDone.isChecked = quickPlan.isDone
        tvDate.text = quickPlan.date
    }
}