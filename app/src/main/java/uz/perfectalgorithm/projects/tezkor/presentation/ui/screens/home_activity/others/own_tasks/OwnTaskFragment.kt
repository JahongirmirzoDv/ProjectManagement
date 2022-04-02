package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.own_tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentOwnTaskBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.own_task.OwnTasksAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.own_tasks.OwnTaskViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException

/**
 * Shaxsiy vaifa(OwnTask) yaratish oynasi fragmenti
 **/
@AndroidEntryPoint
class OwnTaskFragment : Fragment() {

    private var _binding: FragmentOwnTaskBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val ownTasksAdapter by lazy { OwnTasksAdapter() }
    private val viewModel by viewModels<OwnTaskViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOwnTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()

    }

    private fun initViews() = with(binding) {
        ownTaskList.adapter = ownTasksAdapter
        srlRefresh.setOnRefreshListener {
            viewModel.initMeetings()
        }
    }


    private fun initObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isOwnTaskNeedsRefresh.collect { isChanged ->
                if (isChanged) {
                    viewModel.initMeetings()
                    sharedViewModel.setOwnTaskNeedsRefresh(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ownTask.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty ->
                        Unit

                    is DataWrapper.Error -> {
                        binding.srlRefresh.isRefreshing = false
                        binding.tvError.isVisible = true
                        binding.ownTaskList.isVisible = false
                        handleException(dataWrapper.error)
                    }
                    is DataWrapper.Loading -> binding.srlRefresh.isRefreshing = true
                    is DataWrapper.Success -> {
                        binding.srlRefresh.isRefreshing = false
                        binding.tvError.isVisible = false
                        binding.ownTaskList.isVisible = true
                        ownTasksAdapter.submitList(dataWrapper.data)
                        if (dataWrapper.data.isEmpty()) {
                            binding.tvError.text = getString(R.string.empty_meeting_list)
                            binding.tvError.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}