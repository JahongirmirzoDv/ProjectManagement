package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.UserErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.PermissionsListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreateDepartmentResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWorkersBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.SearchWorkersAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.CreateDepartmentDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.CreatePositionDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.workers.ViewStateWorkersPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.WorkersViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.phoneCall
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu

@AndroidEntryPoint
class WorkersFragment : Fragment() {

    private var _binding: FragmentWorkersBinding? = null
    private val binding: FragmentWorkersBinding get() = _binding!!

    private val viewModel: WorkersViewModel by activityViewModels()

    private lateinit var searchWorkersAdapter: SearchWorkersAdapter
    private  var dialogDepartment: CreateDepartmentDialog?= null
    private lateinit var dialogPosition: CreatePositionDialog

    private var checkedWorker: AllWorkersResponse.DataItem? = null

    private lateinit var viewPagerAdapter: ViewStateWorkersPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
        showAppBar()
        showBottomMenu()
        viewModel.getBelongsToUserPermissions()
        viewModel.getDepartmentListData()
        viewModel.getUserPermissionsList()
        viewModel.getAllWorkers()

    }

    private fun loadViews() {

        searchWorkersAdapter = SearchWorkersAdapter()
        searchWorkersAdapter.submitList(listOf())
        binding.rvSearchWorkers.adapter = searchWorkersAdapter

        searchWorkersAdapter.setOnAddToFavouriteClickListener { workerData ->
            if (workerData.isFavourite!!)
                viewModel.removeFromFavourites(workerData.id!!)
            else viewModel.addToFavourite(workerData.id!!)

            checkedWorker = workerData
        }

        searchWorkersAdapter.setOnClickDetailListener { workerData ->
            findNavController().navigate(
                WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
                    workerData.id ?: 0
                )
            )
        }

        searchWorkersAdapter.setOnPhoneClickListener { workerData ->
            workerData.phoneNumber?.let { phoneCall(binding.root, it) }
        }

        searchWorkersAdapter.setOnCalendarClickListener { workerData ->
            findNavController().navigate(
                R.id.action_navigation_workers_to_workerCalendarFragment,
                bundleOf(STAFF_ID to workerData.id)
            )
        }

        initTabLayout()

        binding.apply {
            btnSearch.setOnClickListener {
                openSearchbar()
            }
            btnCancelSearch.setOnClickListener {
                closeSearchBar()
            }
        }

        binding.functionsLayout.apply {

            btnAdd.setOnClickListener {
                viewModel.logDataForPerm()
                setUiPermissions()
            }

            btnAddWorker.setOnClickListener {
                setUiPermissions()
                findNavController().navigate(R.id.action_navigation_workers_to_createWorkerFragment)
            }

            btnAddDepartment.setOnClickListener {
                setUiPermissions()
                viewModel.getMDepartments()?.let { it1 ->
                    dialogDepartment =
                        CreateDepartmentDialog(
                            requireActivity(),
                            it1
                        )
                    dialogDepartment!!.show()

                    dialogDepartment!!.saveClickListener { title, parentId ->
                        viewModel.createDepartment(title, parentId)
                    }
                }
            }

            btnAddPosition.setOnClickListener {
                setUiPermissions()
                viewModel.getMDepartments()?.let { it1 ->
                    viewModel.getMPermissions()?.let { it2 ->
                        dialogPosition =
                            CreatePositionDialog(
                                requireActivity(),
                                it1,
                                it2
                            )
                        dialogPosition.show()

                        dialogPosition.saveClickListener { request ->
                            viewModel.createPosition(request)
                        }
                    }
                }
            }
        }

        binding.etSearch.addTextChangedListener {
            searchWorkersAdapter.submitList(
                searchFilterRECYCLER(
                    it.toString(),
                    viewModel.mWorkerList
                )
            )
        }

//        binding.viewPager.setOnClickListener {
//            setUiPermissions()
//        }
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

    private fun initTabLayout() {
        viewPagerAdapter = ViewStateWorkersPagerAdapter(requireActivity())
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = this.requireActivity().getString(R.string.command)
                1 -> tab.text = this.requireActivity().getString(R.string.list)
                2 -> tab.text = this.requireActivity().getString(R.string.structure)
                3 -> tab.text = this.requireActivity().getString(R.string.outsource)
            }
        }.attach()
    }

    private fun loadObservers() {

        viewModel.createDepartmentLiveData.observe(viewLifecycleOwner, createDepartmentObserver)
        viewModel.createPositionLiveData.observe(viewLifecycleOwner, createPositionObserver)
        viewModel.departmentListLiveData.observe(viewLifecycleOwner, departmentsObserver)
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver)
        viewModel.userResponseLiveData.observe(viewLifecycleOwner, userDataObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.addUserToFavouritesLiveData.observe(viewLifecycleOwner, addToFavouritesObserver)

        viewModel.removeFromFavouritesLiveData.observe(
            viewLifecycleOwner,
            removeFromFavouritesObserver
        )

        viewModel.permissionsLiveData.observe(viewLifecycleOwner, permissionListObserver)
        viewModel.belongsToUserPermissionsLiveData.observe(
            viewLifecycleOwner,
            belongsToUserPermissionsObserver
        )

    }

    private val permissionListObserver =
        Observer<List<PermissionsListResponse.PermissionData>> { permissions ->
            if (permissions != null) {
                viewModel.setMPermissions(permissions)
            }
        }

    private val belongsToUserPermissionsObserver = Observer<List<String>> { permissions ->
        if (permissions != null) {
            viewModel.setCorrectForUIPermissions(permissions)
//            setUiPermissions()
            viewModel.logDataForPerm()
        }
    }

    private val addToFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            checkedWorker?.isFavourite = it.isFavourite
            searchWorkersAdapter.notifyDataSetChanged()
        }
    }

    private val removeFromFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            checkedWorker?.isFavourite = it.isFavourite
            searchWorkersAdapter.notifyDataSetChanged()
        }
    }

    private val allWorkersObserver = Observer<List<AllWorkersResponse.DataItem>> { listWorkers ->
        searchWorkersAdapter.submitList(listWorkers)
        searchWorkersAdapter.notifyDataSetChanged()
        viewModel.mWorkerList = listWorkers
    }

    private val createDepartmentObserver = Observer<CreateDepartmentResponse.DepartmentData> {
        makeSuccessSnack(getString(R.string.department_created__success))
        dialogDepartment?.cancel()
    }

    private val createPositionObserver = Observer<CreatePositionResponse.PositionData> {
        makeSuccessSnack(getString(R.string.position_created_success))
        dialogPosition.cancel()
    }

    private val departmentsObserver =
        Observer<List<DepartmentListResponse.DepartmentDataItem>> { departments ->
            if (departments != null) {
                viewModel.setMDepartments(departments)
            }
        }

    private val userDataObserver = Observer<UserDataResponse.Data> { userData ->
        timberLog("User Id : ${userData.id}")
        timberLog("User email : ${userData.email}")
        viewModel.mUserData = userData
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val errorObserver = Observer<Throwable> { throwable ->
        if (throwable is Exception) {
            handleCustomException(throwable) { error ->
                when {
                    error.errors?.map { it.error }
                        ?.contains(UserErrorsEnum.USER_ID_REQUIRED.message) == true ||
                            error.errors?.map { it.error }
                                ?.contains(UserErrorsEnum.INCORRECT_USER.message) == true ->
                        makeErrorSnack("Xodim mavjud emas. Boshqa xodim tanlab ko'ring")
                    error.errors?.map { it.error }
                        ?.contains(UserErrorsEnum.INCORRECT_COMPANY.message) == true ->
                        makeErrorSnack("Bu xodim sizning kompaniyaga tegishli emas")
                }
            }
        } else {
            Throwable(throwable.message.toString())
        }
    }

    private fun setUiPermissions() {
        viewModel.isOpenAddFunctions = !viewModel.isOpenAddFunctions
        binding.functionsLayout.apply {
            if (viewModel.canAdd) btnAdd.visible()
            else btnAdd.gone()
            if (viewModel.isOpenAddFunctions) {

                binding.viewPager.setCurrentItem(2, false)

                if (viewModel.canAddDepartment) btnAddDepartment.visible()
                if (viewModel.canAddPosition) btnAddPosition.visible()
                if (viewModel.canAddUser) btnAddWorker.visible()
                btnAdd.rotation = 45.0f
            } else {
                btnAddDepartment.gone()
                btnAddPosition.gone()
                btnAddWorker.gone()
                btnAdd.rotation = 0f
            }
        }
    }
}