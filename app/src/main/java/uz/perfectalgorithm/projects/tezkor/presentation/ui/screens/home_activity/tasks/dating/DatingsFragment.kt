package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.dating

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
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDatingsBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.dating.DatingAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationMeetingFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationTasksFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.dating.DatingsViewModel
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh

/**
 * by farrukh_kh
 */
/**
 * Barcha uchrashuvlar ro'yxatini ko'rsatish oynasi
 */
@AndroidEntryPoint
class DatingsFragment : Fragment() {

    private var _binding: FragmentDatingsBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val datingAdapter by lazy { DatingAdapter(::onDatingClick) }
    private val viewModel by viewModels<DatingsViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() = with(binding) {
        rvDating.adapter = datingAdapter
        srlRefresh.setOnRefreshListener {
            viewModel.initDating()
        }
        fabAddDating.setOnClickListener {
            findNavController()
                .navigate(NavigationMeetingFragmentDirections.toCreateDatingFragment())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isDatingNeedsRefresh.collect { isAdded ->
                if (isAdded) {
                    if (viewModel.datings.value !is DataWrapper.Loading) {
                        viewModel.initDating()
                    }
                    sharedViewModel.setDatingNeedsRefresh(false)
                }
            }
        }

        viewModel.datings.simpleCollectWithRefresh(
            this,
            binding.srlRefresh,
            binding.tvError,
            binding.rvDating
        ) { data ->
            binding.srlRefresh.isRefreshing = false
            datingAdapter.submitList(data)
            binding.tvError.isVisible = data.isEmpty()
        }
    }

    private fun onDatingClick(datingId: Int) {
        findNavController().navigate(
            NavigationMeetingFragmentDirections.toDatingDetailsFragment(datingId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}