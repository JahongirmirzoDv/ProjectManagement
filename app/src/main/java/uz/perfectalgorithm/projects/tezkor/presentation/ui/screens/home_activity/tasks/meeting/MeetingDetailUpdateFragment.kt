package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.meeting

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.MeetingStatusEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.UpdateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentMeetingDetailUpdateBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.ReminderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.DiscussedTopicAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.MeetingCommentAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.MeetingMemberAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects.UserDataAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.ModeratorDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.ReminderNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateBaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting.MeetingDetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting.MeetingDetailUpdateViewModelFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting.provideFactory
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/11/21 10:51 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.tasks.meeting
 **/
/**
 * Majlis detail-edit oynasi
 * TODO:
 * 1. Qatnashuvchilar orasidan moderator tayinlash ni qo'shish kerak
 * 2. Kommentlar qo'shish (task dan olish mumkin)
 * 3. Start-end buttonni figmaga moslash
 * 4. Korib chiqilgan masala (discussed_topic) ni proyekt/vazifaga convert qilish
 * logikasini o'zgartirish.
 * Hozir shunchaki create screenga o'tkazib yuborilyapti.
 * Figmadagi holatga keltirish kerak
 */
@AndroidEntryPoint
class MeetingDetailUpdateFragment : DetailUpdateBaseFragment<MeetingDetails>() {

    private var _binding: FragmentMeetingDetailUpdateBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val meetingId by lazy { MeetingDetailUpdateFragmentArgs.fromBundle(requireArguments()).meetingId }
    private val repeatList by lazy { mutableListOf<RepetitionData>() }
    private val deletedDiscussedTopics = mutableListOf<Int>()
    private val newDiscussedTopics = mutableListOf<String>()
    private val deletedReminders = mutableSetOf<Int>()
    private val newReminders = mutableSetOf<Int>()
    private val reminderAdapter by lazy { ReminderAdapter(::onDeleteReminder) }
    private val discussedTopicAdapter by lazy { DiscussedTopicAdapter(::onDiscussedTopicMoreClick) }
    private val meetingCommentAdapter by lazy { MeetingCommentAdapter(::onReplyComment) }
    override var hasMembers = true

    @Inject
    lateinit var viewModelFactory: MeetingDetailUpdateViewModelFactory
    private val viewModel by viewModels<MeetingDetailUpdateViewModel> {
        provideFactory(viewModelFactory, meetingId)
    }

    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeetingDetailUpdateBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    override fun setupViews() = with(binding) {
        this@MeetingDetailUpdateFragment.etDescription = etDescription
        this@MeetingDetailUpdateFragment.ivSelectTimeRule = ivPinDateTime
        this@MeetingDetailUpdateFragment.repeatLayout = repeatLayout
        this@MeetingDetailUpdateFragment.repeatTypeLayout = repeatTypeLayout
        this@MeetingDetailUpdateFragment.reminderDateLayout = reminderDateLayout
        this@MeetingDetailUpdateFragment.repeatText = tvRepeatText
        this@MeetingDetailUpdateFragment.repeatTypeText = repeatTypeText
        this@MeetingDetailUpdateFragment.reminderDateText = tvReminderDateText
        this@MeetingDetailUpdateFragment.ivBackButton = imgBackButton
        this@MeetingDetailUpdateFragment.etName = etMeetingTitle
        this@MeetingDetailUpdateFragment.tvFileCount = tvFilesCount
        this@MeetingDetailUpdateFragment.tvStartDate = tvStartDate
        this@MeetingDetailUpdateFragment.tvStartTime = tvStartTime
        this@MeetingDetailUpdateFragment.tvEndDate = tvEndDate
        this@MeetingDetailUpdateFragment.tvEndTime = tvEndTime
        this@MeetingDetailUpdateFragment.ivImportanceIcon = ivImportance
        this@MeetingDetailUpdateFragment.ivBlueButton = createMeetingBlueButton
        this@MeetingDetailUpdateFragment.rvParticipants = rvParticipants
        this@MeetingDetailUpdateFragment.ivAddParticipant = ivAddParticipant
        this@MeetingDetailUpdateFragment.addFileLayout = cvFiles
        this@MeetingDetailUpdateFragment.vgImportance = cvImportance
        this@MeetingDetailUpdateFragment.vgStartTime = startDateTimeLayout
        this@MeetingDetailUpdateFragment.vgEndTime = endDateTimeLayout
        this@MeetingDetailUpdateFragment.vgParticipants = participantsLayout
        this@MeetingDetailUpdateFragment.ivArrowParticipants = ivArrowParticipants
        this@MeetingDetailUpdateFragment.baseViewModel = viewModel
    }

    override fun initViews() = with(binding) {
        super.initViews()

        rvProblems.adapter = discussedTopicAdapter
        rvComments.adapter = meetingCommentAdapter
        rvReminders.adapter = reminderAdapter
        ivAddProblem.isVisible = isEditorMode
//        ivAddComment.isVisible = isEditorMode
        ivAddReminder.isVisible = isEditorMode

        if (isEditorMode) {
            when {
                dataHolder.repeat == null && originalData?.repeat == "once" ->
                    reminderDateLayout.isVisible = false
                dataHolder.repeat?.key == "once" -> reminderDateLayout.isVisible = false
                else -> reminderDateLayout.isVisible = true
            }
        } else {
            reminderDateLayout.isVisible = originalData?.repeat != "once"
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setClickListeners() = with(binding) {
        super.setClickListeners()
        btnStartEndMeeting.setOnClickListener {
            if (isCreator()) {
                when (originalData?.meetingStatus) {
                    MeetingStatusEnum.NOT_STARTED.text -> {
                        viewModel.startEndMeeting(MeetingStatusEnum.STARTED.text)
                    }
                    MeetingStatusEnum.STARTED.text -> {
                        viewModel.startEndMeeting(MeetingStatusEnum.ENDED.text)
                    }
                }
            }
        }
        llMembersCount.setDropDownClick(
            requireContext(),
            rvParticipants,
            this@MeetingDetailUpdateFragment.ivArrowParticipants
        )
        problemsLayout.setDropDownClick(
            requireContext(),
            rvProblems,
            null
        )
        commentLayout.setDropDownClick(
            requireContext(),
            rvComments,
            null
        )
        reminderLayout.setDropDownClick(
            requireContext(),
            rvReminders,
            null
        )
        btnDeleteMeeting.setOnClickListener {
            findNavController().navigate(
                MeetingDetailUpdateFragmentDirections.actionMeetingDetailsFragmentToDeleteDialogFragment(
                    DeleteDialogEnum.MEETING,
                    meetingId
                )
            )
        }
//        repeatLayout.setOnClickListener {
//            if (isEditorMode && repeatList.isEmpty() && isCreator()) {
//                viewModel.initRepeats()
//            } else if (isEditorMode && isCreator()) {
//                showRepeatDialog(repeatList, this@MeetingDetailUpdateFragment::setRepeat)
//            }
//        }
//        reminderLayout.setOnClickListener {
//            if (isEditorMode && isCreator()) {
//                ReminderNoteDialog(
//                    requireContext(),
//                    this@MeetingDetailUpdateFragment::setReminder,
//                ).show()
//            }
//        }
//        reminderDateLayout.setOnClickListener {
//            if (isEditorMode && isCreator()) {
//                showReminderDateDialog(
//                    if (tvReminderDateText.text.isNullOrBlank()) null else LocalDateTime.parse(
//                        tvReminderDateText.text.toString(),
//                        formatter
//                    ), this@MeetingDetailUpdateFragment::setReminderDate
//                )
//            }
//        }
        myStateLayout.setOnClickListener {
            if (originalData != null) {
                findNavController().navigate(
                    MeetingDetailUpdateFragmentDirections.toMyStateDialogFragment(
                        originalData!!.title,
                        originalData!!.startTime,
                        originalData!!.endTime,
                        originalData!!.leader,
                        MeetingMember(
                            originalData!!.memberState?.id,
                            originalData!!.memberState?.state,
                            originalData!!.memberState?.description,
                            null
                        )
                    )
                )
            }
        }
        ivAddProblem.setOnClickListener {
            if (llCreateDiscussedTopic.isVisible) {
                llCreateDiscussedTopic.isVisible = false
                etDiscussedTopic.setText("")
                ivAddProblem.rotation = 0f
                hideKeyboard()
            } else {
                llCreateDiscussedTopic.isVisible = true
                ivAddProblem.rotation = 45f
                etDiscussedTopic.requestFocus()
            }
        }
        ivAddComment.setOnClickListener {
            if (llCreateComment.isVisible) {
                etComment.setText("")
                llCreateComment.isVisible = false
                ivAddComment.rotation = 0f
                hideKeyboard()
            } else {
                llCreateComment.isVisible = true
                ivAddComment.rotation = 45f
                etComment.requestFocus()
            }
        }
        ivAddReminder.setOnClickListener {
            if (isEditorMode && isCreator()) {
                ReminderNoteDialog(
                    requireContext(),
                    this@MeetingDetailUpdateFragment::addReminder,
                ).show()
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
                            ?: 0 + newDiscussedTopics.size + deletedDiscussedTopics.size,
                        etDiscussedTopic.text.toString()
                    )
                )
                newDiscussedTopics.add(etDiscussedTopic.text.toString())
                discussedTopicAdapter.submitList(dataHolder.discussedTopics?.toMutableList())
                tvProblemsCount.text = dataHolder.discussedTopics?.size?.toString() ?: "0"
                llCreateDiscussedTopic.isVisible = false
                etDiscussedTopic.setText("")
                ivAddProblem.rotation = 0f
                hideKeyboard()
            }
        }
        ivCreateComment.setOnClickListener {
            if (etComment.text.isNullOrBlank()) {
                etComment.error = "Reja matnini kiriting"
            } else {
                viewModel.createMeetingComment(etComment.text?.toString() ?: "")
            }
        }
        viewModel.createComment.simpleCollect(
            this@MeetingDetailUpdateFragment,
            binding.progressLayout.progressLoader
        ) {
            viewModel.parentCommentId = null
            llCreateComment.isVisible = false
            etComment.setText("")
            ivAddComment.rotation = 0f
            hideKeyboard()
        }
        clickOutside.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (llCreateDiscussedTopic.isVisible) {
                    val outRect = Rect()
                    val outExceptionRect = Rect()
                    llCreateDiscussedTopic.getGlobalVisibleRect(outRect)
                    ivAddProblem.getGlobalVisibleRect(outExceptionRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) &&
                        !outExceptionRect.contains(event.rawX.toInt(), event.rawY.toInt())
                    ) {
                        etDiscussedTopic.setText("")
                        hideKeyboard()
                        llCreateDiscussedTopic.isVisible = false
                        ivAddProblem.rotation = 0f
                    }
                }
                if (llCreateComment.isVisible) {
                    val outRect = Rect()
                    val outExceptionRect = Rect()
                    llCreateComment.getGlobalVisibleRect(outRect)
                    ivAddComment.getGlobalVisibleRect(outExceptionRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) &&
                        !outExceptionRect.contains(event.rawX.toInt(), event.rawY.toInt())
                    ) {
                        viewModel.parentCommentId = null
                        llCreateComment.isVisible = false
                        etComment.setText("")
                        ivAddComment.rotation = 0f
                        hideKeyboard()
                    }
                }
            }
            false
        }
        cvAuthor.setOnClickListener {
            it.showPersonTooltip(originalData?.creatorObject?.fullName)
        }

        ivBlueButton.setOnClickListener {
            if (!isEditorMode) {
                switchMode()
            } else {

                val validations = mutableListOf(
                    Triple(
                        etAddress.text.isNullOrBlank(),
                        etAddress,
                        "Majlis o'kazilish joyini kiriting"
                    ),
                )

                etName?.let {
                    validations.add(
                        Triple(
                            it.text.isNullOrBlank(),
                            it,
                            "Majlis nomini kiriting"
                        )
                    )
                }

                val isInputCompleted = isInputCompleted(validations, nsvRoot)

                if (isInputCompleted) {
                    viewModel.updateMeeting(
                        UpdateMeetingRequest(
                            etName?.text.toString(),
                            etAddress.text.toString(),
                            tvStartDate.text.toString() + " " + tvStartTime.text.toString(),
                            tvEndDate.text.toString() + " " + tvEndTime.text.toString(),
                            deletedFiles,
                            newFiles,
                            dataHolder.importance?.first ?: originalData!!.importance,
                            newUsers.toList(),
                            deletedMembers.toList(),
//                            dataHolder.reminder?.first ?: originalData?.reminder,
                            dataHolder.reminderDate?.toUiDateTime() ?: originalData?.reminderDate,
                            null,
                            deletedDiscussedTopics,
                            newDiscussedTopics,
                            deletedReminders.toList(),
                            newReminders.toList(),
                            dataHolder.canEditTime ?: originalData?.canEditTime ?: false,
                            dataHolder.repeatString ?: originalData?.repeat,
                            if ((dataHolder.repeatString ?: originalData?.repeat)
                                == listRepetitionServer[2]
                            ) {
                                if (dataHolder.repeatWeekRule != 0) dataHolder.repeatWeekRule else originalData?.repeatRule
                            } else {
                                if (dataHolder.repeatMonthRule != 0) dataHolder.repeatMonthRule else originalData?.repeatRule
                            },
                            etDescription.text?.toString(),
                            getModeratorsByUser()
                        )
                    )
                }
            }
        }
        setMemberListener(object : MeetingMemberAdapter.Listener {
            override fun onItemLongClick(
                member: MeetingMember,
                userId: Int,
                isModerator: Boolean,
                position: Int
            ) {
                ModeratorDialog.showModeratorBottomSheetDialog(requireContext(), isModerator) {
                    if (isModerator) changeMemberModerator(userId, position, false)
                    else changeMemberModerator(member.user?.id ?: 0, position, true)
                    viewModel.updateModeratorMeeting(getMemberModeratorId())
                }
            }

            override fun onItemClick(userId: Int, position: Int, isChecked: Boolean) {
                changeStatus = 1
                viewModel.updateCheckedMeeting(userId, isChecked)
            }
        })

        setParticipantListener(object : UserDataAdapter.Listener {
            override fun itemLongClick(
                data: PersonData,
                userId: Int,
                isModerator: Boolean,
                position: Int
            ) {
                ModeratorDialog.showModeratorBottomSheetDialog(requireContext(), isModerator) {
                    if (isModerator) changeParticipantModerator(userId, position,false)
                    else changeParticipantModerator(data.id, position,true)
                }
            }

            override fun onItemClick(userId: Int, position: Int, isChecked: Boolean) {
                changeStatus = 0
                viewModel.updateCheckedMeeting(userId, isChecked)
            }

        })
    }

    override fun setObservers() {
        super.setObservers()
        with(viewModel) {
            meeting.simpleCollect(
                this@MeetingDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) { meeting ->
                originalData = meeting
                meetStatus = originalData?.meetingStatus?:""
                setDataToViews(meeting)
                binding.progressLayout.progressLoader.isVisible = false
                binding.btnDeleteMeeting.isVisible = isCreator()
                ivBlueButton.isVisible = isCreator() || originalData?.canEditTime == true
            }

            repeats.simpleCollect(
                this@MeetingDetailUpdateFragment,
                binding.pbLoadingRepetitions
            ) { repeats ->
                repeatList.clear()
                repeatList.addAll(repeats)
                binding.pbLoadingRepetitions.isVisible = false
            }

            updateResponse.simpleCollect(
                this@MeetingDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) {
                makeSuccessSnack("Muvaffaqiyatli o'zgartirildi")
                deletedFiles.clear()
                newFiles.clear()
                dataHolder.clear()
                if (isEditorMode) {
                    switchMode()
                }
                viewModel.initMeeting()
                binding.progressLayout.progressLoader.isVisible = false
                clearUpdateResponse()
                sharedViewModel.setMeetingNeedsRefresh(true)
            }

            updateResponseChecked.simpleCollect(this@MeetingDetailUpdateFragment, binding.progressLayout.progressLoader) {
                makeSuccessSnack("Muvaffaqiyatli o'zgartirildi")
                changeChecked(it.id, it.participated)
                binding.progressLayout.progressLoader.isVisible = false
            }

            startEndReponse.simpleCollect(
                this@MeetingDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) {
                deletedFiles.clear()
                newFiles.clear()
                dataHolder.clear()
                if (isEditorMode) {
                    switchMode()
                }
                viewModel.initMeeting()
                binding.progressLayout.progressLoader.isVisible = false
                clearUpdateResponse()
            }
            commentsList.observe(viewLifecycleOwner) {
                meetingCommentAdapter.submitList(it)
                binding.tvCommentsCount.text = it.size.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isMeetingDeleted.collect { isDeleted ->
                if (isDeleted) {
                    findNavController().navigateUp()
                    sharedViewModel.setMeetingDeleted(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isMemberStateNeedsRefresh.collect { isChanged ->
                if (isChanged) {
                    viewModel.initMeeting()
                    sharedViewModel.setMemberStateNeedsRefresh(false)
                }
            }
        }
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<List<PersonData>?>(PARTICIPANTS)
                ?.observe(viewLifecycleOwner, this@MeetingDetailUpdateFragment::setParticipants)

            this?.getLiveData<List<Int>>(DELETED_FILES)
                ?.observe(viewLifecycleOwner) {
                    deletedFiles.clear()
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
        }
    }

    override fun setDataToViews(data: MeetingDetails) = with(binding) {
        super.setDataToViews(data)
        etAddress.setText(data.address)
        viewModel.comments.addAll(data.commentaries)
        viewModel.commentsList.postValue(viewModel.comments)
        val reminders = data.reminders.map { NoteReminder(getReminderText(it), it) }
        reminderAdapter.submitList(reminders)
        if (dataHolder.reminders == null) {
            dataHolder.reminders = reminders.toMutableSet()
        }
//        reminderDateLayout.isVisible = data.repeat != "once"
//        tvReminderDateText.text = data.reminderDate
//        tvRepeatText.text = when (data.repeat) {
//            "once" -> "Bir marta"
//            "every_day" -> "Har kuni"
//            "every_week" -> "Har hafta"
//            "every_month" -> "Har oy"
//            "every_year" -> "Har yil"
//            else -> ""
//        }
        if (data.creatorObject?.image != null) {
            ivAuthor.loadImageUrl(data.creatorObject.image, R.drawable.ic_person)
        } else {
            ivAuthor.setImageResource(R.drawable.ic_person)
        }

        if (dataHolder.discussedTopics == null) {
            dataHolder.discussedTopics = data.discussedTopics?.toMutableList()
        }
        discussedTopicAdapter.submitList(dataHolder.discussedTopics?.toMutableList())
        tvProblemsCount.text = dataHolder.discussedTopics?.size?.toString() ?: "0"

        with(btnStartEndMeeting) {
            isVisible = isCreator() && data.meetingStatus != MeetingStatusEnum.ENDED.text
            when (data.meetingStatus) {
                MeetingStatusEnum.NOT_STARTED.text -> {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    text = "Majlisni boshlash"
                }
                MeetingStatusEnum.STARTED.text -> {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.delete_color_dialog))
                    text = "Majlisni tugatish"
                }
            }
        }

        svMembersCount.isVisible = !isEditorMode && isCreator()
        tvAcceptedMembers.text =
            data.members?.filter { it.state == "accepted" }?.size.toString()
        tvRejectedMembers.text =
            data.members?.filter { it.state == "rejected" }?.size.toString()
        tvWaitingMembers.text = data.members?.filter { it.state == "waiting" }?.size.toString()


        if (data.memberState?.state == "waiting" && data.creatorObject?.id != storage.userId) {
            findNavController().navigate(
                MeetingDetailUpdateFragmentDirections.toSelectMemberStateDialogFragment(
                    data.memberState.id
                        ?: data.members?.firstOrNull { it.user?.id == storage.userId }?.id ?: 0,
                    data.title,
                    data.description?:"",
                    data.startTime,
                    data.endTime,
                    data.creatorObject
                )
            )
        }

        myStateLayout.isVisible = data.memberState != null
        when (data.memberState?.state) {
            null -> Unit
            "waiting" -> {
                tvTextMyState.text = "Kutilmoqda"
                ivIconMyState.setImageResource(R.drawable.ic_member_waiting)
            }
            "accepted" -> {
                tvTextMyState.text = "Qatnashaman"
                ivIconMyState.setImageResource(R.drawable.ic_member_accepted)
            }
            "rejected" -> {
                tvTextMyState.text = "Qatnasha olmayman"
                ivIconMyState.setImageResource(R.drawable.ic_member_rejected)
            }
        }
    }

    override fun switchMode() = with(binding) {
        super.switchMode()

        if (isEditorMode) {
            if (repeatList.isEmpty()) {
                viewModel.initRepeats()
            }

            when {
                dataHolder.repeat == null && originalData?.repeat == "once" ->
                    reminderDateLayout.isVisible = false
                dataHolder.repeat?.key == "once" -> reminderDateLayout.isVisible = false
                else -> reminderDateLayout.isVisible = true
            }
        } else {
            reminderDateLayout.isVisible = originalData?.repeat != "once"
        }

        deletedReminders.clear()
        newReminders.clear()
        reminderAdapter.changeMode(isEditorMode)
        etAddress.isEnabled = isEditorMode && isCreator()
        svMembersCount.isVisible = !isEditorMode && isCreator()
        newDiscussedTopics.clear()
        deletedDiscussedTopics.clear()
        ivAddProblem.isVisible = isEditorMode
//        ivAddComment.isVisible = isEditorMode
        ivAddReminder.isVisible = isEditorMode
        btnDeleteMeeting.isVisible = !isEditorMode
        btnStartEndMeeting.isVisible =
            !isEditorMode && isCreator() && originalData?.meetingStatus != MeetingStatusEnum.ENDED.text
        val reminders = originalData?.reminders?.map { NoteReminder(getReminderText(it), it) }
        reminderAdapter.submitList(reminders)
        if (dataHolder.reminders == null) {
            dataHolder.reminders = reminders?.toMutableSet()
        }
    }

//    private fun setRepeat(repeat: RepetitionData) {
//        binding.tvRepeatText.text = repeat.title
//        dataHolder.repeat = repeat
//        binding.reminderDateLayout.isVisible = repeat.key != "once"
//    }

//    private fun setReminder(reminder: Pair<String, Int>) {
//        val newPair = Pair(reminder.second, reminder.first)
//        binding.tvReminderText.text = newPair.second
//        dataHolder.reminder = newPair
//    }

//    private fun setReminderDate(dateTime: LocalDateTime) {
//        dateTime.isReminderDateValid(dataHolder, originalData).let { resultPair ->
//            if (resultPair.first) {
//                binding.tvReminderDateText.text = dateTime.toUiDateTime()
//            } else {
//                makeErrorSnack(resultPair.second)
//            }
//        }
//    }

    override fun onDescriptionClick(member: MeetingMember) {
        super.onDescriptionClick(member)
        findNavController().navigate(
            MeetingDetailUpdateFragmentDirections.toMemberDescriptionDialogFragment(
                member
            )
        )
    }

    override fun onRemoteImageClick(item: FilesItem) {
        findNavController().navigate(
            MeetingDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onLocalImageClick(item: FilesItem) {
        findNavController().navigate(
            MeetingDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    private fun addReminder(pair: Pair<String, Int>) {
        val reminder = NoteReminder(pair.first, pair.second)
        if (dataHolder.reminders == null) {
            dataHolder.reminders = mutableSetOf()
        }
        if (!dataHolder.reminders?.map { it.minutesTime }?.contains(pair.second)!!) {
            newReminders.add(reminder.minutesTime)
        }
        dataHolder.reminders?.add(reminder)
        reminderAdapter.submitList(dataHolder.reminders?.toMutableList())
    }

    private fun onDeleteReminder(reminder: NoteReminder) {
        if (dataHolder.reminders == null) {
            dataHolder.reminders = mutableSetOf()
        }
        dataHolder.reminders?.remove(reminder)
        reminderAdapter.submitList(dataHolder.reminders?.toMutableList())
        if (originalData?.reminders?.contains(reminder.minutesTime) == true) {
            deletedReminders.add(reminder.minutesTime)
        } else {
            newReminders.remove(reminder.minutesTime)
        }
    }

    private fun onDeleteDiscussedTopic(topic: DiscussedTopic) {
        if (dataHolder.discussedTopics?.contains(topic) == true) {
            deletedDiscussedTopics.add(topic.id)
        } else {
            newDiscussedTopics.removeAll { it == topic.title }
        }
        dataHolder.discussedTopics?.remove(topic)
        discussedTopicAdapter.submitList(dataHolder.discussedTopics?.toMutableList())
        binding.tvProblemsCount.text = dataHolder.discussedTopics?.size?.toString() ?: "0"
    }

    private fun onReplyComment(comment: MeetingComment) {
        viewModel.parentCommentId = comment.id
        with(binding) {
            if (llCreateComment.isVisible) {
                etComment.setText("")
                llCreateComment.isVisible = false
                ivAddComment.rotation = 0f
                hideKeyboard()
            } else {
                llCreateComment.isVisible = true
                ivAddComment.rotation = 45f
                etComment.requestFocus()
            }
        }
    }

    private fun onDiscussedTopicMoreClick(topic: DiscussedTopic) {
        showConvertToDialog(
            {
                findNavController().navigate(
                    R.id.meetingDetailUpdateFragment_to_createProjectFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title,
                        "ID" to topic.id
                    )
                )
            },
            {
                findNavController().navigate(
                    R.id.meetingDetailUpdateFragment_to_createTaskFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title,
                        "ID" to topic.id
                    )
                )
            },
            isEditorMode,
            {
                onDeleteDiscussedTopic(topic)
            }
        )
    }

    private fun getReminderText(minutes: Int) = when {
        minutes == 0 -> "O'z vaqtida"
        minutes in 1..59 -> "$minutes daqiqa oldin"
        minutes >= 60 && minutes < 60 * 24 -> "${minutes / 60} soat oldin"
        minutes >= 60 * 24 && minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)} kun oldin"
        minutes == 60 * 24 * 7 -> "1 hafta oldin"
        else -> "O'z vaqtida"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}