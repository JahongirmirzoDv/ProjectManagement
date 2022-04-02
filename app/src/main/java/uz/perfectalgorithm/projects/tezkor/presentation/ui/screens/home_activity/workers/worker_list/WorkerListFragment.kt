package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.worker_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.UserErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWorkerListBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.worker_list.WorkerListAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.WorkersFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.WorkerListViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.phoneCall
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class WorkerListFragment : Fragment() {

    private var _binding: FragmentWorkerListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkerListViewModel by viewModels()

    private lateinit var wlAdapter: WorkerListAdapter

    private var chosenWorker: AllWorkersShort? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        initAllWorkersAdapter()
        binding.apply {

        }
    }

    private fun loadObservers() {
        if (viewModel.allWorkersLiveData.value?.isEmpty() != false) {
            viewModel.getAllWorkers()
        }
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver)
        viewModel.addUserToFavouritesLiveData.observe(viewLifecycleOwner, addToFavouritesObserver)
        viewModel.removeFromFavouritesLiveData.observe(
            viewLifecycleOwner,
            removeFromFavouritesObserver
        )
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val allWorkersObserver = Observer<List<AllWorkersShort>> { listWorkers ->

        var mList: List<AllWorkersShort> = listOf()

        if (listWorkers.isNotEmpty()) {

            mList = listWorkers.sortedBy {
                it.fullName!!.trim().toLowerCase()
            }
            wlAdapter.submitList(mList)
            binding.txtEmptyResult.gone()
        } else {
            binding.txtEmptyResult.visible()
        }
    }

    private val addToFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            chosenWorker?.isFavourite = it.isFavourite
            wlAdapter.setUIAddFavourite(chosenWorker!!)
        }
    }

    private val removeFromFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            chosenWorker?.isFavourite = it.isFavourite
            wlAdapter.setUIAddFavourite(chosenWorker!!)
        }
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
            makeErrorSnack(throwable.message.toString())
        }
    }

    private fun initAllWorkersAdapter() {
        wlAdapter = WorkerListAdapter()
        wlAdapter.submitList(listOf())
        binding.rvAllWorkers.layoutManager = LinearLayoutManager(requireContext())

        wlAdapter.setOnClickDetailListener { workerData ->
            findNavController().navigate(
                WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
                    workerData.id ?: 0
                )
            )
        }

        wlAdapter.setOnPhoneClickListener { workerData ->
            workerData.phoneNumber?.let { phoneCall(binding.root, it) }
        }

        wlAdapter.setOnAddToFavouriteClickListener { workerData ->
            if (workerData.isFavourite!!)
                viewModel.removeFromFavourites(workerData.id!!)
            else viewModel.addToFavourite(workerData.id!!)
            chosenWorker = workerData
        }

        wlAdapter.setOnCalendarClickListener { workerData ->
            findNavController().navigate(
                R.id.action_navigation_workers_to_workerCalendarFragment,
                bundleOf(STAFF_ID to workerData.id)
            )
        }

        wlAdapter.setOnClickEditContactListener { workerData ->
            findNavController().navigate(
                WorkersFragmentDirections.actionNavigationWorkersToEditContactFragment(
                    workerData.id ?: 0
                )
            )
        }

        binding.rvAllWorkers.adapter = wlAdapter
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.allWorkersLiveData.value?.isEmpty() != false) {
            viewModel.getAllWorkers()
        }
    }
}