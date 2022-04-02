package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.structure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.DepartmentErrorEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.UserErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreateDepartmentResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentStructureBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure_new.DepartmentAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.DeleteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.DeleteWorkerDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.structure.UpdateDepartmentDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.WorkersFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.StructureViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.WorkerListViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.WorkersViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.phoneCall
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class StructureFragment : Fragment() {

    private var _binding: FragmentStructureBinding? = null
    private val binding get() = _binding!!
    private var open: Boolean = false

    private val viewModel: StructureViewModel by viewModels()
    private val viewModelWork: WorkerListViewModel by viewModels()
    private val shViewModel: WorkersViewModel by activityViewModels()

    private var chosenWorker: AllWorkersShort? = null

    //    private lateinit var adapter: StructureSectionAdapter
    private val adapter by lazy {
        DepartmentAdapter(
            this::deleteDepartment,
            this::updateDepartment,
            this::onWorkerMenuClick,
            this::onWorkerClick
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStructureBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
        initSectionAdapter()

        binding.apply {

            imgDropDown.setOnClickListener {
                if (open) {
                    imgDropDown.rotation = 0f
                    open = false
                } else {
                    imgDropDown.rotation = 180f
                    open = true
                }
                adapter.isOpen(open)
            }
        }
    }

    private fun initSectionAdapter() {
//        adapter = StructureSectionAdapter()
        binding.rvSections.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSections.adapter = adapter
//        adapter.setOnClickListener { dataItem, isVisibility ->
//
//        }
//        adapter.updateClickListener { title, id ->
//
//            updateDepartment(id, title)
//        }
//        adapter.deleteClickListener { id ->
//            deleteDepartment(id)
//        }
//
//        adapter.setOnClickMenuListener { dataItem, position ->
//            when (position) {
//                0 -> {
//                    findNavController().navigate(
//                        WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
//                            dataItem
//                        )
//                    )
//                }
//                1 -> {
//                    findNavController().navigate(
//                        WorkersFragmentDirections.actionNavigationWorkersToEditContactFragment(
//                            dataItem
//                        )
//                    )
//                }
//                2 -> {
//                    dataItem.id?.let { openDeleteDialog(it) }
//                }
//            }
//        }
    }

    private fun openDeleteDialog(id: Int) {
        val dialog = DeleteWorkerDialog(requireActivity())
        dialog.deleteClickListener {
            viewModel.deleteWorker(id)
        }
        dialog.show()
    }

    private fun deleteDepartment(id: Int) {
        val dialog = DeleteDialog(requireContext())
        dialog.deleteClickListener {
            viewModel.deleteDepartment(id)
        }
        dialog.show()
    }

    private fun updateDepartment(title: String, id: Int) {
        UpdateDepartmentDialog(requireContext(), title) { changeTitle ->
            viewModel.updateDepartment(id, changeTitle)
        }.show()
    }

    private fun loadObservers() {
        if (viewModel.structureLiveData.value?.isEmpty() != false) {
            viewModel.getStructureData()
        }
        viewModel.structureLiveData.observe(viewLifecycleOwner, structureObserver)
        viewModel.deleteWorkerLiveData.observe(viewLifecycleOwner, deleteWorkerObserver)
        viewModel.deleteDepartmentLiveData.observe(viewLifecycleOwner, deleteDepartmentObserver)
        viewModel.updateDepartmentLiveData.observe(viewLifecycleOwner, updateDepartmentObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)

        viewModelWork.addUserToFavouritesLiveData.observe(viewLifecycleOwner, addToFavouritesObserver)
        viewModelWork.removeFromFavouritesLiveData.observe(viewLifecycleOwner, removeFromFavouritesObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)

        shViewModel.createDepartmentLiveData.observe(viewLifecycleOwner, createDepartmentObserver)
        shViewModel.createPositionLiveData.observe(viewLifecycleOwner, createPositionObserver)

    }

    private val addToFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            chosenWorker?.isFavourite = it.isFavourite
            adapter.changeUI(chosenWorker!!)
        }
    }

    private val removeFromFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            chosenWorker?.isFavourite = it.isFavourite
            adapter.changeUI(chosenWorker!!)
        }
    }

    private val createDepartmentObserver = Observer<CreateDepartmentResponse.DepartmentData> {
        makeSuccessSnack(getString(R.string.department_created__success))
        viewModel.getStructureData()
    }

    private val createPositionObserver = Observer<CreatePositionResponse.PositionData> {
        makeSuccessSnack(getString(R.string.position_created_success))
        viewModel.getStructureData()
    }


    private val structureObserver = Observer<List<DepartmentStructureShort>> { structureData ->
        if (structureData != null) {
            if (structureData.isNotEmpty()) {
                binding.rvSections.adapter = adapter
                adapter.submitList(structureData)
//                adapter.notifyDataSetChanged()
            } else {
                binding.txtEmptyResult.visible()
            }
        } else {
            binding.txtEmptyResult.visible()
        }
    }

    private val deleteWorkerObserver = Observer<Unit> {
        makeSuccessSnack(getString(R.string.staff_deleted))
        viewModel.getStructureData()
    }
    private val deleteDepartmentObserver = Observer<Unit> {
        makeSuccessSnack(getString(R.string.department_deleted))
        viewModel.getStructureData()
    }
    private val updateDepartmentObserver = Observer<Unit> {
        makeSuccessSnack(getString(R.string.department_updated))
        viewModel.getStructureData()
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressLayout.progressLoader.visible()
        } else {
            binding.progressLayout.progressLoader.gone()
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
                    error.errors?.map { it.error }
                        ?.contains(DepartmentErrorEnum.DEPARTMENT_DELETE_NOT_PERMISSION.message) == true ->
                        makeErrorSnack("Bo'lim bo'sh emas")
                    else -> handleException(throwable)
                }
            }
        } else {
            makeErrorSnack(throwable.message.toString())
        }
    }

    private fun loadViews() {
        binding.apply {

        }
    }

    private fun onWorkerClick(workerData: AllWorkersShort, position: Int) {
        when(position) {
            0 -> {
                if (workerData.isFavourite!!)
                    viewModelWork.addToFavourite(workerData.id!!)
                else viewModelWork.removeFromFavourites(workerData.id!!)
                chosenWorker = workerData
            }
            1 -> {
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
                        workerData.id ?: 0
                    )
                )
            }
            2 -> {
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToEditContactFragment(
                        workerData.id ?: 0
                    )
                )
            }
            3 -> {
                findNavController().navigate(
                    R.id.action_navigation_workers_to_workerCalendarFragment,
                    bundleOf(STAFF_ID to workerData.id)
                )
            }
            4 -> {
                workerData.phoneNumber?.let { phoneCall(binding.root, it) }
            }
        }
    }

    private fun onWorkerMenuClick(dataItem: AllWorkersShort, position: Int) {
        when (position) {
            0 -> {
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
                        dataItem.id ?: 0
                    )
                )
            }
            1 -> {
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToEditContactFragment(
                        dataItem.id ?: 0
                    )
                )
            }
            2 -> {
                dataItem.id?.let { openDeleteDialog(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.structureLiveData.value?.isEmpty() != false) {
            viewModel.getStructureData()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (viewModel.structureLiveData.value?.isEmpty() != false) {
            viewModel.getStructureData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.getStructureData()
    }
}