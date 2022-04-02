package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.tactic_plan

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanStatus
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentTacticPlansBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.tactic_plan.YearAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationTasksFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan.TacticPlansViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toStructuredList

/**
 * by farrukh_kh
 */
/**
 * Barcha taktik rejalar ro'yxatini ko'rsatish oynasi
 */
@AndroidEntryPoint
class TacticPlansFragment : Fragment() {

    private var _binding: FragmentTacticPlansBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val viewModel: TacticPlansViewModel by viewModels()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val yearsAdapter by lazy {
        YearAdapter(::onTacticPlanClick, ::onAddTacticPlanClick, ::changeItemStatus)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTacticPlansBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupObservers()

        viewModel.initTacticPlans()
        viewModel.initTacticPlanStatuses()
    }

    private fun setupViews() = with(binding) {
        rvYears.adapter = yearsAdapter
        srlRefresh.setOnRefreshListener {
            viewModel.initTacticPlanStatuses()
            viewModel.initTacticPlans()
        }
        fabAddTacticPlan.setOnClickListener {
            findNavController().navigate(
                NavigationTasksFragmentDirections.toCreateTacticPlanDialogFragment(
                    -1,
                    -1,
                    null
                )
            )
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isTacticPlanNeedsRefresh.collect { isAdded ->
                if (isAdded) {
                    viewModel.initTacticPlans()
                    viewModel.initTacticPlanStatuses()
                    sharedViewModel.setTacticPlanNeedsRefresh(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.combinedFlow.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.srlRefresh.isRefreshing = false
                        binding.tvError.isVisible = true
                        binding.rvYears.isVisible = false
                        handleException(dataWrapper.error)
                    }
                    is DataWrapper.Loading -> binding.srlRefresh.isRefreshing = true
                    is DataWrapper.Success -> {
                        yearsAdapter.submitList(dataWrapper.data.toStructuredList())
                        binding.tvError.isVisible =
                            dataWrapper.data.first.isNullOrEmpty() || dataWrapper.data.second.isNullOrEmpty()
                        binding.rvYears.isVisible =
                            dataWrapper.data.first.isNotEmpty() && dataWrapper.data.second.isNotEmpty()
                        binding.srlRefresh.isRefreshing = false
                    }
                }
            }
        }

        viewModel.changeStatusResponse.simpleCollect(this, binding.pbLoadingChangeStatus) {
            binding.pbLoadingChangeStatus.isVisible = false
        }
    }

    private fun onAddTacticPlanClick(yearId: Int, monthId: Int, status: Status) {
        findNavController().navigate(
            NavigationTasksFragmentDirections.toCreateTacticPlanDialogFragment(
                yearId,
                monthId,
                status
            )
        )
    }

    private fun changeItemStatus(tacticPlan: TacticPlan, status: TacticPlanStatus) {
        viewModel.changeStatus(tacticPlan.id, ChangeStatusRequest(status.id))
    }

    private fun onTacticPlanClick(tacticPlanId: Int) {
        findNavController().navigate(
            NavigationTasksFragmentDirections.toTacticPlanDetailUpdateFragment(
                tacticPlanId
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}