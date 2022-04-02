package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.goal_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateGoalMapBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map.GoalMapRVAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.create_goal_map.CreateMapDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map.CreateGoalMapViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject

/**
 * GoalMap(Maqsad xarita) yaratish ekrani
 * to'liq holda tayyor
 */
@AndroidEntryPoint
class CreateGoalMapFragment : Fragment() {
    private var _binding: FragmentCreateGoalMapBinding? = null
    private val binding: FragmentCreateGoalMapBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: CreateGoalMapViewModel by viewModels()

    private lateinit var goalMapAdapter: GoalMapRVAdapter

    @Inject
    lateinit var goalMapDialog: CreateMapDialog

    //    private var goalMapList = ArrayList<ItemGoalMapData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showAppBar()

        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.goalMapResponseLiveData.observe(viewLifecycleOwner, createGoalMapObserver)
        viewModel.getAllGoalMapsLiveData.observe(viewLifecycleOwner, getAllGoalMapObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.progressLayout.progressLoader
        if (it) progressView.show()
        else progressView.gone()
    }


    private fun loadViews() {
        goalMapAdapter = GoalMapRVAdapter()
        binding.recyclerGoalMap.adapter = goalMapAdapter

        goalMapAdapter.setOnItemGoalMapClickListener {
            hideAppBar()
            findNavController().navigate(
                CreateGoalMapFragmentDirections.actionCreateGoalMapFragmentToDetailGoalMapFragment(
                    it.id!!,
                    it.title!!
                )
            )

        }

        binding.addGoalsMapLayout.setOnClickListener {
            goalMapDialog.showSectionsBottomSheetDialog(
                requireContext(),
                "Maqsad xarita qo'shish"
            )
            {
                viewModel.createGoalMap(it)
            }
        }

        binding.apply {
            btnAdd.setOnClickListener {
                if (addMapBtn.visibility == View.VISIBLE) {
                    addMapBtn.gone()
                    btnAddProject.gone()
                    btnAddGoal.gone()
                    btnAdd.rotation = 0F
                } else {
                    addMapBtn.visible()
                    btnAddProject.visible()
                    btnAddGoal.visible()
                    btnAdd.rotation = 45F
                }
            }

            btnAddProject.setOnClickListener {
                findNavController().navigate(R.id.action_createGoalMapFragment_to_createProjectFragment)

            }

            addMapBtn.setOnClickListener {
                goalMapDialog.showSectionsBottomSheetDialog(
                    requireContext(),
                    "Maqsaf xarita qo'shish"

                ) {
                    viewModel.createGoalMap(it)
                }
            }

            btnAddGoal.setOnClickListener {
                findNavController().navigate(R.id.action_createGoalMapFragment_to_createGoalFragment)
            }
        }
        viewModel.getAllGoalMapData()


    }

    private val createGoalMapObserver = Observer<ItemGoalMapData> {
        if (binding.addGoalsMapLayout.visibility == View.VISIBLE) {
            binding.addGoalsMapLayout.gone()
            binding.addMapBtn.show()
        }
        val ls = goalMapAdapter.currentList.toMutableList()
        ls.add(it)
        goalMapAdapter.submitList(ls)
        binding.recyclerGoalMap.smoothScrollToPosition(ls.size - 1)
    }

    private val getAllGoalMapObserver = Observer<List<ItemGoalMapData>> {
        if (it.isNotEmpty()) {
            binding.addGoalsMapLayout.gone()
            binding.recyclerGoalMap.show()
            binding.btnAdd.show()
            goalMapAdapter.submitList(it)
        } else {
            binding.addGoalsMapLayout.show()
        }
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