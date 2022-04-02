package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.tactic_plan

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.CreateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogCreateTacticPlanBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan.CreateTacticPlanViewModel
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.setItems
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toTwoDigit

/**
 *Created by farrukh_kh on 7/29/21 2:27 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.tasks.tactic_plan
 **/
@AndroidEntryPoint
class CreateTacticPlanDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogCreateTacticPlanBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private val args by navArgs<CreateTacticPlanDialogFragmentArgs>()
    private val statusList by lazy { mutableListOf<Status>() }
    private val yearList by lazy { initYearList() }
    private val monthList by lazy { initMonthList() }

    private val viewModel by viewModels<CreateTacticPlanViewModel>()
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
        _binding = FragmentDialogCreateTacticPlanBinding.inflate(layoutInflater)
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
        spYear.setItems(yearList.map { it.toString() })
        spMonth.setItems(monthList.map { it.second })
        spStatus.setItems(statusList.map { it.title })

        with(args) {
            if (yearId != -1) {
                spYear.setSelection(yearList.indexOf(yearId))
            }
            if (monthId != -1) {
                spMonth.setSelection(monthList.map { it.first }.indexOf(monthId))
            }
            if (status != null) {
                spStatus.setSelection(statusList.indexOf(status))
                if (statusList.isEmpty()) {
                    spStatus.setItems(listOf(args.status?.title))
                }
            }
        }
        spStatus.setOnTouchListener { _, event ->
            if (statusList.isEmpty() && event.action == MotionEvent.ACTION_DOWN) {
                viewModel.initStatusList()
            } else {
                spStatus.performClick()
            }
            false
        }
        btnSubmit.setOnClickListener {
            val isInputCompleted = isInputCompleted(
                listOf(
                    Triple(
                        etTacticPlan.text.isNullOrBlank(),
                        etTacticPlan,
                        "Taktik rejani kiriting"
                    ),
                    Triple(spYear.selectedItem == null, tvYear, "Yilni tanlang"),
                    Triple(spMonth.selectedItem == null, tvMonth, "Oyni tanlang"),
                    Triple(spStatus.selectedItem == null, tvStatus, "Statusni tanlang"),
                ), null
            )

            if (isInputCompleted) {
                viewModel.createTacticPlan(
                    CreateTacticPlanRequest(
                        "${yearList[spYear.selectedItemPosition]}-${monthList[spMonth.selectedItemPosition].first.toTwoDigit()}-01",
                        statusList.getOrNull(spStatus.selectedItemPosition)?.id ?: args.status!!.id,
                        etTacticPlan.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() = with(viewModel) {
        statusList.simpleCollect(
            this@CreateTacticPlanDialogFragment,
            binding.pbLoadingStatus
        ) { statuses ->
            this@CreateTacticPlanDialogFragment.statusList.clear()
            this@CreateTacticPlanDialogFragment.statusList.addAll(statuses)
            binding.spStatus.setItems(this@CreateTacticPlanDialogFragment.statusList.map { it.title })
            if (args.status != null) binding.spStatus.setSelection(
                this@CreateTacticPlanDialogFragment.statusList.indexOf(
                    args.status
                )
            )
            binding.pbLoadingStatus.isVisible = false
        }
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
                        sharedViewModel.setTacticPlanNeedsRefresh(true)
//                        showToast("Taktik reja muvaffaqiyatli qo'shildi")
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

    private fun initYearList(): List<Int> {
        val currentYear = LocalDate.now().year
        return listOf(
            currentYear - 1,
            currentYear,
            currentYear + 1,
            currentYear + 2,
            currentYear + 3
        )
    }

    private fun initMonthList() = listOf(
        Pair(1, "Yanvar"),
        Pair(2, "Fevral"),
        Pair(3, "Mart"),
        Pair(4, "Aprel"),
        Pair(5, "May"),
        Pair(6, "Iyun"),
        Pair(7, "Iyul"),
        Pair(8, "Avgust"),
        Pair(9, "Sentyabr"),
        Pair(10, "Oktyabr"),
        Pair(11, "Noyabr"),
        Pair(12, "Dekabr")
    )
}