package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.meeting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentMeetingsBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.MeetingAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationMeetingFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationTasksFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting.MeetingsViewModel
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh

/**
 * by farrukh_kh
 */
/**
 * Barcha majlislar ro'yxatini ko'rsatish oynasi
 */
@AndroidEntryPoint
class MeetingsFragment : Fragment() {

    private var _binding: FragmentMeetingsBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val meetingAdapter by lazy { MeetingAdapter(this::onMeetingClick) }
    private val viewModel by viewModels<MeetingsViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeetingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() = with(binding) {
        rvMeeting.adapter = meetingAdapter
        srlRefresh.setOnRefreshListener {
            viewModel.initMeetings()
        }
        fabAddMeeting.setOnClickListener {
            findNavController()
                .navigate(NavigationMeetingFragmentDirections.toCreateMeetingFragment())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isMeetingNeedsRefresh.collect { isChanged ->
                if (isChanged) {
                    if (viewModel.meetings.value !is DataWrapper.Loading) {
                        viewModel.initMeetings()
                    }
                    sharedViewModel.setMeetingNeedsRefresh(false)
                }
            }
        }

        viewModel.meetings.simpleCollectWithRefresh(
            this,
            binding.srlRefresh,
            binding.tvError,
            binding.rvMeeting
        ) { data ->
            binding.srlRefresh.isRefreshing = false
            meetingAdapter.submitList(data)
            binding.tvError.isVisible = data.isEmpty()
        }
    }

    private fun onMeetingClick(meetingId: Int) {
        findNavController().navigate(
            NavigationMeetingFragmentDirections.toMeetingDetailsFragment(meetingId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}