package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.goal_map.detail_goal_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalsItem
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDetailGoalMapBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map.detail_goal_map.DetailGoalMapGoalProjectTaskSubtaskAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map.detail_goal_map.DetailGoalMapViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class DetailGoalMapFragment : Fragment() {
    private var _binding: FragmentDetailGoalMapBinding? = null
    private val binding: FragmentDetailGoalMapBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val detailGoalMapViewModel: DetailGoalMapViewModel by viewModels()

    private lateinit var goalMapAdapter: DetailGoalMapGoalProjectTaskSubtaskAdapter

    private val args: DetailGoalMapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailGoalMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        binding.toolbar.title = args.title

        detailGoalMapViewModel.getGoalStructure(args.id)
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        detailGoalMapViewModel.getAllGoalMapsProjectsLiveData.observe(
            viewLifecycleOwner,
            allGoalMapObserver
        )
        detailGoalMapViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        detailGoalMapViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }


    private val allGoalMapObserver = Observer<List<GoalsItem>> {
        if (it.isEmpty()) {
            binding.tvError.show()
        } else {
            binding.tvError.gone()
            goalMapAdapter = DetailGoalMapGoalProjectTaskSubtaskAdapter(
                this::onGoalClick,
                this::onProjectClick,
                this::onTaskClick
            )
            goalMapAdapter.submitList(it)
            binding.goalMapRecycler.adapter = goalMapAdapter
        }

    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.detailGoalProgressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private fun loadViews() {

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnAdd.setOnClickListener {
                if (btnAddGoal.visibility == View.VISIBLE) {
                    btnAddGoal.gone()
                    btnAddGoal.gone()
                    btnAdd.rotation = 0F
                } else {
                    btnAddGoal.visible()
                    btnAddGoal.visible()
                    btnAdd.rotation = 45F
                }
            }

            btnAddGoal.setOnClickListener {
                findNavController().navigate(R.id.action_detailGoalMapFragment_to_createGoalFragment)
            }
        }
    }

    private fun onGoalClick(goalId: Int) {
        findNavController()
            .navigate(DetailGoalMapFragmentDirections.toGoalDetailsFragment(goalId))
    }

    private fun onProjectClick(projectId: Int) {
        findNavController()
            .navigate(DetailGoalMapFragmentDirections.toProjectDetailsFragment(projectId))
    }

    private fun onTaskClick(taskId: Int) {
        findNavController()
            .navigate(DetailGoalMapFragmentDirections.toTaskDetailsFragment(taskId))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}