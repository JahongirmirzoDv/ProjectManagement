package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.select_person

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPerformerSelectedBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person.AllStaffSelectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person.AllStaffSelectViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh
import javax.inject.Inject


/**
 *Created by farrukh_kh on 8/9/21 9:13 PM
 *uz.rdo.projects.projectmanagement.utils.adding
 **/
/**
 * Create va edit oynalarida xodim tanlashda ko'rsatiladigan oyna
 * (parent fragment, o'zida viewPager da bir nechta fragmentlarni ko'rsatadi)
 */
@AndroidEntryPoint
class SelectPersonFragment : Fragment() {
    private var _binding: FragmentPerformerSelectedBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val title by lazy { SelectPersonFragmentArgs.fromBundle(requireArguments()).title }
    private val navController by lazy { findNavController() }


    private var personData: PersonData? = null
    private var personList: List<PersonData>? = null
    private val pagerAdapter by lazy { PagerStateAdapter(childFragmentManager, lifecycle) }


    private val selectionList = mutableSetOf<AllWorkersShort>()
    private val workersAdapter by lazy { AllStaffSelectAdapter(this::onWorkerClick) }
    private val viewModel by viewModels<AllStaffSelectViewModel>()


    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerformerSelectedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideKeyboard()
        hideAppBar()
        hideBottomMenu()

        binding.titleAppbar.text = title

        initTabLayout()
        initViews()
        initObservers()

        setResultDataListener(MASTER) {
            personData = it
        }
        setResultDataListener(PERFORMER) {
            personData = it
        }
        setResultListListener(PARTICIPANTS) {
            personList = it
        }
        setResultListListener(OBSERVERS) {
            personList = it
        }
        setResultListListener(FUNCTIONAL_GROUP) {
            personList = it
        }
    }

    private fun initViews() = with(binding) {

        rvSearchWorkers.adapter = workersAdapter

        binding.etSearch.addTextChangedListener {
            when (viewModel.workers.value) {
                is DataWrapper.Empty -> Unit
                is DataWrapper.Success -> {
                    if ((viewModel.workers.value as DataWrapper.Success<List<AllWorkersResponse.DataItem>>).data.isEmpty()) {
                        binding.emptyList.show()
                    } else {
                        binding.emptyList.gone()
                        workersAdapter.submitList(
                            searchFilterRECYCLERShort(
                                it.toString(),
                                (viewModel.workers.value as DataWrapper.Success<List<AllWorkersShort>>).data
                            ).sortedBy { it.fullName?.trim()?.lowercase() }
                        )
                    }
                }
            }
        }

        imgBackButton.setOnClickListener { navController.navigateUp() }
        btnSearch.setOnClickListener { openSearchbar() }
        btnCancelSearch.setOnClickListener { closeSearchBar() }

        imgSave.setOnClickListener {
            if (personData == null && personList.isNullOrEmpty()) {
                if (storage.participant == PERFORMER || storage.participant == MASTER) {
                    makeErrorSnack("Xodimni tanlang")
                } else {
                    makeErrorSnack("Xodimlarni tanlang")
                }
            } else {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    storage.participant,
                    if (storage.participant == PERFORMER || storage.participant == MASTER) personData else personList?.distinct()
                )
                if (storage.participant == PERFORMER) {
                    personData?.leader?.let {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            MASTER,
                            it
                        )
                    }
                }
                navController.navigateUp()
            }
        }
    }

    private fun initObservers() {
        viewModel.workers.simpleCollectWithRefresh(this, null) { workers ->
            workersAdapter.submitList(workers.sortedBy { it.fullName?.trim()?.lowercase() })
            selectionList.clear()
            selectionList.addAll(workers.filter { storage.persons.contains(it.id.toString()) })
            if (storage.participant == PERFORMER || storage.participant == MASTER) {
                selectionList.firstOrNull()?.let {
                    personData = it.toPersonData()
                }
            } else {
                personList = selectionList.mapNotNull { it.toPersonData() }
            }
        }
    }

    private fun initTabLayout() {
        binding.viewPager.apply {
            adapter = pagerAdapter
            TabLayoutMediator(binding.personSelectedTabLayout, this) { tab, position ->
                tab.text = resources.getStringArray(R.array.tab_in_perfomer)[position]
                setCurrentItem(tab.position, true)
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

    private fun onWorkerClick(worker: AllWorkersShort) {
        if (storage.participant == PERFORMER || storage.participant == MASTER) {
            personData = if (selectionList.contains(worker)) {
                selectionList.remove(worker)
                null
            } else {
                selectionList.clear()
                selectionList.add(worker)
                worker.toPersonData()
            }
            storage.persons = selectionList.map { it.id.toString() }.toSet()
        } else {
            if (selectionList.contains(worker)) {
                selectionList.remove(worker)
            } else {
                selectionList.add(worker)
            }
            storage.persons = selectionList.map { it.id.toString() }.toSet()
            personList = selectionList.mapNotNull { it.toPersonData() }
        }
        workersAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}