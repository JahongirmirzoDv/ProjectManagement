package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.outsource

import android.annotation.SuppressLint
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentOutsourceBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.outsource.OutsourceWorkersAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.WorkersFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.OutsourceViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.phoneCall
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class OutsourceFragment : Fragment() {

    private var _binding: FragmentOutsourceBinding? = null
    private val binding: FragmentOutsourceBinding get() = _binding!!

    private val viewModel: OutsourceViewModel by viewModels()

    private lateinit var adapter: OutsourceWorkersAdapter
    private var checkedWorker: AllWorkersResponse.DataItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutsourceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObserver()
    }

    private fun loadViews() {
        binding.apply {
            rvTeam.layoutManager = LinearLayoutManager(requireContext())
            adapter = OutsourceWorkersAdapter()

            adapter.setOnClickDetailListener { workerData ->
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
                        workerData.id ?: 0
                    )
                )
            }

            adapter.setOnPhoneClickListener { workerData ->
                workerData.phoneNumber?.let { phoneCall(binding.root, it) }
            }

            adapter.setOnAddToFavouriteClickListener { workerData ->
                if (workerData.isFavourite!!)
                    viewModel.removeFromFavourites(workerData.id!!)
                else viewModel.addToFavourite(workerData.id!!)
                checkedWorker = workerData
            }

            adapter.setOnCalendarClickListener { workerData ->
                findNavController().navigate(
                    R.id.action_navigation_workers_to_workerCalendarFragment,
                    bundleOf(STAFF_ID to workerData.id)
                )
            }
            rvTeam.adapter = adapter
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObserver() {
        if (viewModel.outsourceWorkersLiveData.value?.isEmpty() != false) {
            viewModel.getOutsourceWorkers()
        }
        viewModel.outsourceWorkersLiveData.observe(viewLifecycleOwner, outsourceWorkersObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.addUserToFavouritesLiveData.observe(viewLifecycleOwner, addToFavouritesObserver)
        viewModel.removeFromFavouritesLiveData.observe(
            viewLifecycleOwner,
            removeFromFavouritesObserver
        )
    }

    private val outsourceWorkersObserver =
        Observer<List<AllWorkersResponse.DataItem>> { teamWorkers ->
            if (teamWorkers.isNotEmpty()) {
                adapter.submitList(teamWorkers)
                binding.txtEmptyResult.gone()
            } else {
                binding.txtEmptyResult.visible()
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

    private val addToFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            checkedWorker?.isFavourite = it.isFavourite
            adapter.notifyDataSetChanged()
        }
    }

    private val removeFromFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            checkedWorker?.isFavourite = it.isFavourite
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.outsourceWorkersLiveData.value?.isEmpty() != false) {
            viewModel.getOutsourceWorkers()
        }
    }
}