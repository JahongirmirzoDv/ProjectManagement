package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.project

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DeleteDialogEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.Project
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectsByStatus
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.Status
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentProjectsBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects.ProjectStatusAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationTasksFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project.ProjectsViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect

/**
 * by farrukh_kh
 */
/**
 * Barcha proyektlar ro'yxatini ko'rsatish oynasi
 */
@AndroidEntryPoint
class ProjectsFragment : Fragment() {

    private var _binding: FragmentProjectsBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val viewModel by viewModels<ProjectsViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val projectStatusAdapter by lazy {
        ProjectStatusAdapter(
            requireContext(),
            ::onAddProjectClick,
            ::onAddColumnClick,
            ::changeItemStatus,
            ::onProjectClick,
            ::onMenuClick
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() = with(binding) {
        dragBoardView.setHorizontalAdapter(projectStatusAdapter)
        fabAddProject.setOnClickListener {
            findNavController().navigate(NavigationTasksFragmentDirections.toCreateProjectFragment())
        }
        // TODO: 9/13/21 fix bug: using srl with dragboardview
//        dragBoardView.mRecyclerView.addOnItemTouchListener(object :
//            RecyclerView.OnItemTouchListener {
//
//            var lastX = 0.0f
//            var lastY = 0.0f
//
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//
//                when (e.action) {
////                    MotionEvent.ACTION_DOWN -> {
////                        rv.parent.requestDisallowInterceptTouchEvent(true)
////                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        if (dragBoardView.mDragHelper.isDraggingItem) {
//                            rv.parent.requestDisallowInterceptTouchEvent(false)
//                        } else {
//                            if (abs(lastX - e.x) > abs(lastY - e.y)) {
//                                rv.parent.requestDisallowInterceptTouchEvent(true)
//                                when {
//                                    lastX < e.x && rv.canScrollHorizontally(-1) -> {
//                                        rv.parent.requestDisallowInterceptTouchEvent(true)
//                                    }
//                                    lastY > e.x && rv.canScrollHorizontally(1) -> {
//                                        rv.parent.requestDisallowInterceptTouchEvent(true)
//                                    }
//                                    else -> {
//                                        rv.parent.requestDisallowInterceptTouchEvent(false)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        rv.parent.requestDisallowInterceptTouchEvent(false)
//                    }
//                }
//
//                lastX = e.x
//                lastY = e.y
//                return false
//            }
//
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//        })
//        srlRefresh.setOnRefreshListener {
//            viewModel.initProjects()
//        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isProjectNeedsRefresh.collect { isAdded ->
                if (isAdded) {
                    if (viewModel.projects.value !is DataWrapper.Loading) {
                        viewModel.initProjects()
                    }
                    sharedViewModel.setProjectNeedsRefresh(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isProjectColumnNeedsRefresh.collect { isAdded ->
                if (isAdded) {
                    viewModel.initProjects()
                    sharedViewModel.setProjectColumnNeedsRefresh(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.projects.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.pbLoadingChangeStatus.isVisible = false
                        binding.tvError.isVisible = true
                        binding.dragBoardView.isVisible = false
                        handleException(dataWrapper.error)
                    }
                    is DataWrapper.Loading -> binding.pbLoadingChangeStatus.isVisible = true
                    is DataWrapper.Success -> {
                        binding.pbLoadingChangeStatus.isVisible = false
                        binding.dragBoardView.isVisible = true
                        binding.dragBoardView.isVisible = true
                        projectStatusAdapter.submitList(dataWrapper.data)
                        binding.tvError.isVisible = dataWrapper.data.isEmpty()
                    }
                }
            }
        }

        viewModel.changeStatusResponse.simpleCollect(this, binding.pbLoadingChangeStatus) {
            viewModel.initProjects()
            binding.pbLoadingChangeStatus.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onAddProjectClick() {
        findNavController()
            .navigate(NavigationTasksFragmentDirections.toCreateProjectFragment())
    }

    private fun onAddColumnClick() {
        findNavController()
            .navigate(NavigationTasksFragmentDirections.toCreateStatusDialogFragment2(null))
    }

    private fun onMenuClick(status: Status, parent: View) {
        val menu = PopupMenu(requireContext(), parent)
        menu.inflate(R.menu.task_column_menu)

        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_edit -> {
                    findNavController().navigate(
                        NavigationTasksFragmentDirections.toCreateStatusDialogFragment2(
                            status
                        )
                    )
                }
                R.id.item_delete -> {
                    findNavController().navigate(
                        NavigationTasksFragmentDirections.toDeleteDialogFragment(
                            DeleteDialogEnum.PROJECT_STATUS,
                            status.id!!
                        )
                    )
                }
            }
            true
        }

        menu.show()
    }

    private fun changeItemStatus(item: Project, status: ProjectsByStatus) {
        viewModel.changeProjectStatus(item.id, ChangeStatusRequest(status.id))
    }

    private fun onProjectClick(projectId: Int) {
        findNavController()
            .navigate(NavigationTasksFragmentDirections.toProjectDetailUpdateFragment(projectId))
    }
}