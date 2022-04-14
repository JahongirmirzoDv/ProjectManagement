package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.project

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.projects.UpdateProjectRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentProjectDetailUpdateBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.DiscussedTopicAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateBaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project.ProjectDetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project.ProjectDetailUpdateViewModelFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project.provideFactory
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.adding.showConvertToDialog
import uz.perfectalgorithm.projects.tezkor.utils.adding.showPersonTooltip
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/16/21 11:16 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.tasks.project
 **/
/**
 * Proyekt detail-edit oynasi
 * TODO
 * 1. Maqsad ga bog'lash ni ixtiyoriy (not required) qilish kerak
 * 2. O'zgarishlar tarixi ni qo'shish kerak
 * 3. Proyekt rejasi (project_plan) ni proyekt/vazifaga convert qilish
 * logikasini o'zgartirish.
 * Hozir shunchaki create screenga o'tkazib yuborilyapti.
 * Figmadagi holatga keltirish kerak
 */
@AndroidEntryPoint
class ProjectDetailUpdateFragment : DetailUpdateBaseFragment<ProjectDetails>() {

    private var _binding: FragmentProjectDetailUpdateBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val goalList by lazy { mutableListOf<GoalData>() }
    private val projectId by lazy { ProjectDetailUpdateFragmentArgs.fromBundle(requireArguments()).projectId }
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val deletedPlans = mutableListOf<Int>()
    private val newPlans = mutableListOf<String>()
//    private val listRepetitionServer = mutableListOf<String>()
    private val discussedTopicAdapter by lazy {
        DiscussedTopicAdapter(
//            ::onDeleteDiscussedTopic,
            ::onDiscussedTopicMoreClick,
//            false
        )
    }

    @Inject
    lateinit var viewModelFactory: ProjectDetailUpdateViewModelFactory
    private val viewModel by viewModels<ProjectDetailUpdateViewModel> {
        provideFactory(
            viewModelFactory,
            projectId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectDetailUpdateBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        listRepetitionServer.addAll(resources.getStringArray(R.array.list_repeat_server))
        setObservers()
    }

    override fun setupViews() = with(binding) {
        this@ProjectDetailUpdateFragment.repeatLayout = repeatLayout
        this@ProjectDetailUpdateFragment.repeatTypeLayout = repeatTypeLayout
        this@ProjectDetailUpdateFragment.reminderDateLayout = reminderDateLayout
        this@ProjectDetailUpdateFragment.repeatText = tvRepeatText
        this@ProjectDetailUpdateFragment.repeatTypeText = repeatTypeText
        this@ProjectDetailUpdateFragment.reminderDateText = reminderDateText
        this@ProjectDetailUpdateFragment.ivBackButton = imgBackButton
        this@ProjectDetailUpdateFragment.etName = etProjectTitle
//        this@ProjectDetailUpdateFragment.etDescription = etProjectDescription
//        this@ProjectDetailUpdateFragment.etYaqm = etyqm
        this@ProjectDetailUpdateFragment.tvFileCount = tvFilesCount
        this@ProjectDetailUpdateFragment.ivPerformerAvatar = ivPerformer
        this@ProjectDetailUpdateFragment.ivLeaderAvatar = ivLeader
//        this@ProjectDetailUpdateFragment.tvPerformerName = projectNamePerformer
//        this@ProjectDetailUpdateFragment.tvLeaderName = projectNameMaster
        this@ProjectDetailUpdateFragment.ivSelectTimeRule = ivPinDateTime
        this@ProjectDetailUpdateFragment.tvStartDate = tvStartDate
        this@ProjectDetailUpdateFragment.tvStartTime = tvStartTime
        this@ProjectDetailUpdateFragment.tvEndDate = tvEndDate
        this@ProjectDetailUpdateFragment.tvEndTime = tvEndTime
        this@ProjectDetailUpdateFragment.ivStatusIcon = ivStatus
//        this@ProjectDetailUpdateFragment.tvInternalStatusTitle = tvStatusTitle
//        this@ProjectDetailUpdateFragment.tvCommentTitle = tvCommentTitle
//        this@ProjectDetailUpdateFragment.tvCommentReject = tvCommentReject
//        this@ProjectDetailUpdateFragment.tvCommentCount = tvCommentCount
//        this@ProjectDetailUpdateFragment.commentLayout = commentLayout
        this@ProjectDetailUpdateFragment.ivImportanceIcon = ivImportance
//        this@ProjectDetailUpdateFragment.tvImportanceTitle = importanceText
        this@ProjectDetailUpdateFragment.ivBlueButton = createProjectBlueButton
//        this@ProjectDetailUpdateFragment.rvFiles = rvFiles
//        this@ProjectDetailUpdateFragment.rvParticipants = rvParticipants
//        this@ProjectDetailUpdateFragment.rvObservers = recyclerObserve
//        this@ProjectDetailUpdateFragment.rvFunctionalGroup = functionalRecycler
//        this@ProjectDetailUpdateFragment.ivAddParticipant = participantsAdd
//        this@ProjectDetailUpdateFragment.addParticipants = cvParticipants
//        this@ProjectDetailUpdateFragment.ivAddObserver = observersAdd
        this@ProjectDetailUpdateFragment.addObservers = cvObservers
//        this@ProjectDetailUpdateFragment.ivAddFunctionalGroup = functionalGroupAdd
//        this@ProjectDetailUpdateFragment.addFile = fileAdd
        this@ProjectDetailUpdateFragment.addFileLayout = cvFiles
        this@ProjectDetailUpdateFragment.vgStatus = cvStatus
        this@ProjectDetailUpdateFragment.vgImportance = cvImportance
        this@ProjectDetailUpdateFragment.vgStartTime = startDateTimeLayout
        this@ProjectDetailUpdateFragment.vgEndTime = endDateTimeLayout
//        this@ProjectDetailUpdateFragment.vgParticipants = projectParticipantLayout
//        this@ProjectDetailUpdateFragment.vgObservers = projectObserversLayout
//        this@ProjectDetailUpdateFragment.vgFunctionalGroup = projectFunctionalGroupLayout
//        this@ProjectDetailUpdateFragment.ivParticipants = imageViewPart
//        this@ProjectDetailUpdateFragment.ivObservers = imgDropDownOb
//        this@ProjectDetailUpdateFragment.ivFunctionalGroup = imgDropDownFun
        this@ProjectDetailUpdateFragment.vgLeader = cvLeader
        this@ProjectDetailUpdateFragment.vgPerformer = cvPerformer
        this@ProjectDetailUpdateFragment.baseViewModel = viewModel
        this@ProjectDetailUpdateFragment.tvParticipantsCount = tvParticipantsCount
        this@ProjectDetailUpdateFragment.tvObserversCount = tvObserversCount
//        this@ProjectDetailUpdateFragment.ivParticipantsInCv = ivParticipants
        this@ProjectDetailUpdateFragment.ivObserversInCv = ivObservers
    }

    override fun initViews() {
        super.initViews()

        binding.rvPlans.adapter = discussedTopicAdapter
        binding.spGoals.isEnabled = isEditorMode && isCreator()
        binding.ivAddPlan.isVisible = isEditorMode
    }

    override fun setDataToViews(data: ProjectDetails) {
        super.setDataToViews(data)

        binding.spGoals.setItems(listOf(data.goal.title))

        if (data.creatorObject?.image != null) {
            binding.ivAuthor.loadImageUrl(data.creatorObject.image, R.drawable.ic_person)
        } else {
            binding.ivAuthor.setImageResource(R.drawable.ic_person)
        }
//        binding.tvRepeatText.text = when (data.repeat) {
//            listRepetitionServer[2] -> getString(R.string.every_week)
//            listRepetitionServer[3] -> getString(R.string.every_month)
//            listRepetitionServer[4] -> getString(R.string.every_year)
//            listRepetitionServer[1] -> getString(R.string.every_day)
//            else -> getString(R.string.once)
//        }

        if (dataHolder.discussedTopics == null) {
            dataHolder.discussedTopics = data.plans?.toMutableList()
        }
        discussedTopicAdapter.submitList(dataHolder.discussedTopics?.toMutableList())
        binding.tvPlansCount.text = dataHolder.discussedTopics?.size?.toString() ?: "0"
    }

    override fun setObservers() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)
                ?.observe(viewLifecycleOwner, this@ProjectDetailUpdateFragment::setLeader)

            this?.getLiveData<PersonData?>(PERFORMER)
                ?.observe(viewLifecycleOwner, this@ProjectDetailUpdateFragment::setPerformer)

            this?.getLiveData<List<PersonData>?>(PARTICIPANTS)
                ?.observe(viewLifecycleOwner, this@ProjectDetailUpdateFragment::setParticipants)

            this?.getLiveData<List<PersonData>?>(OBSERVERS)
                ?.observe(viewLifecycleOwner, this@ProjectDetailUpdateFragment::setObserverPersons)

            this?.getLiveData<List<Int>>(DELETED_FILES)
                ?.observe(viewLifecycleOwner) {
//                    deletedFiles.clear()
                    deletedFiles.addAll(it)
                }

            this?.getLiveData<List<File>?>(NEW_FILES)
                ?.observe(viewLifecycleOwner) {
                    newFiles.clear()
                    newFiles.addAll(it)
                }

            this?.getLiveData<List<FilesItem>?>(FILES_FOR_ADAPTER)
                ?.observe(viewLifecycleOwner) {
                    dataHolder.filesForAdapter = it.toMutableList()
                    tvFileCount.text = "${it.size}"
                }

//            this?.getLiveData<List<PersonData>?>(FUNCTIONAL_GROUP)
//                ?.observe(viewLifecycleOwner, this@ProjectDetailUpdateFragment::setFunctionalGroup)
        }

        with(viewModel) {
            project.simpleCollect(
                this@ProjectDetailUpdateFragment,
                binding.progressLayout.progressLoader
//                binding.createProjectProgressBar
            ) { project ->
                originalData = project
                setDataToViews(project)
                binding.progressLayout.progressLoader.isVisible = false
                binding.btnDeleteProject.isVisible = isCreator()
                ivBlueButton.isVisible = isCreatorOrPerformer() || project.canEditTime == true
            }
            updateResponse.simpleCollect(
                this@ProjectDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) {
                makeSuccessSnack(R.string.edited_toast.toString())
                deletedFiles.clear()
                newFiles.clear()
                dataHolder.clear()
                if (isEditorMode) {
                    switchMode()
                }
                viewModel.initProject()
                binding.progressLayout.progressLoader.isVisible = false
                clearUpdateResponse()
                sharedViewModel.setProjectNeedsRefresh(true)
            }

            goalList.simpleCollect(
                this@ProjectDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) { goalList ->
                this@ProjectDetailUpdateFragment.goalList.clear()
                this@ProjectDetailUpdateFragment.goalList.addAll(goalList)
                if (isEditorMode) {
                    binding.spGoals.apply {
                        setItems(goalList.map { it.title })
//                        setSelection(goalList.map { it.id }.indexOf(originalData?.goal?.id))
                    }
                }
                binding.progressLayout.progressLoader.isVisible = false
            }

            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.isProjectDeleted.collect { isDeleted ->
                    if (isDeleted) {
                        findNavController().navigateUp()
                        sharedViewModel.setProjectDeleted(false)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setClickListeners() {
        super.setClickListeners()

        with(binding) {
//            repeatLayout.setOnClickListener {
//                if (isEditorMode && isCreator()) {
//                    showRepeatNoteDialog(this@ProjectDetailUpdateFragment::setRepeat)
//                }
//            }
            cvAuthor.setOnClickListener {
                it.showPersonTooltip(originalData?.creatorObject?.fullName)
            }
            plansLayout.setDropDownClick(
                requireContext(),
                rvPlans,
                null
            )
            spGoals.onItemSelected { position ->
                goalList.getOrNull(position)?.let {
                    dataHolder.goal = GoalData(id = it.id, title = it.title)
                }
            }
            spGoals.setOnTouchListener { _, event ->
                if (isEditorMode && goalList.isEmpty() && event.action == MotionEvent.ACTION_DOWN) {
                    viewModel.initGoalList()
                } else {
                    spGoals.performClick()
                }
                false
            }
            binding.btnDeleteProject.setOnClickListener {
                findNavController().navigate(
                    ProjectDetailUpdateFragmentDirections.toDeleteDialogFragment(
                        DeleteDialogEnum.PROJECT,
                        projectId
                    )
                )
            }
            ivAddPlan.setOnClickListener {
                if (llCreateDiscussedTopic.isVisible) {
                    llCreateDiscussedTopic.isVisible = false
                    etDiscussedTopic.setText("")
                    ivAddPlan.rotation = 0f
                    hideKeyboard()
                } else {
                    llCreateDiscussedTopic.isVisible = true
                    ivAddPlan.rotation = 45f
                    etDiscussedTopic.requestFocus()
                }
            }
            ivCreateDiscussedTopic.setOnClickListener {
                if (etDiscussedTopic.text.isNullOrBlank()) {
                    etDiscussedTopic.error = "Reja matnini kiriting"
                } else {
                    if (dataHolder.discussedTopics == null) {
                        dataHolder.discussedTopics = mutableListOf()
                    }
                    dataHolder.discussedTopics?.add(
                        DiscussedTopic(
                            dataHolder.discussedTopics?.size
                                ?: 0 + newPlans.size + deletedPlans.size,
                            etDiscussedTopic.text.toString()
                        )
                    )
                    newPlans.add(etDiscussedTopic.text.toString())
                    discussedTopicAdapter.submitList(dataHolder.discussedTopics?.toMutableList())
                    tvPlansCount.text = dataHolder.discussedTopics?.size?.toString() ?: "0"
                    llCreateDiscussedTopic.isVisible = false
                    etDiscussedTopic.setText("")
                    ivAddPlan.rotation = 0f
                    hideKeyboard()
                }
            }
            clickOutside.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (llCreateDiscussedTopic.isVisible) {
                        val outRect = Rect()
                        val outExceptionRect = Rect()
                        llCreateDiscussedTopic.getGlobalVisibleRect(outRect)
                        ivAddPlan.getGlobalVisibleRect(outExceptionRect)
                        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) &&
                            !outExceptionRect.contains(event.rawX.toInt(), event.rawY.toInt())
                        ) {
                            etDiscussedTopic.setText("")
                            hideKeyboard()
                            llCreateDiscussedTopic.isVisible = false
                            ivAddPlan.rotation = 0f
                        }
                    }
                }
                false
            }

            ivBlueButton.setOnClickListener {
                if (!isEditorMode) {
                    switchMode()
                } else {
                    val validations = mutableListOf<Triple<Boolean, TextView, String>>()

                    etName?.let {
                        Triple(
                            it.text.isNullOrBlank(),
                            it,
                            "Proyekt nomini kiriting"
                        )
                    }

//                    etYaqm?.let {
//                        validations.add(
//                            Triple(it.text.isNullOrBlank(), it, "Yaqm ni kiriting"),
//                        )
//                    }

                    val isInputCompleted =
                        if (validations.isEmpty()) true else isInputCompleted(
                            validations,
                            nestedScroll
                        )

                    if (isInputCompleted) {
                        viewModel.updateProject(
                            projectId,
                            UpdateProjectRequest(
                                etName?.text.toString(),
//                                etDescription?.text.toString(),
//                                etYaqm?.text.toString(),
                                dataHolder.performer?.id ?: originalData!!.performer.id,
                                dataHolder.leader?.id ?: originalData!!.leader.id,
                                tvStartDate.text.toString() + " " + tvStartTime.text.toString(),
                                tvEndDate.text.toString() + " " + tvEndTime.text.toString(),
                                deletedFiles,
                                newFiles,
                                dataHolder.status?.id ?: originalData!!.status.id!!,
                                dataHolder.importance?.first ?: originalData!!.importance,
                                dataHolder.goal?.id ?: originalData!!.goal.id,
//                                if (dataHolder.participants != null) dataHolder.participants!!.map { it.id } else originalData!!.participants.map { it.id },
                                if (dataHolder.observers != null) dataHolder.observers!!.map { it.id } else originalData!!.spectators.map { it.id },
//                                if (dataHolder.functionalGroup != null) dataHolder.functionalGroup!!.map { it.id } else originalData!!.functionalGroup.map { it.id },
                                dataHolder.internalStatus,
                                dataHolder.comment,
                                dataHolder.canEditTime ?: originalData?.canEditTime ?: false,
                                deletedPlans,
                                newPlans,
                                dataHolder.repeatString ?: originalData?.repeat,
                                if ((dataHolder.repeatString ?: originalData?.repeat)
                                    == listRepetitionServer[2]
                                ) {
                                    if (dataHolder.repeatWeekRule != 0) dataHolder.repeatWeekRule else originalData?.repeatRule
                                } else {
                                    if (dataHolder.repeatMonthRule != 0) dataHolder.repeatMonthRule else originalData?.repeatRule
                                },                            )
                        )
                    }
                }
            }
        }
    }

    override fun switchMode() {
        super.switchMode()

        with(binding) {
            if (isEditorMode) {
                if (goalList.isEmpty()) {
                    viewModel.initGoalList()
                } else {
                    spGoals.apply {
                        setItems(goalList.map { it.title })
                        setSelection(goalList.map { it.id }.indexOf(originalData?.goal?.id))
                    }
                }
            }
            spGoals.isEnabled = isEditorMode && isCreator()

//            discussedTopicAdapter.changeMode(isEditorMode)
            newPlans.clear()
            deletedPlans.clear()
            ivAddPlan.isVisible = isEditorMode
        }
    }

    override fun onRemoteImageClick(item: FilesItem) {
        findNavController().navigate(
            ProjectDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onLocalImageClick(item: FilesItem) {
        findNavController().navigate(
            ProjectDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    private fun onDeleteDiscussedTopic(topic: DiscussedTopic) {
        if (dataHolder.discussedTopics?.contains(topic) == true) {
            deletedPlans.add(topic.id)
        } else {
            newPlans.removeAll { it == topic.title }
        }
        dataHolder.discussedTopics?.remove(topic)
        discussedTopicAdapter.submitList(dataHolder.discussedTopics?.toMutableList())
        binding.tvPlansCount.text = dataHolder.discussedTopics?.size?.toString() ?: "0"
    }

    private fun onDiscussedTopicMoreClick(topic: DiscussedTopic) {
        showConvertToDialog(
            {
                findNavController().navigate(
                    R.id.projectDetailUpdateFragment_to_createProjectFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title,
                        "PLAN_ID" to topic.id
                    )
                )
            },
            {
                findNavController().navigate(
                    R.id.projectDetailUpdateFragment_to_createTaskFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title,
                        "PLAN_ID" to topic.id
                    )
                )
            },
            isEditorMode,
            {
                onDeleteDiscussedTopic(topic)
            }
        )
    }

//    private fun setRepeat(repeat: String) {
//        binding.tvRepeatText.text = repeat
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}