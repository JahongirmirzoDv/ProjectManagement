package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDashboardSelectPersonBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person.DashboardStaffSelectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard.select_person.AllStaffSelectDashboardViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.FragmentPageSelectedListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultDepartmentDashboardListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultStaffDashboardListener
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh
import javax.inject.Inject

/**
 *Created by farrukh_kh on 10/1/21 10:44 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person
 **/
/**
 * Dashboard uchun xodim yoki bo'lim tanlash oynasi (umumiy parent fragment)
 */
@AndroidEntryPoint
class DashboardSelectPersonFragment : Fragment() {
    private var _binding: FragmentDashboardSelectPersonBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val title by lazy { DashboardSelectPersonFragmentArgs.fromBundle(requireArguments()).title }
    private val navController by lazy { findNavController() }
    private var selectedStaff: StaffBelow? = null
    private var selectedDepartment: DepartmentStructureBelow? = null
    private val selectionList = mutableSetOf<StaffBelow>()
    private val pagerAdapter by lazy { DashboardPagerStateAdapter(childFragmentManager, lifecycle) }
    private val workersAdapter by lazy { DashboardStaffSelectAdapter(this::onWorkerClick) }
    private val viewModel by viewModels<AllStaffSelectDashboardViewModel>()

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardSelectPersonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideKeyboard()
        hideAppBar()
        hideBottomMenu()

        initTabLayout()
        initViews()
        initObservers()

        setResultStaffDashboardListener {
            selectedStaff = it
            selectedDepartment = null
        }
        setResultDepartmentDashboardListener {
            selectedDepartment = it
            selectedStaff = null
        }
    }

    private fun initViews() = with(binding) {
        titleAppbar.text = title
        titleAppbar.isSelected = true
        rvSearchWorkers.adapter = workersAdapter

        etSearch.addTextChangedListener {
            viewModel.staffBelow.value.let { response ->
                when (response) {
                    is DataWrapper.Success -> {
                        if (response.data.isEmpty()) {
                            emptyList.show()
                        } else {
                            emptyList.gone()
                            workersAdapter.submitList(
                                searchFilterRecycler(
                                    it.toString(),
                                    response.data
                                ).sortedBy { it.firstName.trim().lowercase() }
                            )
                        }
                    }
                    is DataWrapper.Error -> {
                        makeErrorSnack("Xodimlar ro'yxati yuklanmagan")
                        emptyList.show()
                    }
                    is DataWrapper.Loading -> Unit
                    is DataWrapper.Empty -> Unit
                }
            }
        }

        imgBackButton.setOnClickListener {
//            if (selectedStaff != null || selectedDepartment != null) {
//            findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                DASHBOARD_STAFF,
//                selectedStaff
//            )
//            findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                DASHBOARD_DEPARTMENT,
//                selectedDepartment
//            )
//            }
            navController.navigateUp()
        }
        btnSearch.setOnClickListener {
            openSearchbar()
        }
        btnCancelSearch.setOnClickListener {
            closeSearchBar()
        }
        btnPersonal.setOnClickListener {
            selectedStaff = StaffBelow(storage.userId, "Shaxsiy", "")
            selectedDepartment = null
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                DASHBOARD_STAFF,
                selectedStaff
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                DASHBOARD_DEPARTMENT,
                selectedDepartment
            )
            navController.navigateUp()
        }

        imgSave.setOnClickListener {
//            findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                if (selectedDepartment == null) DASHBOARD_STAFF else DASHBOARD_DEPARTMENT,
//                selectedDepartment ?: selectedStaff
//            )
            if (selectedStaff == null && selectedDepartment == null) {
                makeErrorSnack("Xodim yoki bo'limni tanlang")
            } else {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    DASHBOARD_STAFF,
                    selectedStaff
                )
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    DASHBOARD_DEPARTMENT,
                    selectedDepartment
                )
                navController.navigateUp()
            }
        }
    }

    private fun initObservers() {
        viewModel.staffBelow.simpleCollectWithRefresh(this, null) { workers ->
            workersAdapter.submitList(workers.sortedBy { it.firstName.trim().lowercase() })
            //todo handle prev selected value (staff or department) on first collect
            selectionList.clear()
            selectionList.addAll(workers.filter { storage.persons.contains(it.id.toString()) })
//            binding.srlRefresh.isRefreshing = false
            selectionList.firstOrNull()?.let {
                selectedStaff = it
                storage.selectedDepartmentId = -1
            }
        }
    }

    private fun initTabLayout() {
        binding.viewPager.apply {
            adapter = pagerAdapter
            TabLayoutMediator(binding.personSelectedTabLayout, this) { tab, position ->
                tab.text = resources.getStringArray(R.array.tab_in_perfomer)[position]
                setCurrentItem(tab.position, false)
            }.attach()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val fragment = pagerAdapter.fragmentList[position]
                    if (fragment is FragmentPageSelectedListener) {
                        fragment.onFragmentSelected()
                    }
                }
            })
        }
    }

    private fun closeSearchBar() {
        binding.apply {
            tabRelativeLl.visible()
            btnSearch.visible()
            viewPager.visible()
            llSearchBar.gone()
            rvSearchWorkers.gone()
        }
    }

    private fun openSearchbar() {
        binding.apply {
            tabRelativeLl.inVisible()
            btnSearch.gone()
            viewPager.gone()
            llSearchBar.visible()
            rvSearchWorkers.visible()
        }
    }

    private fun onWorkerClick(worker: StaffBelow) {
        //todo handle adapter (maybe needs using selection list)
        selectedStaff = if (selectionList.contains(worker)) {
            selectionList.remove(worker)
            null
        } else {
            selectionList.clear()
            selectionList.add(worker)
            worker
        }
        storage.persons = selectionList.map { it.id.toString() }.toSet()
//        personData = worker.toPersonData()
        storage.selectedDepartmentId = -1
        workersAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
//        findNavController().previousBackStackEntry?.savedStateHandle?.set(
//            DASHBOARD_STAFF,
//            selectedStaff
//        )
//        findNavController().previousBackStackEntry?.savedStateHandle?.set(
//            DASHBOARD_DEPARTMENT,
//            null
//        )
        super.onDestroyView()
        _binding = null
    }
}