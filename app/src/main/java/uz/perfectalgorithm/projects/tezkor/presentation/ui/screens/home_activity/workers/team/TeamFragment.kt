package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.team

import android.annotation.SuppressLint
import android.content.Context
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentTeamBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.team_workers.TeamWorkersAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.WorkersFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.TeamViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.phoneCall
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class TeamFragment : Fragment() {

    private var _binding: FragmentTeamBinding? = null
    private val binding: FragmentTeamBinding get() = _binding!!

    private val viewModel: TeamViewModel by viewModels()

    private lateinit var adapter: TeamWorkersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObserver()
    }

    private fun loadViews() {
        binding.apply {
            rvTeam.layoutManager = LinearLayoutManager(requireContext())
            adapter = TeamWorkersAdapter()
            rvTeam.adapter = adapter

            adapter.setOnPhoneClickListener { workerData ->
                workerData.phoneNumber?.let { phoneCall(binding.root, it) }
            }

            adapter.setOnClickEditContactListener { workerData ->
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToEditContactFragment(
                        workerData.id ?: 0
                    )
                )
            }

            adapter.setOnClickDetailListener { workerData ->
                findNavController().navigate(
                    WorkersFragmentDirections.actionNavigationWorkersToWorkerDetailFragment(
                        workerData.id ?: 0
                    )
                )
            }

            adapter.setOnCalendarClickListener { workerData ->
                findNavController().navigate(
                    R.id.action_navigation_workers_to_workerCalendarFragment,
                    bundleOf(STAFF_ID to workerData.id)
                )
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObserver() {
        if (viewModel.teamWorkersLiveData.value?.isEmpty() != false) {
            viewModel.getTeamWorkers()
        }
        viewModel.teamWorkersLiveData.observe(viewLifecycleOwner, teamWorkersObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val teamWorkersObserver = Observer<List<AllWorkersShort>> { teamWorkers ->
        if (teamWorkers.isNotEmpty()) {
            adapter.submitList(teamWorkers)
            binding.txtEmptyResult.gone()
        } else {
            adapter.submitList(listOf())
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

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.teamWorkersLiveData.value?.isEmpty() != false) {
            viewModel.getTeamWorkers()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myLogD("OnAttach")
    }

}