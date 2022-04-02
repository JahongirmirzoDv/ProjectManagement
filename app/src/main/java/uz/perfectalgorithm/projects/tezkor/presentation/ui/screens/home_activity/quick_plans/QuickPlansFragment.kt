package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_plans

import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.LocalDate
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.QuickPlanByTimeEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.QuickPlanByTypeEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.TransitionStatusEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentQuickPlansBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.paging.ListLoadStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_plan.QuickPlanAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_plan.QuickPlanDayAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_plan.QuickPlanPagingAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_plans.QuickPlansViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.quick_plan.toMonthList
import uz.perfectalgorithm.projects.tezkor.utils.quick_plan.toWeekList
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toTwoDigit
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Jasurbek Kurganbaev on 26.06.2021 9:41
 **/
/**
 * Tezkor rejalar oynasi
 * Activity dagi quickButton click orqali ochiladi
 */
@AndroidEntryPoint
class QuickPlansFragment : Fragment() {

    companion object {
        var draggedItemID = 0
        var newParentItemID = 0
        var upperItemPosition: Int? = null
        var lowerItemPosition: Int? = null
    }

    private var _binding: FragmentQuickPlansBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private var selectedByTime = QuickPlanByTimeEnum.BY_WEEK
    private var selectedByType = QuickPlanByTypeEnum.ALL

    private var selectedDay = LocalDate()
    private var selectedWeek = LocalDate().withDayOfWeek(1)
    private var selectedMonth = LocalDate().withDayOfMonth(1)
    private var selectedYear = LocalDate().withDayOfYear(1)

    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val viewModel by viewModels<QuickPlansViewModel>()
    private var allQuickPlans: PagingData<QuickPlan>? = null
    private var quickPlansOfDay: List<QuickPlan>? = null
    private var quickPlansOfWeek: List<QuickPlan>? = null
    private var quickPlansOfMonth: List<QuickPlan>? = null
    private var quickPlansOfYear: List<QuickPlan>? = null

    private val allQuickPlansAdapter by lazy {
        QuickPlanPagingAdapter(
            this::onAddChildQuickPlan,
            this::onChangeStatus,
            this::onEditClick,
            this::onVisibleMainETListener,
            this::onDropItemListener
        )
    }
    private val quickPlanDayAdapter by lazy {
        QuickPlanDayAdapter(
            this::onAddChildQuickPlan,
            this::onChangeStatus,
            this::onEditClick,
            this::onVisibleMainETListener,
            this::onDropItemListener,
            requireContext()
        )
    }
    private val quickPlanAdapter by lazy {
        QuickPlanAdapter(
            this::onAddChildQuickPlan,
            this::onChangeStatus,
            this::onEditClick,
            this::onVisibleMainETListener,
            this::onDropItemListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickPlansBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavigationIcon()
        hideQuickButton()
        hideBottomMenu()
        showAppBar()

        initViews()
        initObservers()
        initQuickPlans(selectedByTime)
        binding.apply {
            tvDate.text = selectedByTime.text.translateTimes(requireContext())
            tvState.text = when (selectedByType) {
                QuickPlanByTypeEnum.ALL -> resources.getString(R.string.all)
                QuickPlanByTypeEnum.DONE -> resources.getString(R.string.by_done)
                QuickPlanByTypeEnum.UNDONE -> resources.getString(R.string.do_not_done)
            }
            rvQuickPlans.adapter = when (selectedByTime) {
                QuickPlanByTimeEnum.BY_DAY -> quickPlanAdapter
                QuickPlanByTimeEnum.BY_WEEK -> quickPlanDayAdapter
                QuickPlanByTimeEnum.BY_MONTH -> quickPlanDayAdapter
                QuickPlanByTimeEnum.BY_YEAR -> quickPlanAdapter
                QuickPlanByTimeEnum.ALL -> allQuickPlansAdapter.withLoadStateHeaderAndFooter(
                    ListLoadStateAdapter { allQuickPlansAdapter.retry() },
                    ListLoadStateAdapter { allQuickPlansAdapter.retry() }
                )
            }
        }
    }

    private fun initViews() = with(binding) {
        srlRefresh.setOnRefreshListener {
            initQuickPlans(selectedByTime)
        }
        ivCalendarSelect.setOnClickListener {
            when (selectedByTime) {
                QuickPlanByTimeEnum.BY_DAY -> showDatePickerDialog(selectedDay) {
                    selectedDay = it
                    initByDay(selectedDay)
                }
                QuickPlanByTimeEnum.BY_WEEK -> showWeekPickerDialog(selectedWeek) {
                    selectedWeek = it
                    initByWeek(selectedWeek)
                }
                QuickPlanByTimeEnum.BY_MONTH -> showMonthPickerDialog(selectedMonth) {
                    selectedMonth = it
                    initByMonth(selectedMonth)
                }
                QuickPlanByTimeEnum.BY_YEAR -> showYearPickerDialog(selectedYear) {
                    selectedYear = it
                    initByYear(selectedYear)
                }
            }
        }
        ivCreate.setOnClickListener {
            if (!etQuickPlan.text.isNullOrBlank()) {
                etQuickPlan.error = null
                viewModel.createQuickPlan(
                    CreateQuickPlanRequest(
                        etQuickPlan.text.toString(),
                        when (selectedByTime) {
                            QuickPlanByTimeEnum.BY_DAY -> selectedDay.toUiDate()
                            QuickPlanByTimeEnum.BY_WEEK -> DateTime.now()
                                .toString(Formatter.SIMPLE_DAY_PATTERN)
                            QuickPlanByTimeEnum.BY_MONTH -> DateTime.now()
                                .toString(Formatter.SIMPLE_DAY_PATTERN)
                            QuickPlanByTimeEnum.BY_YEAR -> selectedYear.toUiDate()
                            QuickPlanByTimeEnum.ALL -> LocalDate().toUiDate()
                        }
                    )
                )
            } else {
                etQuickPlan.error = "Reja matnini kiriting"
            }
        }
        llDate.setOnClickListener {
            showQuickPlanTimeDialog(selectedByTime) {
                selectedByTime = it
                tvDate.text = it.text.translateTimes(requireContext())
                when (selectedByTime) {
                    QuickPlanByTimeEnum.BY_DAY -> setByDay(selectedDay)
                    QuickPlanByTimeEnum.BY_WEEK -> setByWeek(selectedWeek)
                    QuickPlanByTimeEnum.BY_MONTH -> setByMonth(selectedMonth)
                    QuickPlanByTimeEnum.BY_YEAR -> setByYear(selectedYear)
                    QuickPlanByTimeEnum.ALL -> setPaged()
                }
            }
        }
        llState.setOnClickListener {
            showQuickPlanTypeDialog(selectedByType) { selected ->
                selectedByType = selected
                when (selectedByType) {
                    QuickPlanByTypeEnum.ALL -> {
                        tvState.text = "Barchasi"
                        allQuickPlans?.let { data -> submitPagedData(data) }
                        quickPlansOfDay?.let { list -> quickPlanAdapter.submitList(list) }
                        quickPlansOfWeek?.let { list ->
                            quickPlanDayAdapter.submitList(list.toWeekList(selectedWeek))
                        }
                        quickPlansOfMonth?.let { list ->
                            quickPlanDayAdapter.submitList(list.toMonthList(selectedMonth))
                        }
                        quickPlansOfYear?.let { list -> quickPlanAdapter.submitList(list) }
                    }
                    QuickPlanByTypeEnum.DONE -> {
                        tvState.text = "Bajarilganlar"
                        allQuickPlans?.let { data -> submitPagedData(data.filter { it.isDone }) }
                        quickPlansOfDay?.let { list -> quickPlanAdapter.submitList(list.filter { it.isDone }) }
                        quickPlansOfWeek?.let { list ->
                            quickPlanDayAdapter.submitList(list.filter { it.isDone }
                                .toWeekList(selectedWeek))
                        }
                        quickPlansOfMonth?.let { list ->
                            quickPlanDayAdapter.submitList(list.filter { it.isDone }
                                .toMonthList(selectedMonth))
                        }
                        quickPlansOfYear?.let { list -> quickPlanAdapter.submitList(list.filter { it.isDone }) }
                    }
                    QuickPlanByTypeEnum.UNDONE -> {
                        tvState.text = "Bajarilmaganlar"
                        allQuickPlans?.let { data -> submitPagedData(data.filter { !it.isDone }) }
                        quickPlansOfDay?.let { list -> quickPlanAdapter.submitList(list.filter { !it.isDone }) }
                        quickPlansOfWeek?.let { list ->
                            quickPlanDayAdapter.submitList(list.filter { !it.isDone }
                                .toWeekList(selectedWeek))
                        }
                        quickPlansOfMonth?.let { list ->
                            quickPlanDayAdapter.submitList(list.filter { !it.isDone }
                                .toMonthList(selectedMonth))
                        }
                        quickPlansOfYear?.let { list -> quickPlanAdapter.submitList(list.filter { !it.isDone }) }
                    }
                }
            }
            etQuickPlan.setOnDragListener(smoothScrollForToDownListener)
            vToTop.setOnDragListener(smoothScrollForToUpListener)
        }
    }

    private fun initObservers() = with(viewModel) {
        quickPlansOfDay.simpleCollectWithRefresh(
            this@QuickPlansFragment,
            binding.srlRefresh
        ) { quickPlans ->
            this@QuickPlansFragment.quickPlansOfDay = quickPlans
            quickPlanAdapter.submitList(when (selectedByType) {
                QuickPlanByTypeEnum.ALL -> quickPlans
                QuickPlanByTypeEnum.DONE -> quickPlans.filter { it.isDone }
                QuickPlanByTypeEnum.UNDONE -> quickPlans.filter { !it.isDone }
                else -> throw Exception("There are only 3 items")
            })
            viewModel.submitQPList(quickPlans)
            binding.srlRefresh.isRefreshing = false
        }

        quickPlansOfWeek.simpleCollectWithRefresh(
            this@QuickPlansFragment,
            binding.srlRefresh
        ) { quickPlans ->
            this@QuickPlansFragment.quickPlansOfWeek = quickPlans
            quickPlanDayAdapter.submitList((when (selectedByType) {
                QuickPlanByTypeEnum.ALL -> quickPlans
                QuickPlanByTypeEnum.DONE -> quickPlans.filter { it.isDone }
                QuickPlanByTypeEnum.UNDONE -> quickPlans.filter { !it.isDone }
            }).toWeekList(selectedWeek))
            binding.srlRefresh.isRefreshing = false
        }

        quickPlansOfMonth.simpleCollectWithRefresh(
            this@QuickPlansFragment,
            binding.srlRefresh
        ) { quickPlans ->
            this@QuickPlansFragment.quickPlansOfMonth = quickPlans
            quickPlanDayAdapter.submitList(
                when (selectedByType) {
                    QuickPlanByTypeEnum.ALL -> quickPlans.toMonthList(selectedMonth)
                    QuickPlanByTypeEnum.DONE -> quickPlans.filter { it.isDone }
                        .toMonthList(selectedMonth)
                    QuickPlanByTypeEnum.UNDONE -> quickPlans.filter { !it.isDone }
                        .toMonthList(selectedMonth)
                }
            )
            binding.srlRefresh.isRefreshing = false
        }

        quickPlansOfYear.simpleCollectWithRefresh(
            this@QuickPlansFragment,
            binding.srlRefresh
        ) { quickPlans ->
            this@QuickPlansFragment.quickPlansOfYear = quickPlans
            quickPlanAdapter.submitList(when (selectedByType) {
                QuickPlanByTypeEnum.ALL -> quickPlans
                QuickPlanByTypeEnum.DONE -> quickPlans.filter { it.isDone }
                QuickPlanByTypeEnum.UNDONE -> quickPlans.filter { !it.isDone }
            })
            binding.srlRefresh.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            allQuickPlans.collectLatest { data ->
                this@QuickPlansFragment.allQuickPlans = data
                submitPagedData(when (selectedByType) {
                    QuickPlanByTypeEnum.ALL -> data
                    QuickPlanByTypeEnum.DONE -> data.filter { it.isDone }
                    QuickPlanByTypeEnum.UNDONE -> data.filter { !it.isDone }
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            allQuickPlansAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.srlRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
//                binding.btnRetry.isVisible = loadStates.refresh is LoadState.Error
//                binding.tvError.isVisible = loadStates.refresh is LoadState.Error

//                loadStates.refresh.let {
//                    if (it is LoadState.Error) {
//                        binding.tvError.text = it.error.message
//                    }
//                }
            }
        }

        createResponse.simpleCollectWithRefresh(
            this@QuickPlansFragment,
            binding.srlRefresh
        ) {
            binding.etQuickPlan.setText("")
            hideKeyboard()
            initQuickPlans(selectedByTime)
        }

        updateResponse.simpleCollectWithRefresh(
            this@QuickPlansFragment,
            binding.srlRefresh
        ) {
            initQuickPlans(selectedByTime)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isQuickPlanNeedsRefresh.collect { isChanged ->
                if (isChanged) {
                    initQuickPlans(selectedByTime)
                    sharedViewModel.setQuickPlanNeedsRefresh(false)
                }
            }
        }
    }

    private fun initQuickPlans(byTime: QuickPlanByTimeEnum) {
        when (byTime) {
            QuickPlanByTimeEnum.BY_DAY -> initByDay(selectedDay)
            QuickPlanByTimeEnum.BY_WEEK -> initByWeek(selectedWeek)
            QuickPlanByTimeEnum.BY_MONTH -> initByMonth(selectedMonth)
            QuickPlanByTimeEnum.BY_YEAR -> initByYear(selectedYear)
            QuickPlanByTimeEnum.ALL -> allQuickPlansAdapter.refresh()
        }
    }

    private fun onEditClick(quickPlanId: Int) {
        findNavController().navigate(
            QuickPlansFragmentDirections.toQuickPlanDetailUpdateFragment(
                quickPlanId, true
            )
        )
    }

    private fun onVisibleMainETListener(boolean: Boolean) {
        if (boolean) {
            binding.llCreate.visible()
        } else {
            binding.llCreate.gone()
        }
    }

    private fun onDropItemListener(status: String, newParentStatus: String) {
        when (status) {
            TransitionStatusEnum.INTO_PARENT.text -> {
                viewModel.changeParent(draggedItemID, newParentItemID)
            }
            TransitionStatusEnum.INTO_POSITION.text -> {
                viewModel.updateQuickPlanPosition(
                    id = draggedItemID,
                    upper = upperItemPosition,
                    lower = lowerItemPosition
                )
                upperItemPosition = null
                lowerItemPosition = null
            }
        }

        draggedItemID = 0; newParentItemID = 0

    }

    private fun updatePlanToTop() {
        viewModel.changePlanToTop(draggedItemID)
        draggedItemID = 0; newParentItemID = 0
    }

    private fun setByDay(date: LocalDate) {
        binding.rvQuickPlans.adapter = quickPlanAdapter
        initByDay(date)
    }

    private fun setByWeek(date: LocalDate) {
        binding.rvQuickPlans.adapter = quickPlanDayAdapter
        initByWeek(date)
    }

    private fun setByMonth(date: LocalDate) {
        binding.rvQuickPlans.adapter = quickPlanDayAdapter
        initByMonth(date)
    }

    private fun setByYear(date: LocalDate) {
        binding.rvQuickPlans.adapter = quickPlanAdapter
        initByYear(date)
    }

    private fun setPaged() {
        binding.rvQuickPlans.adapter = allQuickPlansAdapter.withLoadStateHeaderAndFooter(
            ListLoadStateAdapter { allQuickPlansAdapter.retry() },
            ListLoadStateAdapter { allQuickPlansAdapter.retry() }
        )
        allQuickPlansAdapter.refresh()
    }

    private fun submitPagedData(data: PagingData<QuickPlan>) =
        viewLifecycleOwner.lifecycleScope.launch {
            allQuickPlansAdapter.submitData(data)
        }

    private fun initByDay(date: LocalDate) {
        viewModel.initQuickPlansOfDay(date.toUiDate())
    }

    private fun initByWeek(date: LocalDate) {
        viewModel.initQuickPlansOfWeek(date.toUiDate())
    }

    private fun initByMonth(date: LocalDate) {
        viewModel.initQuickPlansOfMonth("${date.year}-${date.monthOfYear.toTwoDigit()}")
    }

    private fun initByYear(date: LocalDate) {
        viewModel.initQuickPlansOfYear(date.year.toString())
    }

    private fun onChangeStatus(id: Int, isDone: Boolean) {
        viewModel.changeStatus(id, isDone)
    }

    private fun onAddChildQuickPlan(createQuickPlanRequest: CreateQuickPlanRequest) {
        viewModel.createQuickPlan(createQuickPlanRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        showQuickButton()
    }

    private val smoothScrollForToUpListener = View.OnDragListener { view, dragEvent ->
        var oldY = 1000
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_LOCATION -> {
                val newY = dragEvent.y.roundToInt()
                if (oldY >= newY) {
                    binding.rvQuickPlans.smoothScrollBy(0, -350)
                    oldY = newY
                }
                true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                binding.imgToTop.visible()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                binding.imgToTop.gone()
                true
            }
            DragEvent.ACTION_DROP -> {
                try {
                    binding.imgToTop.gone()
                    updatePlanToTop()
                } catch (e: Exception) {
                    myLogD("${e.message.toString()}")
                }
                true
            }
            else -> {
                false
            }
        }
    }

    private val smoothScrollForToDownListener = View.OnDragListener { view, dragEvent ->
        var oldY = 0
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_LOCATION -> {
                val newY = dragEvent.y.roundToInt()
                if (oldY <= newY) {
                    binding.rvQuickPlans.smoothScrollBy(0, 350)
                    oldY = newY
                }
                true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                true
            }
            else -> {
                false
            }
        }
    }


}