package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.functional

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.hyuwah.draggableviewlib.DraggableView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DeleteDialogEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TasksByStatus
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentTasksBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional.AllTaskAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional.TaskFolderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationTasksFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.NavigationTasksViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional.TasksViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setupDraggableCustom

/**
 * by farrukh_kh
 */
/**
 * Barcha vazifalar ro'yxatini ko'rsatish oynasi
 */
@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private var isFirstLaunch = true
    private val viewModel by viewModels<TasksViewModel>()
    private val navigationViewModel by activityViewModels<NavigationTasksViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val allTaskAdapter by lazy { AllTaskAdapter(::onTaskClick) }
    private val folderAdapter by lazy {
        TaskFolderAdapter(
            ::onItemClick,
            ::onAddNewTask,
            ::onAddColumnClick,
            ::changeItemStatus,
            ::onTaskClick,
            ::onMenuClick,
            ::onFolderLongClick
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupObservers()
    }

    private fun setupViews() = with(binding) {
        fabAdd.setupDraggableCustom(
            stickyAxis = DraggableView.Mode.STICKY_X,
            onDragStarted = {
                fabAdd.parent.requestDisallowInterceptTouchEvent(true)
                binding.rootLayout.requestDisallowInterceptTouchEvent(true)
                binding.srlRefresh.requestDisallowInterceptTouchEvent(true)
                binding.rvTasks.requestDisallowInterceptTouchEvent(true)
            },
            onDragStopped = {
                fabAdd.parent.requestDisallowInterceptTouchEvent(false)
                binding.rootLayout.requestDisallowInterceptTouchEvent(false)
                binding.srlRefresh.requestDisallowInterceptTouchEvent(false)
                binding.rvTasks.requestDisallowInterceptTouchEvent(false)
            }
        )

        if (navigationViewModel.isFolderAdapter.value) {
            rvTasks.adapter = folderAdapter
        } else {
            rvTasks.adapter = allTaskAdapter
        }

        srlRefresh.setOnRefreshListener {
            if (navigationViewModel.isFolderAdapter.value) {
                viewModel.initFolders()
            } else {
                viewModel.initTasks()
            }
        }

        fabAdd.setOnClickListener {
            if (navigationViewModel.isFolderAdapter.value) {
                findNavController()
                    .navigate(NavigationTasksFragmentDirections.toCreateFolderDialogFragment(null))
            } else {
                findNavController().navigate(
                    NavigationTasksFragmentDirections.actionNavigationTaskToCreateTaskFragment()
                )
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            navigationViewModel.isFolderAdapter.collect { isFolderAdapter ->
                if (!isFirstLaunch) {
                    if (isFolderAdapter) {
                        binding.rvTasks.adapter = folderAdapter
                        viewModel.initFolders()
                    } else {
                        binding.rvTasks.adapter = allTaskAdapter
                        viewModel.initTasks()
                    }
                }
            }
        }

        with(sharedViewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                isFunctionalGroupNeedsRefresh.collect { isAdded ->
                    if (isAdded) {
                        viewModel.initFolders()
                        sharedViewModel.setFunctionalGroupNeedsRefresh(false)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                isTaskNeedsRefresh.collect { isAdded ->
                    if (isAdded) {
                        if (viewModel.tasks.value !is DataWrapper.Loading) {
                            if (navigationViewModel.isFolderAdapter.value) {
                                viewModel.initFolders()
                            } else {
                                viewModel.initTasks()
                            }
                        }
                        sharedViewModel.setTaskNeedsRefresh(false)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                isFunctionalColumnNeedsRefresh.collect { isAdded ->
                    if (isAdded) {
                        viewModel.initFolders()
                        sharedViewModel.setFunctionalColumnNeedsRefresh(false)
                    }
                }
            }
        }

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                folders.collect { dataWrapper ->
                    when (dataWrapper) {
                        is DataWrapper.Empty -> Unit
                        is DataWrapper.Error -> {
                            binding.srlRefresh.isRefreshing = false
                            binding.tvError.isVisible = true
                            binding.rvTasks.isVisible = false
                            handleException(dataWrapper.error)
                        }
                        is DataWrapper.Loading -> binding.srlRefresh.isRefreshing = true
                        is DataWrapper.Success -> {
                            folderAdapter.submitList(dataWrapper.data)
                            binding.srlRefresh.isRefreshing = false
                            binding.rvTasks.isVisible = true
                            binding.tvError.isVisible = dataWrapper.data.isEmpty()
                        }
                    }
                    isFirstLaunch = false
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                tasks.collect { dataWrapper ->
                    when (dataWrapper) {
                        is DataWrapper.Empty -> Unit
                        is DataWrapper.Error -> {
                            binding.srlRefresh.isRefreshing = false
                            binding.tvError.isVisible = true
                            binding.rvTasks.isVisible = false
                            handleException(dataWrapper.error)
                        }
                        is DataWrapper.Loading -> binding.srlRefresh.isRefreshing = true
                        is DataWrapper.Success -> {
                            binding.srlRefresh.isRefreshing = false
                            allTaskAdapter.submitList(dataWrapper.data)
                            binding.rvTasks.isVisible = true
                            binding.tvError.isVisible = dataWrapper.data.isEmpty()
                        }
                    }
                    isFirstLaunch = false
                }
            }


            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.createResponse.collect { dataWrapper ->
                    when (dataWrapper) {
                        is DataWrapper.Empty -> Unit
                        is DataWrapper.Error -> {
                            binding.srlRefresh.isRefreshing = false
                            handleException(dataWrapper.error)
                        }
                        is DataWrapper.Loading -> binding.srlRefresh.isRefreshing = true
                        is DataWrapper.Success -> {
                            binding.srlRefresh.isRefreshing = false
                            sharedViewModel.setTaskNeedsRefresh(true)
                        }
                    }
                }
            }

            changeStatusResponse.simpleCollect(
                this@TasksFragment,
                binding.pbLoadingChangeStatus
            ) {
                viewModel.initFolders()
                binding.pbLoadingChangeStatus.isVisible = false
            }
        }
    }

    private fun onAddNewTask(title: String, folderId: Int, statusId: Int) {
        viewModel.createTask(title, folderId, statusId)
    }

    private fun onAddColumnClick() {
        findNavController().navigate(
            NavigationTasksFragmentDirections
                .toCreateStatusDialogFragment(null)
        )
    }

    private fun onItemClick(position: Int) {
        binding.rvTasks.layoutManager.let {
            if (it is LinearLayoutManager) {
                it.scrollToPositionWithOffset(position, 0)
            } else {
                binding.rvTasks.layoutManager?.scrollToPosition(position)
            }
        }
    }

    private fun onMenuClick(status: Status, parent: View) {
        val menu = PopupMenu(requireContext(), parent)
        menu.inflate(R.menu.task_column_menu)

        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_edit -> {
                    findNavController().navigate(
                        NavigationTasksFragmentDirections.toCreateStatusDialogFragment(
                            status
                        )
                    )
                }
                R.id.item_delete -> {
                    findNavController().navigate(
                        NavigationTasksFragmentDirections.toDeleteDialogFragment(
                            DeleteDialogEnum.TASK_STATUS,
                            status.id!!
                        )
                    )
                }
            }
            true
        }

        menu.show()
    }

    private fun onFolderLongClick(folder: TaskFolderListItem, parent: View) {
        val menu = PopupMenu(requireContext(), parent)
        menu.inflate(R.menu.task_column_menu)

        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_edit -> {
                    findNavController().navigate(
                        NavigationTasksFragmentDirections.toCreateFolderDialogFragment(
                            folder
                        )
                    )
                }
                R.id.item_delete -> {
                    findNavController().navigate(
                        NavigationTasksFragmentDirections.toDeleteDialogFragment(
                            DeleteDialogEnum.TASK_FOLDER,
                            folder.id
                        )
                    )
                }
            }
            true
        }

        menu.show()
    }

    private fun changeItemStatus(item: FolderItem, status: TasksByStatus) {
        viewModel.changeStatus(item.id, ChangeStatusRequest(status.id))
    }

    private fun onTaskClick(taskId: Int) {
        findNavController()
            .navigate(NavigationTasksFragmentDirections.toTaskDetailUpdateFragment(taskId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}