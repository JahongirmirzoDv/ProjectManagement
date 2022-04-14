package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.functional

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DeleteDialogEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.TaskCommentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.UpdateTaskRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.*
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentTaskDetailUpdateBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.HistoryUpdatedAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.TaskCommentAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateBaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional.*
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/6/21 10:00 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.tasks.functional
 **/
/**
 * Vazifa detail-edit oynasi
 */
@AndroidEntryPoint
class TaskDetailUpdateFragment :
    DetailUpdateBaseFragment<TaskDetails>() {

    private var _binding: FragmentTaskDetailUpdateBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private var parentType = "project"
    private val folderList by lazy { mutableListOf<TaskFolderListItem>() }

    //    private val repeatList by lazy { mutableListOf<RepetitionData>() }
    private val projectList by lazy { mutableListOf<ProjectData>() }
    private val taskList by lazy { mutableListOf<FolderItem>() }
    private val comments by lazy { mutableListOf<TaskCommentData>() }


    //    private val commentAdapter by lazy { TaskCommentAdapter(isEditorMode, isCreator()) }
    private lateinit var commentAdapter: TaskCommentAdapter
    private val historyAdapter: HistoryUpdatedAdapter by lazy { HistoryUpdatedAdapter() }

    private val taskId by lazy { TaskDetailUpdateFragmentArgs.fromBundle(requireArguments()).taskId }
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

//    private val listRepetitionServer = mutableListOf<String>()


    @Inject
    lateinit var viewModelFactory: TaskDetailUpdateViewModelFactory
    private val viewModel by viewModels<TaskDetailUpdateViewModel> {
        provideFactory(
            viewModelFactory,
            taskId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailUpdateBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        listRepetitionServer.addAll(resources.getStringArray(R.array.list_repeat_server))
        setObservers()
    }

    override fun setupViews() = with(binding) {
        this@TaskDetailUpdateFragment.repeatLayout = repeatLayout
        this@TaskDetailUpdateFragment.repeatTypeLayout = repeatTypeLayout
        this@TaskDetailUpdateFragment.reminderDateLayout = reminderDateLayout
        this@TaskDetailUpdateFragment.repeatText = repeatText
        this@TaskDetailUpdateFragment.repeatTypeText = repeatTypeText
        this@TaskDetailUpdateFragment.reminderDateText = reminderDateText
        ivBackButton = imgBackButton
        etName = etTaskName
        tvFileCount = fileCount
        ivPerformerAvatar = personPerformerImageView
        ivLeaderAvatar = personMasterImageView
        ivSelectTimeRule = ivTimeEditRule
        this@TaskDetailUpdateFragment.tvStartDate = tvStartDate
        this@TaskDetailUpdateFragment.tvStartTime = tvStartTime
        this@TaskDetailUpdateFragment.tvEndDate = tvEndDate
        this@TaskDetailUpdateFragment.tvEndTime = tvEndTime
        ivStatusIcon = statusImage
        ivImportanceIcon = importanceIcon
        ivBlueButton = createTaskBlueButton
        this@TaskDetailUpdateFragment.ivAddObserver = addObserverPerson
        this@TaskDetailUpdateFragment.tvObserversCount = observerCount
        vgStatus = statusLayout
        vgImportance = importanceLayout
        vgStartTime = taskBeginningTimeLayout
        vgEndTime = taskEndingTimeLayout
        vgObservers = cvObserversLayout
        vgLeader = cvMasterLayout
        vgPerformer = cvPerformerLayout
        baseViewModel = viewModel


//        etDescription = etTaskDescription
//        etYaqm = etTaskYaQM
//        tvPerformerName = namePerformer
//        tvLeaderName = nameMaster
//        this@TaskDetailUpdateFragment.tvInternalStatusTitle = tvStatusTitle
//        this@TaskDetailUpdateFragment.tvCommentTitle = tvCommentTitle
//        this@TaskDetailUpdateFragment.tvCommentReject = tvCommentReject
//        this@TaskDetailUpdateFragment.tvCommentCount = tvCommentCount
//        this@TaskDetailUpdateFragment.commentLayout = commentLayout
//        tvImportanceTitle = importanceText
//        this@TaskDetailUpdateFragment.rvFiles = rvFiles
//        rvParticipants = participantRecycler
//        rvObservers = recyclerObserve
//        rvFunctionalGroup = functionalRecycler
//        ivAddParticipant = participantsAdd
//        ivAddFunctionalGroup = functionalGroupAdd
//        addFile = fileAdd
//        vgParticipants = taskParticipantLayout
//        vgFunctionalGroup = taskFunctionalGroupLayout
//        ivArrowParticipants = imageViewPart
//        ivArrowObservers = imgDropDownOb
//        ivFunctionalGroup = imgDropDownFun

    }


    override fun initViews() {
        super.initViews()

        with(binding) {
            commentAdapter = TaskCommentAdapter()
            rvComments.adapter = commentAdapter
            rvHistorys.adapter = historyAdapter
            listOf(
                spinnerTaskFolder,
                projectListSpinner,
            ).forEach { view ->
                view.isEnabled = isEditorMode && isCreator()
            }
        }
    }

    override fun setDataToViews(data: TaskDetails) {
        super.setDataToViews(data)

        with(binding) {

//            repeatText.text = when (data.repeat) {
//                listRepetitionServer[2] -> getString(R.string.every_week)
//                listRepetitionServer[3] -> getString(R.string.every_month)
//                listRepetitionServer[4] -> getString(R.string.every_year)
//                listRepetitionServer[1] -> getString(R.string.every_day)
//                else -> getString(R.string.once)
//            }
            spinnerTaskFolder.setItems(listOf(data.folder?.title))
            projectListSpinner.setItems(listOf(data.parent?.title))
            swShowCalendar.isChecked = data.isShowCalendar
            viewModel.isShowCalendar = data.isShowCalendar
            historyAdapter.submitList(data.updatesHistory)
            tvHistorysCount.text = (data.updatesHistory?.size ?: 0).toString()
        }
    }

    override fun setObservers() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)
                ?.observe(viewLifecycleOwner, this@TaskDetailUpdateFragment::setLeader)

            this?.getLiveData<PersonData?>(PERFORMER)
                ?.observe(viewLifecycleOwner, this@TaskDetailUpdateFragment::setPerformer)

            this?.getLiveData<List<PersonData>?>(OBSERVERS)
                ?.observe(viewLifecycleOwner, this@TaskDetailUpdateFragment::setObserverPersons)

            this?.getLiveData<List<Int>>("deleted_files")
                ?.observe(viewLifecycleOwner) {
                    deletedFiles.clear()
                    deletedFiles.addAll(it)
                }

            this?.getLiveData<List<File>?>("new_files")
                ?.observe(viewLifecycleOwner) {
                    newFiles.clear()
                    newFiles.addAll(it)
                }

            this?.getLiveData<List<FilesItem>?>("files_for_adapter")
                ?.observe(viewLifecycleOwner) {
                    dataHolder.filesForAdapter = it.toMutableList()
                    tvFileCount.text = "${it.size}"
                }
        }

        with(viewModel) {
            task.simpleCollect(
                this@TaskDetailUpdateFragment,
                binding.taskProgressBar.progressLoader
            ) { task ->
                if (task.commentaries?.size!! > 0) binding.tvCommentsCount.text =
                    task.commentaries.size.toString()

//                commentAdapter.submitList(task.commentaries)
                originalData = task
                setDataToViews(task)
                binding.taskProgressBar.progressLoader.isVisible = false
                binding.deleteBtn.isVisible = isCreator()
                ivBlueButton.isVisible = isCreatorOrPerformer() || task.canEditTime == true
            }
            updateResponse.simpleCollect(
                this@TaskDetailUpdateFragment,
                binding.taskProgressBar.progressLoader
            ) {
                makeSuccessSnack(R.string.edited_toast.toString())
                deletedFiles.clear()
                newFiles.clear()
                dataHolder.clear()
                if (isEditorMode) {
                    switchMode()
                }
                viewModel.initTask()

                viewModel.initTaskComments()

                binding.taskProgressBar.progressLoader.isVisible = false
                clearUpdateResponse()
                sharedViewModel.setTaskNeedsRefresh(true)
            }

            folders.simpleCollect(
                this@TaskDetailUpdateFragment,
                binding.taskProgressBar.progressLoader
            ) { folders ->
                folderList.clear()
                folderList.addAll(folders)
                if (isEditorMode) {
                    binding.spinnerTaskFolder.apply {
                        setItems(folderList.map { it.title })
                    }
                }
            }


            projects.simpleCollect(
                this@TaskDetailUpdateFragment,
                binding.taskProgressBar.progressLoader
            ) { projects ->
                projectList.clear()
                projectList.addAll(projects)
                if (isEditorMode) {
                    if (parentType == "project") {
                        binding.projectListSpinner.apply {
                            setItems(projectList.map { it.title })
//                            setSelection(projectList.map { it.id }.indexOf(originalData?.parent?.id))
                        }
                        binding.taskProgressBar.progressLoader.isVisible = false
                    }
                } else {
                    binding.taskProgressBar.progressLoader.isVisible = false
                }
            }

            taskComments.simpleCollect(
                this@TaskDetailUpdateFragment,
                binding.taskProgressBar.progressLoader
            ) { taskComments ->

                Log.d("TTTTTTT", "$taskComments")

                comments.clear()
                comments.addAll(taskComments)
                commentAdapter.submitList(taskComments.toMutableList())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.isTaskDeleted.collect { isDeleted ->
                    if (isDeleted) {
                        findNavController().navigateUp()
                        sharedViewModel.setTaskDeleted(false)
                    }
                }
            }
        }
    }

    override fun setClickListeners() {
        super.setClickListeners()

        with(binding) {

            binding.deleteBtn.isVisible = isCreator()
            spinnerTaskFolder.onItemSelected { position ->
                folderList.getOrNull(position)?.let {
                    dataHolder.folder = FolderData(it.id, it.title)
                }
            }
            spinnerTaskFolder.setOnTouchListener { _, event ->
                if (isEditorMode && folderList.isEmpty() && event.action == MotionEvent.ACTION_DOWN) {
                    viewModel.initFolders()
                } else {
                    spinnerTaskFolder.performClick()
                }
                false
            }

            commentsLayout.setDropDownClick(
                requireContext(),
                rvComments,
                null
            )
            historyLayout.setDropDownClick(
                requireContext(),
                rvHistorys,
                null
            )

//            repeatLayout.setOnClickListener {
//                if (isEditorMode && isCreator()) {
//                    showRepeatNoteDialog(this@TaskDetailUpdateFragment::setRepeat)
//                }
//            }

            commentAdapter.setOnClickCommentClickListener {
                storage.parentTaskId = it.id!!
                storage.commentType = SIMPLE_COMMENT_REPLY
                if (llCreateDiscussedTopic.isVisible) {
                    llCreateDiscussedTopic.isVisible = false
                    etDiscussedTopic.setText("")
                    ivAddComment.rotation = 0f
                    hideKeyboard()
                } else {
                    llCreateDiscussedTopic.isVisible = true
                    ivAddComment.rotation = 45f
                    etDiscussedTopic.requestFocus()
                }

            }

            ivAddComment.setOnClickListener {
                storage.commentType = SIMPLE_COMMENT
                if (llCreateDiscussedTopic.isVisible) {
                    llCreateDiscussedTopic.isVisible = false
                    etDiscussedTopic.setText("")
                    ivAddComment.rotation = 0f
                    hideKeyboard()
                } else {
                    llCreateDiscussedTopic.isVisible = true
                    ivAddComment.rotation = 45f
                    etDiscussedTopic.requestFocus()
                }
            }

            ivCreateComment.setOnClickListener {
                if (etDiscussedTopic.text.isNullOrBlank()) {
                    etDiscussedTopic.error = "Reja matnini kiriting"
                } else {
                    when (storage.commentType) {
                        SIMPLE_COMMENT -> {
                            val commentData = TaskCommentRequest(
                                text = etDiscussedTopic.text.toString(),
                                task = taskId
                            )
                            viewModel.postComment(commentData)
                        }
                        SIMPLE_COMMENT_REPLY -> {
                            val commentData = TaskCommentRequest(
                                parent = storage.parentTaskId,
                                text = etDiscussedTopic.text.toString(),
                                task = taskId
                            )
                            viewModel.postComment(commentData)
                        }
                    }

                    /*if (dataHolder.discussedTopics == null) {
                        dataHolder.discussedTopics = mutableListOf()
                    }*/

                    /* dataHolder.commentList.add(
                         *//*DiscussedTopic(
                            dataHolder.discussedTopics?.size
                                ?: 0 + newComments.size + deletedPlans.size,
                            etDiscussedTopic.text.toString()
                        )*//*
//                        TaskCommentData()
                    )*/
//                    newComments.add(etDiscussedTopic.text.toString())

//                    commentAdapter.submitList(dataHolder.commentList?.toMutableList())

                    tvCommentsCount.text = comments.size.toString()
                    /*dataHolder.comm?.size?.toString() ?: "0"*/
                    llCreateDiscussedTopic.isVisible = false
                    etDiscussedTopic.setText("")
                    ivAddComment.rotation = 0f
                    hideKeyboard()
                }
            }


            deleteBtn.setOnClickListener {
                findNavController().navigate(
                    TaskDetailUpdateFragmentDirections.toDeleteDialogFragment(
                        DeleteDialogEnum.TASK,
                        taskId
                    )
                )
            }

            projectListSpinner.onItemSelected { position ->
                if (parentType == "task") {
                    dataHolder.parent =
                        taskList.map { ParentData("task", it.id, it.title) }.getOrNull(position)
                } else {
                    dataHolder.parent =
                        projectList.map { ParentData("project", it.id!!, it.title!!) }
                            .getOrNull(position)
                }
            }

            cvOwner.setOnClickListener {
                it.showPersonTooltip(storage.userFirstName + " " + storage.userLastName)
            }

            ivBlueButton.setOnClickListener {
                if (!isEditorMode) {
                    switchMode()
                } else {
                    val validations = mutableListOf<Triple<Boolean, TextView, String>>()
                    etName?.let {
                        validations.add(
                            Triple(
                                it.text.isNullOrBlank(),
                                it,
                                "Vazifa nomini kiriting"
                            )
                        )
                    }
                    etYaqm?.let {
                        validations.add(
                            Triple(it.text.isNullOrBlank(), it, "Yaqm ni kiriting"),
                        )
                    }

                    val isInputCompleted =
                        if (validations.isEmpty()) true else isInputCompleted(
                            validations,
                            nestedScroll
                        )

                    if (isInputCompleted) {
                        viewModel.updateTask(
                            UpdateTaskRequest(
                                etName?.text.toString(),
                                etDescription?.text.toString(),
                                etYaqm?.text?.toString(),
                                dataHolder.performer?.id ?: originalData!!.performer?.id,
                                dataHolder.leader?.id ?: originalData!!.leader?.id,
                                tvStartDate.text.toString() + " " + tvStartTime.text.toString(),
                                tvEndDate.text.toString() + " " + tvEndTime.text.toString(),
                                deletedFiles,
                                newFiles,
                                dataHolder.status?.id ?: originalData!!.status?.id,
                                dataHolder.importance?.first ?: originalData!!.importance,
                                if (parentType == "task") dataHolder.parent?.id else null,
                                if (parentType == "project") dataHolder.parent?.id else null,
                                dataHolder.folder?.id ?: originalData!!.folder?.id,
                                if (dataHolder.participants != null) dataHolder.participants?.map { it.id } else originalData!!.participants?.map { it.id },
                                if (dataHolder.observers != null) dataHolder.observers?.map { it.id } else originalData!!.spectators?.map { it.id },
                                if (dataHolder.functionalGroup != null) dataHolder.functionalGroup?.map { it.id } else originalData!!.functionalGroup?.map { it.id },
                                dataHolder.internalStatus,
                                dataHolder.comment,
                                dataHolder.canEditTime ?: originalData?.canEditTime ?: false,
                                dataHolder.repeatString ?: originalData?.repeat,
                                if ((dataHolder.repeatString ?: originalData?.repeat)
                                    == listRepetitionServer[2]
                                ) {
                                    if (dataHolder.repeatWeekRule != 0) dataHolder.repeatWeekRule else originalData?.repeatRule
                                } else {
                                    if (dataHolder.repeatMonthRule != 0) dataHolder.repeatMonthRule else originalData?.repeatRule
                                }, viewModel.isShowCalendar
                            )
                        )
                    }
                }
            }
            swShowCalendar.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.isShowCalendar = isChecked
            }
        }
    }

    override fun switchMode() = with(binding) {
        super.switchMode()

        if (isEditorMode) {
            if (folderList.isEmpty()) {
                viewModel.initFolders()
            } else {
                spinnerTaskFolder.apply {
                    setItems(folderList.map { it.title })
                    setSelection(folderList.map { it.id }.indexOf(originalData?.folder?.id))
                }
            }

            if (projectList.isEmpty()) {
                viewModel.initProjects()
            } else {
                projectListSpinner.apply {
                    setItems(projectList.map { it.title })
                    setSelection(projectList.map { it.id }
                        .indexOf(originalData?.parent?.id))
                }
            }
        }
        listOf(
            spinnerTaskFolder,
            projectListSpinner
        ).forEach { view -> view.isEnabled = isEditorMode && isCreator() }

//        newComments.clear()

    }

    override fun onRemoteImageClick(item: FilesItem) {
        findNavController().navigate(
            TaskDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onLocalImageClick(item: FilesItem) {
        findNavController().navigate(
            TaskDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onResume() {
        super.onResume()
        binding.personAuthorImageView.loadImageUrl(storage.userImage)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun setRepeat(repeat: String) {
//        binding.repeatText.text = repeat
//
//        dataHolder.repeat = RepetitionData(
//            repeat,
//            when (repeat) {
//                getString(R.string.every_week) -> listRepetitionServer[2]
//                getString(R.string.every_month) -> listRepetitionServer[3]
//                getString(R.string.every_year) -> listRepetitionServer[4]
//                getString(R.string.every_day) -> listRepetitionServer[1]
//                else -> listRepetitionServer[0]
//            }
//        )
//    }
}