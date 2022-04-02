package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.tactic_plan

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
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DeleteDialogEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.UpdateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanDetails
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentTacticPlanDetailUpdateBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan.TacticPlanDetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan.TacticPlanDetailUpdateViewModelFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan.provideFactory
import uz.perfectalgorithm.projects.tezkor.utils.CONVERTED_TITLE
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.adding.showStatusDialog
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideQuickButton
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.setItems
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toTwoDigit
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/23/21 4:26 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.tasks.tactic_plan
 **/
/**
 * Taktik reja detail-edit oynasi
 */
@AndroidEntryPoint
class TacticPlanDetailUpdateFragment : Fragment() {

    private var _binding: FragmentTacticPlanDetailUpdateBinding? = null
    private val binding get() = _binding!!
    private val tacticPlanId by lazy {
        TacticPlanDetailUpdateFragmentArgs.fromBundle(
            requireArguments()
        ).tacticPlanId
    }
    private var isEditorMode = false
    private var originalData: TacticPlanDetails? = null
    private val statusList by lazy { mutableListOf<Status>() }
    private val yearList by lazy { mutableListOf<String>() }
    private val monthList by lazy {
        listOf(
            "Yanvar",
            "Fevral",
            "Mart",
            "Aprel",
            "May",
            "Iyun",
            "Iyul",
            "Avgust",
            "Sentyabr",
            "Oktyabr",
            "Noyabr",
            "Dekabr"
        )
    }

    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    @Inject
    lateinit var viewModelFactory: TacticPlanDetailUpdateViewModelFactory
    private val viewModel by viewModels<TacticPlanDetailUpdateViewModel> {
        provideFactory(
            viewModelFactory,
            tacticPlanId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTacticPlanDetailUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAppBar()
        hideBottomMenu()
        hideQuickButton()

        var year = LocalDate().year - 1

        yearList.clear()
        for (i in 0..4) {
            yearList.add(year.toString())
            year++
        }
        initViews()
        setObservers()
    }

    private fun initViews() = with(binding) {
        spYear.setItems(yearList)
        spMonth.setItems(monthList)
        btnToTask.setOnClickListener {
            findNavController().navigate(
                R.id.tacticPlanDetailUpdateFragment_to_createTaskFragment,
                bundleOf(
                    CONVERTED_TITLE to etTitle.text.toString()
                )
            )
        }
        btnDelete.setOnClickListener {
            findNavController().navigate(
                TacticPlanDetailUpdateFragmentDirections.toDeleteDialogFragment(
                    DeleteDialogEnum.TACTIC_PLAN,
                    tacticPlanId
                )
            )
        }
        ivEdit.setOnClickListener {
            if (isEditorMode) {
                val isInputCompleted = isInputCompleted(
                    listOf(
                        Triple(etTitle.text.isNullOrBlank(), etTitle, "Reja matnini kiriting")
                    ), nestedScroll
                )

                if (isInputCompleted) {
                    viewModel.updateQuickPlan(
                        UpdateTacticPlanRequest(
                            "${yearList[spYear.selectedItemPosition]}-${(spMonth.selectedItemPosition + 1).toTwoDigit()}-01",
                            statusList.find { it.title == tvStatus.text.toString() }!!.id,
                            etTitle.text.toString(),
                            etDescription.text.toString(),
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

        statusLayout.setOnClickListener {
            if (isEditorMode) {
                if (statusList.isNullOrEmpty()) {
                    viewModel.initStatusList()
                } else {
                    showStatusDialog(statusList.map {
                        StatusData(
                            id = it.id,
                            title = it.title
                        )
                    }) { selectedStatus ->
                        tvStatus.text = selectedStatus.title
                    }
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.tacticPlan.simpleCollect(
            this,
            binding.pbLoading
        ) { tacticPlan ->
            originalData = tacticPlan
            setDataToViews(tacticPlan)
            binding.pbLoading.isVisible = false
            if (isEditorMode) switchMode()
        }

        viewModel.updateResponse.simpleCollect(this, binding.pbLoading) { _ ->
            viewModel.initTacticPlan()
            originalData?.let { it -> setDataToViews(it) }
            binding.pbLoading.isVisible = false
            if (isEditorMode) switchMode()
//            sharedViewModel.setTacticPlanNeedsRefresh(true)
        }

        viewModel.statusList.simpleCollect(this, binding.pbLoadingStatus) { statuses ->
            statusList.clear()
            statusList.addAll(statuses)
            binding.pbLoadingStatus.isVisible = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isTacticPlanDeleted.collect { isChanged ->
                if (isChanged) {
                    findNavController().navigateUp()
                    sharedViewModel.setTacticPlanDeleted(false)
                }
            }
        }
    }

    private fun switchMode() = with(binding) {
        isEditorMode = !isEditorMode
        listOf(etTitle, etDescription, spYear, spMonth).forEach { it.isEnabled = isEditorMode }
        ivEdit.setImageResource(if (isEditorMode) R.drawable.ic_checedk_in_create_project else R.drawable.ic_circle_edit)
        if (!isEditorMode) originalData?.let { setDataToViews(it) }
    }

    private fun setDataToViews(tacticPlan: TacticPlanDetails) = with(binding) {
        etTitle.setText(tacticPlan.title)
        etDescription.setText(tacticPlan.description)
        spYear.setSelection(yearList.indexOf(tacticPlan.date.split("-")[0]))
        spMonth.setSelection(tacticPlan.date.split("-")[1].toInt() - 1)
        tvStatus.text = tacticPlan.status.title
    }
}