package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.toFilesItemList
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.toListForAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapter.Companion.VIEW_TYPE_IMAGE_NEW
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapter.Companion.VIEW_TYPE_OTHER_NEW
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapterListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.MeetingMemberAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects.UserDataAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.ModeratorDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RadioGroupDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RepeatRuleWeeklyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.projects.CauseRejectDialogFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.dating.DatingDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.meeting.MeetingDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.project.ProjectDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.download.isImageFile
import uz.perfectalgorithm.projects.tezkor.utils.download.openFile
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.tasks.getDate
import uz.perfectalgorithm.projects.tezkor.utils.tasks.getTime
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import java.net.URI
import javax.inject.Inject
import kotlin.math.pow


/**
 *Created by farrukh_kh on 8/15/21 9:26 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.others
 **/
/**
 * Meeting, Dating, Project, Task, Goal lar uchun detail va edit bitta screen da qilingan
 * Shu screenlarning umumiy logikasini o'zida saqlovchi base fragment
 * TODO:
 * 1. Dasturga ish jarayonida ancha o'zgarishlar kiritilganligi sababli
 * bu qismdagi bazi kod qismlari o'z manosini yo'qotgan.
 * Qayta yozish yoki optimizatsiya qilish tavsiya etiladi
 */
//TODO this base class needs optimization and refactor (maybe better to rewrite it)
abstract class DetailUpdateBaseFragment<T : BaseDetails> : Fragment(), EditFileAdapterListener {

    // TODO: 9/2/21 for v2.0: base frag needs big optimization and refactor
    lateinit var ivBackButton: ImageView
    lateinit var tvFileCount: TextView
    lateinit var tvStartDate: TextView
    lateinit var tvStartTime: TextView
    lateinit var tvEndDate: TextView
    lateinit var tvEndTime: TextView
    lateinit var ivBlueButton: ImageView
    lateinit var vgStartTime: ViewGroup
    lateinit var vgEndTime: ViewGroup


    var repeatLayout: View? = null
    var repeatTypeLayout: View? = null
    var reminderDateLayout: View? = null

    var repeatText: TextView? = null
    var repeatTypeText: TextView? = null
    var reminderDateText: TextView? = null

    var ivParticipantsInCv: ImageView? = null
    var tvParticipantsCount: TextView? = null
    var ivObserversInCv: ImageView? = null
    var tvObserversCount: TextView? = null
    var rvParticipants: RecyclerView? = null
    var ivArrowParticipants: ImageView? = null
    var vgParticipants: ViewGroup? = null
    var addObservers: View? = null
    var addParticipants: View? = null
    var ivAddParticipant: ImageView? = null
    var addFileLayout: View? = null
    var addFile: View? = null
    var rvFiles: RecyclerView? = null
    var etDescription: EditText? = null
    var etName: TextInputEditText? = null
    var etYaqm: EditText? = null
    var vgStatus: ViewGroup? = null
    var vgPerformer: ViewGroup? = null
    var vgLeader: ViewGroup? = null
    var ivFunctionalGroup: ImageView? = null
    var ivArrowObservers: ImageView? = null
    var vgFunctionalGroup: ViewGroup? = null
    var vgObservers: ViewGroup? = null
    var ivAddObserver: ImageView? = null
    var ivAddFunctionalGroup: ImageView? = null
    var rvObservers: RecyclerView? = null
    var rvFunctionalGroup: RecyclerView? = null
    var ivStatusIcon: ImageView? = null
    var tvStatusTitle: TextView? = null
    var tvInternalStatusTitle: TextView? = null
    var tvCommentTitle: TextView? = null
    var tvCommentReject: TextView? = null
    var tvCommentCount: TextView? = null
    var commentLayout: ViewGroup? = null
    var ivPerformerAvatar: ImageView? = null
    var ivLeaderAvatar: ImageView? = null
    var tvPerformerName: TextView? = null
    var tvLeaderName: TextView? = null
    var ivImportanceIcon: ImageView? = null
    var tvImportanceTitle: TextView? = null
    var vgImportance: ViewGroup? = null
    var ivSelectTimeRule: ImageView? = null

    val listRepetitionServer by lazy { resources.getStringArray(R.array.list_repeat_server) }
    var isEditorMode = false
    open var hasMembers = false
    var originalData: T? = null
    var meetStatus = ""
    var changeStatus = -1
    val dataHolder by lazy { AddingDataHolder() }
    val newFiles by lazy { mutableListOf<File>() }
    val deletedFiles by lazy { mutableListOf<Int>() }
    private var downloadID: Long = 0
    val statusList by lazy { mutableListOf<StatusData>() }
    private val filesAdapter by lazy { EditFileAdapter(this) }
    val formatter by lazy { DateTimeFormat.forPattern("yyyy-MM-dd HH:mm") }

    val newUsers by lazy { mutableSetOf<Int>() }
    val deletedMembers by lazy { mutableSetOf<Int>() }

    private val membersAdapter by lazy { MeetingMemberAdapter(this::onDescriptionClick) }
    private val participantsAdapter by lazy { UserDataAdapter(this::onDeleteParticipantClick) }
    private val observersAdapter by lazy { UserDataAdapter(this::onDeleteObserverClick) }
    private val functionalGroupAdapter by lazy { UserDataAdapter(this::onDeleteFunctionalGroupClick) }

    @Inject
    lateinit var storage: LocalStorage

    lateinit var baseViewModel: DetailUpdateViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAppBar()
        hideBottomMenu()

        initViews()
        setClickListeners()
    }

    fun setMemberListener(listener: MeetingMemberAdapter.Listener) {
        membersAdapter.listener = listener
    }

    fun setParticipantListener(listener: UserDataAdapter.Listener) {
        participantsAdapter.listener = listener
    }

    fun getMemberModeratorId(): List<Int> {
        return membersAdapter.moderatorId
    }

    fun getModeratorsByUser(): List<Int> {
        return participantsAdapter.moderatorId
    }

    fun changeMemberModerator(id:Int,postion:Int,isAdded:Boolean){
        membersAdapter.changeModerator(id,postion,isAdded)
    }

    fun changeParticipantModerator(id:Int,postion:Int,isAdded:Boolean){
        participantsAdapter.changeModerator(id,postion,isAdded)
    }

    fun changeChecked(id: Int, isCheck: Boolean) {
        if (changeStatus == 1) {
            membersAdapter.changeCheck(isCheck, id)
        } else if (changeStatus == 0) {
            participantsAdapter.changeCheck(isCheck, id)
        }
        changeStatus = -1
    }

    abstract fun setupViews()

    open fun initViews() {
        rvFiles?.adapter = filesAdapter

        if (dataHolder.repeatString ?: originalData?.repeat != null &&
            dataHolder.repeatString ?: originalData?.repeat != getString(R.string.once) &&
            dataHolder.repeatString ?: originalData?.repeat != getString(R.string.every_day)
        ) {
            repeatTypeLayout?.visible()
        } else {
            repeatTypeLayout?.hide()
        }
        if (dataHolder.repeatString ?: originalData?.repeat != null &&
            dataHolder.repeatString ?: originalData?.repeat != getString(R.string.once)
        ) {
            reminderDateLayout?.visible()
        } else {
            reminderDateLayout?.hide()
        }

        if (hasMembers && !isEditorMode && isCreator()) {
            rvParticipants?.adapter = membersAdapter
        } else {
            rvParticipants?.adapter = participantsAdapter
        }
        rvFunctionalGroup?.adapter = functionalGroupAdapter
        rvObservers?.adapter = observersAdapter
        if (!isEditorMode) {
            originalData?.let { setDataToViews(it) }
            ivBlueButton.setImageResource(R.drawable.ic_circle_edit)
            //temp
            filesAdapter.submitList(originalData?.files?.toListForAdapter())
        } else {
            ivBlueButton.setImageResource(R.drawable.ic_checedk_in_create_project)
            dataHolder.apply {
                leader?.let { setLeader(it) }
                performer?.let { setPerformer(it) }
                startDate?.let { setStartDate(it) }
                startTime?.let { setStartTime(it) }
                endDate?.let { setEndDate(it) }
                endTime?.let { setEndTime(it) }
                repeatString?.let { setRepeat(it) }
                files?.let { tvFileCount.text = it.size.toString() }
                status?.let { setStatus(it) }
                importance?.let { setImportance(it) }
                participants?.let { setParticipants(it) }
                observers?.let { setObserverPersons(it) }
                functionalGroup?.let { setFunctionalGroup(it) }
            }
        }
        listOf(
            etName,
            etDescription,
            etYaqm
        ).forEach { view ->
            view?.isEnabled = isEditorMode
        }

        listOf(addFile, ivAddParticipant, ivAddObserver, ivAddFunctionalGroup).forEach { view ->
            view?.isVisible = isEditorMode
        }
    }

    open fun setDataToViews(data: T) {
        etName?.setText(data.title)
        etDescription?.setText(data.description)
        etYaqm?.setText(data.yaqm)

        data.performer.let {
            if (data is ProjectDetails || data is MeetingDetails) {
                when {
                    it != null && !it.image.isNullOrBlank() -> ivPerformerAvatar?.loadImageUrl(
                        it.image,
                        R.drawable.ic_person
                    )
                    it != null -> ivPerformerAvatar?.setImageResource(R.drawable.ic_person)
                    else -> ivPerformerAvatar?.setImageResource(R.drawable.ic_adding_person)
                }
            } else {
                if (it?.image != null) {
                    ivPerformerAvatar?.loadImageUrl(it.image)
                } else {
                    ivPerformerAvatar?.setImageResource(R.drawable.ic_user)
                }
            }
        }
        data.leader.let {
            if (data is ProjectDetails || data is MeetingDetails) {
                when {
                    it != null && !it.image.isNullOrBlank() -> ivLeaderAvatar?.loadImageUrl(
                        it.image,
                        R.drawable.ic_person
                    )
                    it != null -> ivLeaderAvatar?.setImageResource(R.drawable.ic_person)
                    else -> ivLeaderAvatar?.setImageResource(R.drawable.ic_adding_person)
                }
            } else {
                if (it?.image != null) {
                    ivLeaderAvatar?.loadImageUrl(it.image)
                } else {
                    ivLeaderAvatar?.setImageResource(R.drawable.ic_user)
                }
            }
        }
        tvLeaderName?.text = data.leader?.fullName
        tvPerformerName?.text = data.performer?.fullName

        try {
            val startTime = LocalDateTime.parse(data.startTime, formatter)
            val endTime = LocalDateTime.parse(data.endTime, formatter)
            tvStartDate.text = startTime.getDate()
            tvStartTime.text = startTime.getTime()
            tvEndDate.text = endTime.getDate()
            tvEndTime.text = endTime.getTime()
        } catch (e: Exception) {
        }

        tvFileCount.text = (data.files?.size ?: 0).toString()

        data.importance?.let { Pair(ivImportanceIcon, tvImportanceTitle).setImportance(it, true) }
        data.status?.let { Pair(ivStatusIcon, tvStatusTitle).setStatus(it) }

        tvCommentReject?.hide()
        tvCommentTitle?.hide()
        if (data is TaskDetails) {

            data.internalStatus?.let {
                setInternalStatus(it)
            }
            if (!data.comments.isNullOrEmpty()) {
                commentLayout?.visible()
                tvCommentCount?.text = data.comments.size.toString()
            } else {
                commentLayout?.hide()
            }
            data.commentaries?.let {
                dataHolder.commentList = it
            }
        }

        if (data is ProjectDetails) {
            data.internalStatus?.let {
                setInternalStatus(it)
            }
            data.comments?.let {
                dataHolder.commentList = it
            }

            if (!data.comments.isNullOrEmpty()) {
                commentLayout?.visible()
                tvCommentCount?.text = data.comments.size.toString()
            } else {
                commentLayout?.hide()
            }
        }

        if (hasMembers) {
            membersAdapter.moderatorId = data.members?.filter { it.moderator }?.map {  it.user?.id} as ArrayList<Int>
            participantsAdapter.moderatorId = data.members?.filter { it.moderator }?.map {  it.user?.id} as ArrayList<Int>
            if (meetStatus == "started") {
                membersAdapter.isCanChange = true
            }
            participantsAdapter.isCheckedMode = isModerator()
            membersAdapter.submitList(data.members)
            participantsAdapter.submitList(data.members?.map { it.user })
        } else {
//            participantsAdapter.submitList(data.participants)
            setParticipants(data.participants)
        }

        setObserverPersons(data.spectators)
        setFunctionalGroup(data.functionalGroup)
//        observersAdapter.submitList(data.spectators)
//        functionalGroupAdapter.submitList(data.functionalGroup)

        val originalFiles = data.files?.toListForAdapter()
        dataHolder.filesForAdapter = originalFiles?.toMutableList()
        filesAdapter.submitList(originalFiles)

        setRepeat(
            when (data.repeat) {
                listRepetitionServer[1] -> getString(R.string.every_day)
                listRepetitionServer[2] -> getString(R.string.every_week)
                listRepetitionServer[3] -> getString(R.string.every_month)
                listRepetitionServer[4] -> getString(R.string.every_year)
                else -> getString(R.string.once)
            }, false
        )

        data.reminderDate?.parseLocalDateTime()?.let { setReminderDate(it) }
    }

    private fun setInternalStatus(internalStatus: String) {
        dataHolder.internalStatus = internalStatus
        if (originalData is ProjectDetails) {
            when (internalStatus) {
                getString(R.string.new__) -> {
                    tvInternalStatusTitle?.text = getString(R.string.new_uz)
                    ivStatusIcon?.setImageResource(R.drawable.ic_new_status)
                }
                getString(R.string.begin_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.begin)
                    ivStatusIcon?.setImageResource(R.drawable.ic_began_with_bg)
                }
                getString(R.string.receive_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.receive)
                    ivStatusIcon?.setImageResource(R.drawable.ic_checked_with_bg)
                }
                getString(R.string.owner_check_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.owner_check)
                    ivStatusIcon?.setImageResource(R.drawable.ic_checked_by_owner_with_bg)
                }
                getString(R.string.done) -> {
                    tvInternalStatusTitle?.text = getString(R.string.done)
                    ivStatusIcon?.setImageResource(R.drawable.ic_done_with_bg)
                }
                getString(R.string.reject_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.reject)
                    ivStatusIcon?.setImageResource(R.drawable.ic_rejected_with_bg)
                }
                getString(R.string.leader_check_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.leader_check)
                    ivStatusIcon?.setImageResource(R.drawable.ic_checked_by_leader_with_bg)
                }
                getString(R.string.reject_receive_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.reject_receive)
                    ivStatusIcon?.setImageResource(R.drawable.ic_reject_confirmed_with_bg)
                }
            }
        } else {
            when (internalStatus) {
                getString(R.string.new__) -> {
                    tvInternalStatusTitle?.text = getString(R.string.new_uz)
                    ivStatusIcon?.setImageResource(R.drawable.ic_new)
                }
                getString(R.string.begin_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.begin)
                    ivStatusIcon?.setImageResource(R.drawable.ic_begin)
                }
                getString(R.string.receive_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.receive)
                    ivStatusIcon?.setImageResource(R.drawable.ic_check)
                }
                getString(R.string.owner_check_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.owner_check)
                    ivStatusIcon?.setImageResource(R.drawable.ic_owner_check)
                }
                getString(R.string.done) -> {
                    tvInternalStatusTitle?.text = getString(R.string.done)
                    ivStatusIcon?.setImageResource(R.drawable.ic_done)
                }
                getString(R.string.reject_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.reject)
                    ivStatusIcon?.setImageResource(R.drawable.ic_reject)
                }
                getString(R.string.leader_check_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.leader_check)
                    ivStatusIcon?.setImageResource(R.drawable.ic_leader_check)
                }
                getString(R.string.reject_receive_) -> {
                    tvInternalStatusTitle?.text = getString(R.string.reject_receive)
                    ivStatusIcon?.setImageResource(R.drawable.ic_reject_recevie)
                }
            }
        }
    }

    open fun setClickListeners() {
        ivBackButton.setOnClickListener {
            if (isEditorMode) {
                switchMode()
            } else {
                findNavController().navigateUp()
            }
        }
        vgParticipants?.setDropDownClick(
            requireContext(),
            rvParticipants,
            ivArrowParticipants
        )
        vgObservers?.setDropDownClick(
            requireContext(),
            rvObservers,
            ivArrowObservers
        )
        vgFunctionalGroup?.setDropDownClick(
            requireContext(),
            rvFunctionalGroup,
            ivFunctionalGroup
        )
        vgLeader?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = MASTER
                if (dataHolder.leader == null) {
                    if (originalData is DatingDetails) {
                        if ((originalData as DatingDetails).partnerIn == null) {
                            storage.persons = emptySet()
                        } else {
                            storage.persons =
                                setOf((originalData as DatingDetails).partnerIn?.map { it.id }.toString())
                        }
                    } else {
                        if (originalData?.leader == null) {
                            storage.persons = emptySet()
                        } else {
                            storage.persons = setOf(originalData!!.leader?.id.toString())
                        }
                    }
                } else {
                    storage.persons = setOf(dataHolder.leader!!.id.toString())
                }
                showSelectPersonFragment("Rahbarni tanlang")
            } else {
                if (tvLeaderName == null) {
                    it.showPersonTooltip(
                        dataHolder.leader?.fullName ?: originalData?.leader?.fullName
                    )
                }
            }
        }
        vgPerformer?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = PERFORMER
                if (dataHolder.performer == null) {
                    if (originalData?.performer == null) {
                        storage.persons = emptySet()
                    } else {
                        storage.persons = setOf(originalData!!.performer?.id.toString())
                    }
                } else {
                    storage.persons = setOf(dataHolder.performer!!.id.toString())
                }
                showSelectPersonFragment("Ijrochini tanlang")
            } else {
                if (tvPerformerName == null) {
                    it.showPersonTooltip(
                        dataHolder.performer?.fullName ?: originalData?.performer?.fullName
                    )
                }
            }
        }
        vgStartTime.setOnClickListener {
            if (isEditorMode && (isCreator() || originalData?.canEditTime == true)) {
                showDateTimePickerDialog(
                    this::setStartTime,
                    this::setStartDate
                )
            }
        }
        vgEndTime.setOnClickListener {
            if (isEditorMode && (isCreator() || originalData?.canEditTime == true)) {
                showDateTimePickerDialog(
                    this::setEndTime,
                    this::setEndDate
                )
            }
        }
        tvStartDate.setOnClickListener {
            if (isEditorMode && (isCreator() || originalData?.canEditTime == true)) {
                showDatePickerDialog(onDateSelected = this::setStartDate)
            }
        }
        tvEndDate.setOnClickListener {
            if (isEditorMode && (isCreator() || originalData?.canEditTime == true)) {
                showDatePickerDialog(onDateSelected = this::setEndDate)
            }
        }
        tvStartTime.setOnClickListener {
            if (isEditorMode && (isCreator() || originalData?.canEditTime == true)) {
                showTimePickerDialog(this::setStartTime)
            }
        }
        tvEndTime.setOnClickListener {
            if (isEditorMode && (isCreator() || originalData?.canEditTime == true)) {
                showTimePickerDialog(this::setEndTime)
            }
        }
        addFile?.setOnClickListener {
            if (isEditorMode && (isCreator())) {
                hideKeyboard()
                getFileFromDevice(fileSelectResultLauncher)
            }
        }
        addFileLayout?.setOnClickListener {
            if (isEditorMode && (isCreator())) {
//                hideKeyboard()
//                getFileFromDevice(fileSelectResultLauncher)
                when (originalData) {
                    is ProjectDetails -> findNavController().navigate(
                        ProjectDetailUpdateFragmentDirections.toSelectedFilesFragment(
                            ((originalData?.files?.toListForAdapter()
                                ?: emptyList()) + newFiles.toFilesItemList()).toTypedArray(), true
                        )
                    )
                    is MeetingDetails -> findNavController().navigate(
                        MeetingDetailUpdateFragmentDirections.toSelectedFilesFragment(
                            ((originalData?.files?.toListForAdapter()
                                ?: emptyList()) + newFiles.toFilesItemList()).toTypedArray(), true
                        )
                    )
                    is DatingDetails -> findNavController().navigate(
                        DatingDetailUpdateFragmentDirections.toSelectedFilesFragment(
                            ((originalData?.files?.toListForAdapter()
                                ?: emptyList()) + newFiles.toFilesItemList()).toTypedArray(), true
                        )
                    )
                    else -> {
                        hideKeyboard()
                        getFileFromDevice(fileSelectResultLauncher)
                    }
                }
            } else {
                if (originalData?.files.isNullOrEmpty()) {
                    it.showPersonTooltip("Fayllar qo'shilmagan")
                } else {
                    when (originalData) {
                        is ProjectDetails -> findNavController().navigate(
                            ProjectDetailUpdateFragmentDirections.toSelectedFilesFragment(
                                originalData?.files?.toListForAdapter()?.toTypedArray()
                                    ?: emptyArray(), false
                            )
                        )
                        is MeetingDetails -> findNavController().navigate(
                            MeetingDetailUpdateFragmentDirections.toSelectedFilesFragment(
                                originalData?.files?.toListForAdapter()?.toTypedArray()
                                    ?: emptyArray(), false
                            )
                        )
                        is DatingDetails -> findNavController().navigate(
                            DatingDetailUpdateFragmentDirections.toSelectedFilesFragment(
                                originalData?.files?.toListForAdapter()?.toTypedArray()
                                    ?: emptyArray(), false
                            )
                        )
                    }
                }
            }
        }
        commentLayout?.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(COMMENT_LIST, dataHolder.commentList)
            findNavController().navigate(R.id.to_causeCommentFragment, bundle)
        }
//        vgStatus?.setOnClickListener {
//            if (isEditorMode && (isCreatorOrPerformer())) {
//                if (statusList.isEmpty()) {
//                    baseViewModel.initStatusList()
//                } else {
//                    showStatusDialog(statusList, this::setStatus)
//                }
//            }
//        }

        vgStatus?.setOnClickListener {
            if (isEditorMode && (isCreatorOrPerformer())) {
//                if (statusList.isEmpty()) {
//                    baseViewModel.initStatusList()
//                } else {
                showStatusTaskProjectDialog(
                    dataHolder.internalStatus,
                    this::setStatusProjectTask
                )
//                }
            }
//            else {
//                if (originalData is ProjectDetails || originalData is TaskDetails) {
//                    val bundle = Bundle()
//                    bundle.putParcelableArrayList(COMMENT_LIST, dataHolder.commentList)
//                    findNavController().navigate(R.id.to_causeCommentFragment, bundle)
//                }
//            }
        }

        vgImportance?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                showImportanceDialog(this::setImportance)
            }
        }
        ivAddParticipant?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = PARTICIPANTS
                if (dataHolder.participants == null) {
                    if (hasMembers) {
                        if (originalData?.members == null) {
                            storage.persons = emptySet()
                        } else {
                            storage.persons =
                                originalData!!.members!!.map { it.user?.id.toString() }.toSet()
                        }
                    } else {
                        if (originalData?.participants == null) {
                            storage.persons = emptySet()
                        } else {
                            storage.persons =
                                originalData!!.participants!!.map { it.id.toString() }.toSet()
                        }
                    }
                } else {
                    storage.persons = dataHolder.participants!!.map { it.id.toString() }.toSet()
                }
                showSelectPersonFragment(R.string.select_participants.toString())
            }
        }
        addParticipants?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = PARTICIPANTS
                if (dataHolder.participants == null) {
                    if (hasMembers) {
                        if (originalData?.members == null) {
                            storage.persons = emptySet()
                        } else {
                            storage.persons =
                                originalData!!.members!!.map { it.user?.id.toString() }.toSet()
                        }
                    } else {
                        if (originalData?.participants == null) {
                            storage.persons = emptySet()
                        } else {
                            storage.persons =
                                originalData!!.participants!!.map { it.id.toString() }.toSet()
                        }
                    }
                } else {
                    storage.persons = dataHolder.participants!!.map { it.id.toString() }.toSet()
                }
                showSelectPersonFragment(R.string.select_participants.toString())
            } else {
                when (originalData) {
                    is ProjectDetails -> {
                        if (originalData?.participants.isNullOrEmpty()) {
                            it.showPersonTooltip("Ishtirokchilar mavjud emas")
                        } else {
                            findNavController().navigate(
                                ProjectDetailUpdateFragmentDirections.toSelectedPersonsFragment(
                                    originalData?.participants?.toTypedArray() ?: emptyArray()
                                )
                            )
                        }
                    }
                    is DatingDetails -> {
                        if (originalData?.participants.isNullOrEmpty()) {
                            it.showPersonTooltip("Ishtirokchilar mavjud emas")
                        } else {
                            findNavController().navigate(
                                DatingDetailUpdateFragmentDirections.toSelectedPersonsFragment(
                                    originalData?.participants?.toTypedArray() ?: emptyArray()
                                )
                            )
                        }
                    }
                }
            }
        }
        ivAddObserver?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = OBSERVERS
                if (dataHolder.observers == null) {
                    if (originalData?.spectators == null) {
                        storage.persons = emptySet()
                    } else {
                        storage.persons =
                            originalData!!.spectators?.map { it.id.toString() }?.toSet()
                                ?: emptySet()
                    }
                } else {
                    storage.persons = dataHolder.observers!!.map { it.id.toString() }.toSet()
                }
                showSelectPersonFragment("Kuzatuvchilarni tanlang")
            }
        }
        addObservers?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = OBSERVERS
                if (dataHolder.observers == null) {
                    if (originalData?.spectators == null) {
                        storage.persons = emptySet()
                    } else {
                        storage.persons =
                            originalData!!.spectators?.map { it.id.toString() }?.toSet()
                                ?: emptySet()
                    }
                } else {
                    storage.persons = dataHolder.observers!!.map { it.id.toString() }.toSet()
                }
                showSelectPersonFragment("Kuzatuvchilarni tanlang")
            } else {
                when (originalData) {
                    is ProjectDetails -> {
                        if (originalData?.spectators.isNullOrEmpty()) {
                            it.showPersonTooltip("Kuzatuvchilar mavjud emas")
                        } else {
                            findNavController().navigate(
                                ProjectDetailUpdateFragmentDirections.toSelectedPersonsFragment(
                                    originalData?.spectators?.toTypedArray() ?: emptyArray()
                                )
                            )
                        }
                    }
                }
            }
        }
        ivAddFunctionalGroup?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = FUNCTIONAL_GROUP
                if (dataHolder.functionalGroup == null) {
                    if (originalData?.functionalGroup == null) {
                        storage.persons = emptySet()
                    } else {
                        storage.persons =
                            originalData!!.functionalGroup?.map { it.id.toString() }?.toSet()
                                ?: emptySet()
                    }
                } else {
                    storage.persons =
                        dataHolder.functionalGroup!!.map { it.id.toString() }.toSet()
                }
                showSelectPersonFragment("Funksional guruhni tanlang")
            }
        }
        ivSelectTimeRule?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                showEditRuleDialog(
                    dataHolder.canEditTime ?: originalData?.canEditTime ?: false
                ) {
                    dataHolder.canEditTime = it
                }
            }
        }
        repeatLayout?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                showRepeatNoteDialog(this::setRepeat)
            }
        }
        repeatTypeLayout?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                val repeat = repeatText?.text.toString()
                showRepeatRule(repeat)
            }
        }
        reminderDateLayout?.setOnClickListener {
            if (isEditorMode && isCreator()) {
                showReminderDateDialog(
                    if (reminderDateText?.text.isNullOrBlank()) null else LocalDateTime.parse(
                        reminderDateText?.text.toString(),
                        formatter
                    ), this::setReminderDate
                )
            }
        }
    }

    open fun switchMode() {
        if (isCreatorOrPerformer()) {
            isEditorMode = !isEditorMode
        }

        if (hasMembers && !isEditorMode && isCreator()) {
            rvParticipants?.adapter = membersAdapter
        } else {
            rvParticipants?.adapter = participantsAdapter
        }

        participantsAdapter.isEditorMode =
            isEditorMode && (isCreator())
        observersAdapter.isEditorMode =
            isEditorMode && (isCreator())
        functionalGroupAdapter.isEditorMode =
            isEditorMode && (isCreator())
        filesAdapter.isEditorMode =
            isEditorMode && (isCreator())
        filesAdapter.notifyDataSetChanged()
        tvFileCount.text = (originalData?.files?.size ?: 0).toString()

        deletedFiles.clear()
        newFiles.clear()
        newUsers.clear()
        deletedMembers.clear()
        dataHolder.clear()

        if (isEditorMode && (isCreatorOrPerformer())) {
            ivBlueButton.setImageResource(R.drawable.ic_checedk_in_create_project)
        } else {
            originalData?.let { setDataToViews(it) }
            ivBlueButton.setImageResource(R.drawable.ic_circle_edit)
            dataHolder.clear()
        }

        listOf(
            etName,
            etDescription,
            etYaqm
        ).forEach { view ->
            view?.isEnabled =
                isEditorMode && (isCreator())
        }

        listOf(addFile, ivAddParticipant, ivAddObserver, ivAddFunctionalGroup).forEach { view ->
            view?.isVisible =
                isEditorMode && (isCreator())
        }
    }

    open fun setObservers() {}

    private fun setReminderDate(dateTime: LocalDateTime) {
//        dateTime.isReminderDateValid(dataHolder, originalData).let { resultPair ->
//            if (resultPair.first) {
        dataHolder.reminderDate = dateTime
        reminderDateText?.text = dateTime.toUiDateTime()
//            } else {
//                makeErrorSnack(resultPair.second)
//            }
//        }
    }

    private fun getSelectedDaysString(sumRepeat: Int): String {
        val dayBits = arrayListOf(
            MONDAY_SUM, TUESDAY_SUM, WEDNESDAY_SUM, THURSDAY_SUM, FRIDAY_SUM,
            SATURDAY_SUM, SUNDAY_SUM
        )
        val weekDays = resources.getStringArray(R.array.week_days_short)
            .toList() as java.util.ArrayList<String>

        var days = ""
        dayBits.forEachIndexed { index, it ->
            if (sumRepeat and it != 0) {
                days += "${weekDays[index]}, "
            }
        }
        return days.trim().trimEnd(',')
    }

    private fun changeRepeatMode(repeat: String): String {
        return when (repeat) {
            getString(R.string.once) -> {
                listRepetitionServer[0]
            }
            getString(R.string.every_day) -> {
                listRepetitionServer[1]
            }
            getString(R.string.every_week) -> {
                listRepetitionServer[2]
            }
            getString(R.string.every_month) -> {
                listRepetitionServer[3]
            }
            else -> {
                listRepetitionServer[4]
            }
        }
    }

    private fun setRepeat(repeat: String, byUserClick: Boolean = true) {
        repeatText?.text = repeat
        dataHolder.repeatString = changeRepeatMode(repeat)

        when (repeat) {
            getString(R.string.every_week) -> {
                if (byUserClick) {
                    dataHolder.repeatWeekRule =
                        2.0.pow(
                            (dataHolder.startDate ?: originalData?.startTime?.parseLocalDateTime()
                                ?.toLocalDate() ?: LocalDate()).dayOfWeek.toDouble() - 1
                        ).toInt()
                }
                repeatWeekText(
                    if (dataHolder.repeatWeekRule != 0) dataHolder.repeatWeekRule else originalData?.repeatRule
                        ?: 0
                )
            }
            getString(R.string.every_month) -> {
                if (byUserClick) {
                    dataHolder.repeatMonthRule = 1
                }
                repeatMonthYearText(
                    if (dataHolder.repeatMonthRule != 0) dataHolder.repeatMonthRule else originalData?.repeatRule
                        ?: 0
                )
            }
            getString(R.string.every_year) -> {
                if (byUserClick) {
                    dataHolder.repeatMonthRule = 1
                }
                repeatMonthYearText(
                    if (dataHolder.repeatMonthRule != 0) dataHolder.repeatMonthRule else originalData?.repeatRule
                        ?: 0
                )
            }
        }

        if (repeat != getString(R.string.once) && repeat != getString(R.string.every_day)) {
            repeatTypeLayout?.visible()
        } else {
            repeatTypeLayout?.hide()
        }
        if (repeat != getString(R.string.once)) {
            reminderDateLayout?.visible()
        } else {
            reminderDateLayout?.hide()
        }

        if (byUserClick) {
            showRepeatRule(repeat)
        }
    }

    private fun showRepeatRule(repeat: String) {
        when (repeat) {
            getString(R.string.every_week) -> {
                RepeatRuleWeeklyDialog(
                    requireContext(),
                    if (dataHolder.repeatWeekRule == 0) originalData?.repeatRule
                        ?: 0 else dataHolder.repeatWeekRule
                ) { it ->
                    dataHolder.repeatWeekRule = it
                    repeatWeekText(it)
                }.show()
            }
            getString(R.string.every_month) -> {
                RadioGroupDialog(
                    requireContext(),
                    dataHolder.startDate?.toDateTime(LocalTime())
                        ?: originalData?.startTime?.parseLocalDateTime()?.toDateTime()
                        ?: DateTime(),
                    if (dataHolder.repeatMonthRule == 0) originalData?.repeatRule
                        ?: 0 else dataHolder.repeatMonthRule,
                    true
                ) { it ->
                    dataHolder.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
            getString(R.string.every_year) -> {
                RadioGroupDialog(
                    requireContext(),
                    dataHolder.startDate?.toDateTime(LocalTime())
                        ?: originalData?.startTime?.parseLocalDateTime()?.toDateTime()
                        ?: DateTime(),
                    if (dataHolder.repeatMonthRule == 0) originalData?.repeatRule
                        ?: 0 else dataHolder.repeatMonthRule,
                    false
                ) { it ->
                    dataHolder.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
        }
    }

    private fun repeatMonthYearText(it: Int) {
        when (it) {
            REPEAT_SAME_DAY -> {
                repeatTypeText?.text = getString(R.string.repeat_type_by_day)
            }
            REPEAT_ORDER_WEEKDAY -> {
                repeatTypeText?.text = getString(R.string.repeat_type_by_dayofweek)
            }
            REPEAT_ORDER_WEEKDAY_USE_LAST -> {
                repeatTypeText?.text = getString(R.string.repeat_type_by_end_week)
            }
            REPEAT_LAST_DAY -> {
                repeatTypeText?.text = getString(R.string.repeat_type_by_end)
            }
        }
    }

    private fun repeatWeekText(it: Int) {
        when (it) {
            WEEKEND_DAY -> {
                repeatTypeText?.text = getString(R.string.weekend_day)
            }
            WORK_DAY -> {
                repeatTypeText?.text = getString(R.string.work_day)
            }
            EVERY_DAY -> {
                repeatTypeText?.text = getString(R.string.every_day)
            }
            else -> {
                repeatTypeText?.text = getSelectedDaysString(it)
            }

        }
    }

    //todo wth... needs optimization

    private fun getStartDateTime(
        startDate: LocalDate? = null,
        startTime: LocalTime? = null,
        dataHolder: AddingDataHolder,
        originalData: BaseDetails? = null,
    ) = (startTime ?: dataHolder.startTime ?: originalData?.startTime?.parseLocalTime())?.let {
        (startDate ?: dataHolder.startDate
        ?: originalData?.startTime?.parseLocalDate())?.toLocalDateTime(it)
    }

    private fun getEndDateTime(
        endDate: LocalDate? = null,
        endTime: LocalTime? = null,
        dataHolder: AddingDataHolder,
        originalData: BaseDetails? = null
    ) = (endTime ?: dataHolder.endTime ?: originalData?.endTime?.parseLocalTime())?.let {
        (endDate ?: dataHolder.endDate
        ?: originalData?.endTime?.parseLocalDate())?.toLocalDateTime(
            it
        )
    }

    private fun getDuration(dataHolder: AddingDataHolder, originalData: BaseDetails? = null) =
        try {
            Period(
                getStartDateTime(dataHolder = dataHolder, originalData = originalData),
                getEndDateTime(dataHolder = dataHolder, originalData = originalData)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Period(0)
        }

    private fun endDateTimeFromStartDateTime(
        startDateTime: LocalDateTime,
        dataHolder: AddingDataHolder
    ) = startDateTime + getDuration(dataHolder)

    private fun startDateTimeFromEndDateTime(
        endDateTime: LocalDateTime,
        dataHolder: AddingDataHolder
    ) = endDateTime - getDuration(dataHolder)

    private fun setStartTime(time: LocalTime) {
        getStartDateTime(
            startTime = time,
            dataHolder = dataHolder,
            originalData = originalData
        )?.plus(
            getDuration(dataHolder, originalData)
        )?.let {
            dataHolder.endDate = it.toLocalDate()
            dataHolder.endTime = it.toLocalTime()
            tvEndDate.text = it.toLocalDate().toUiDate()
            tvEndTime.text = it.toLocalTime().toUiTime()
        }
        dataHolder.startTime = time
        tvStartTime.text = time.toUiTime()

//        getStartDateTime(
//            startTime = time,
//            dataHolder = dataHolder,
//            originalData = originalData
//        )?.let {
//            endDateTimeFromStartDateTime(
//                it,
//                dataHolder
//            ).let { newEndDateTime ->
//                dataHolder.apply {
//                    endDate = newEndDateTime.toLocalDate()
//                    endTime = newEndDateTime.toLocalTime()
//                }
//            }
//        }
//        time.isStartTimeValid(dataHolder, originalData).let { pair ->
//            if (pair.first) {
//                tvStartTime.text = time.toUiTime()
//                dataHolder.startTime = time
//                tvEndDate.text = dataHolder.endDate?.toUiDate()
//                tvEndTime.text = dataHolder.endTime?.toUiTime()
//            } else {
//                dataHolder.apply {
//                    endDate = tvEndDate.text.toString().parseLocalDate()
//                    endTime = tvEndTime.text.toString().parseLocalTime()
//                    tvEndDate.text = endDate?.toUiDate()
//                    tvEndTime.text = endTime?.toUiTime()
//                }
//                makeErrorSnack(pair.second)
//            }
//        }
    }

    private fun setEndTime(time: LocalTime) {
//        getEndDateTime(endTime = time, dataHolder = dataHolder, originalData = originalData)?.minus(
//            getDuration(dataHolder, originalData)
//        )?.let {
//            dataHolder.startDate = it.toLocalDate()
//            dataHolder.startTime = it.toLocalTime()
//            tvStartDate.text = it.toLocalDate().toUiDate()
//            tvStartTime.text = it.toLocalTime().toUiTime()
//        }
//        tvEndTime.text = time.toUiTime()
//        dataHolder.startTime = time

//        getEndDateTime(
//            endTime = time,
//            dataHolder = dataHolder,
//            originalData = originalData
//        )?.let {
//            endDateTimeFromStartDateTime(
//                it,
//                dataHolder
//            ).let { newStartDateTime ->
//                dataHolder.apply {
//                    startDate = newStartDateTime.toLocalDate()
//                    startTime = newStartDateTime.toLocalTime()
//                }
//            }
//        }
        time.isEndTimeValid(dataHolder, originalData).let { resultPair ->
            if (resultPair.first) {
                tvEndTime.text = time.toUiTime()
                dataHolder.endTime = time
//                tvStartDate.text = dataHolder.startDate?.toUiDate()
//                tvStartTime.text = dataHolder.startTime?.toUiTime()
            } else {
//                dataHolder.apply {
//                    startDate = tvStartDate.text.toString().parseLocalDate()
//                    startTime = tvStartTime.text.toString().parseLocalTime()
//                    tvStartDate.text = startDate?.toUiDate()
//                    tvStartTime.text = startTime?.toUiTime()
//                }
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setStartDate(date: LocalDate) {
        getStartDateTime(
            startDate = date,
            dataHolder = dataHolder,
            originalData = originalData
        )?.plus(
            getDuration(dataHolder, originalData)
        )?.let {
            dataHolder.endDate = it.toLocalDate()
            dataHolder.endTime = it.toLocalTime()
            tvEndDate.text = it.toLocalDate().toUiDate()
            tvEndTime.text = it.toLocalTime().toUiTime()
        }
        tvStartDate.text = date.toUiDate()
        dataHolder.startDate = date

//        getStartDateTime(
//            startDate = date,
//            dataHolder = dataHolder,
//            originalData = originalData
//        )?.let {
//            endDateTimeFromStartDateTime(
//                it,
//                dataHolder
//            ).let { newEndDateTime ->
//                dataHolder.apply {
//                    endDate = newEndDateTime.toLocalDate()
//                    endTime = newEndDateTime.toLocalTime()
//                }
//            }
//        }
//        date.isStartDateValid(dataHolder, originalData).let { pair ->
//            if (pair.first) {
//                tvStartDate.text = date.toUiDate()
//                dataHolder.startDate = date
//                tvEndDate.text = dataHolder.endDate?.toUiDate()
//                tvEndTime.text = dataHolder.endTime?.toUiTime()
//            } else {
//                dataHolder.apply {
//                    endDate = tvEndDate.text.toString().parseLocalDate()
//                    endTime = tvEndTime.text.toString().parseLocalTime()
//                    tvEndDate.text = endDate?.toUiDate()
//                    tvEndTime.text = endTime?.toUiTime()
//                }
//                makeErrorSnack(pair.second)
//            }
//        }
    }

    private fun setEndDate(date: LocalDate) {
//        getEndDateTime(endDate = date, dataHolder = dataHolder, originalData = originalData)?.minus(
//            getDuration(dataHolder, originalData)
//        )?.let {
//            dataHolder.startDate = it.toLocalDate()
//            dataHolder.startTime = it.toLocalTime()
//            tvStartDate.text = it.toLocalDate().toUiDate()
//            tvStartTime.text = it.toLocalTime().toUiTime()
//        }
//        tvEndTime.text = date.toUiDate()
//        dataHolder.endDate = date

//        getEndDateTime(
//            endDate = date,
//            dataHolder = dataHolder,
//            originalData = originalData
//        )?.let {
//            endDateTimeFromStartDateTime(
//                it,
//                dataHolder
//            ).let { newStartDateTime ->
//                dataHolder.apply {
//                    startDate = newStartDateTime.toLocalDate()
//                    startTime = newStartDateTime.toLocalTime()
//                }
//            }
//        }
        date.isEndDateValid(dataHolder, originalData).let { resultPair ->
            if (resultPair.first) {
                tvEndDate.text = date.toUiDate()
                dataHolder.endDate = date
//                tvStartDate.text = dataHolder.startDate?.toUiDate()
//                tvStartTime.text = dataHolder.startTime?.toUiTime()
            } else {
                makeErrorSnack(resultPair.second)
//                dataHolder.apply {
//                    startDate = tvStartDate.text.toString().parseLocalDate()
//                    startTime = tvStartTime.text.toString().parseLocalTime()
//                    tvStartDate.text = startDate?.toUiDate()
//                    tvStartTime.text = startTime?.toUiTime()
//                }
            }
        }
    }

    private fun setImportance(pair: Pair<String, String>) {
        dataHolder.importance = pair
        Pair(ivImportanceIcon, tvImportanceTitle).setImportance(pair.first, true)
    }

    private fun setStatus(status: StatusData) {
        dataHolder.status = status
        Pair(ivStatusIcon, tvStatusTitle).setStatus(status)
    }

    private fun setStatusProjectTask(status: Triple<String, String, Int>) {
        if (status.first == getString(R.string.reject_)) {

            val causeReject = CauseRejectDialogFragment()
            causeReject.show(
                (requireActivity() as HomeActivity).supportFragmentManager,
                "cause Reject Dialog"
            )
            causeReject.sendClickListener {
                tvCommentTitle?.visible()
                tvCommentReject?.visible()
                dataHolder.comment = it
                dataHolder.internalStatus = status.first
                tvInternalStatusTitle?.text = status.second
                ivStatusIcon?.setImageResource(status.third)
                tvCommentReject?.text = it
            }
        } else {
            tvCommentTitle?.hide()
            tvCommentReject?.hide()
            dataHolder.internalStatus = status.first
            tvInternalStatusTitle?.text = status.second
            ivStatusIcon?.setImageResource(status.third)
        }
//        Pair(ivStatusIcon, tvStatusTitle).setStatus(status)
    }

    open fun setLeader(leader: PersonData?) {
        dataHolder.leader = leader
        Pair(ivLeaderAvatar, tvLeaderName).setPerson(leader)
    }

    fun setPerformer(performer: PersonData?) {
        dataHolder.performer = performer
        Pair(ivPerformerAvatar, tvPerformerName).setPerson(performer)
//        if (performer?.leader != null) {
//            setLeader(performer.leader)
//        }
    }

    // TODO: 9/2/21 for v2.0 needs optimization
    fun setParticipants(participants: List<PersonData>?) {
        dataHolder.participants = participants?.toMutableList()
        participantsAdapter.submitList(participants)

        if (participants.isNullOrEmpty()) {
            tvParticipantsCount?.gone()
            ivParticipantsInCv?.setImageResource(R.drawable.ic_adding_person)
        } else {
            tvParticipantsCount?.show()
            participants[0].image.let {
                if (it != null) {
                    ivParticipantsInCv?.loadImageUrl(it, R.drawable.ic_person)
                } else {
                    ivParticipantsInCv?.setImageResource(R.drawable.ic_person)
                }
            }
            tvParticipantsCount?.text = participants.size.toString()
        }

        participants?.filter { personData ->
            originalData?.members?.map { it.user?.id }?.contains(personData.id) == false
        }?.map { it.id }?.let { newUsers.addAll(it) }

        deletedMembers.clear()
        val deletedUsers = participants?.map { it.id }?.let { list ->
            originalData?.members?.map { it.user?.id }?.subtract(list)
        }
        originalData?.members?.filter { deletedUsers?.contains(it.user?.id) == true }
            ?.map { it.id }?.let { deletedMembers.addAll(it.filterNotNull()) }
    }

    fun setObserverPersons(observers: List<PersonData>?) {
        dataHolder.observers = observers?.toMutableList()
        observersAdapter.submitList(observers)

        if (observers.isNullOrEmpty()) {
            Log.d("ASDF", "IF")
            tvObserversCount?.gone()
            ivObserversInCv?.setImageResource(R.drawable.ic_adding_person)
        } else {
            Log.d("ASDF", "ELSE")
            ivAddObserver?.show()
            tvObserversCount?.show()
            tvObserversCount?.text = observers.size.toString()
            observers[0].image.let {
                if (it != null) {
                    ivObserversInCv?.loadImageUrl(it, R.drawable.ic_person)
                } else {
                    ivObserversInCv?.setImageResource(R.drawable.ic_person)
                }
            }
        }
    }

    fun setFunctionalGroup(functionalGroup: List<PersonData>?) {
        dataHolder.functionalGroup = functionalGroup?.toMutableList()
        functionalGroupAdapter.submitList(functionalGroup)
    }

    open fun onDescriptionClick(member: MeetingMember) {}

    private fun onDeleteParticipantClick(person: PersonData) {
        if (dataHolder.participants == null) {
            dataHolder.participants =
                if (hasMembers) originalData?.members?.mapNotNull { it.user }
                    ?.toMutableList() else originalData?.participants?.toMutableList()
        }
        dataHolder.participants?.remove(person)
        participantsAdapter.submitList(dataHolder.participants?.toMutableList())
        originalData?.members?.firstOrNull { it.user?.id == person.id }?.id?.let {
            deletedMembers.add(it)
        }
        newUsers.remove(person.id)
    }

    private fun onDeleteObserverClick(person: PersonData) {
        if (dataHolder.observers == null) {
            dataHolder.observers = originalData?.spectators?.toMutableList()
        }
        dataHolder.observers?.remove(person)
        observersAdapter.submitList(dataHolder.observers?.toMutableList())
    }

    private fun onDeleteFunctionalGroupClick(person: PersonData) {
        if (dataHolder.functionalGroup == null) {
            dataHolder.functionalGroup = originalData?.functionalGroup?.toMutableList()
        }
        dataHolder.functionalGroup?.remove(person)
        functionalGroupAdapter.submitList(dataHolder.functionalGroup?.toMutableList())
    }

    override fun onLocalFileClick(item: FilesItem) {
        try {
            requireActivity().openFile(File(URI.create(item.path)))
        } catch (e: Exception) {
            makeErrorSnack(e.message.toString())
        }
    }

    override fun onRemoteFileClick(item: FilesItem) {
        try {
            when {
                item.isDownloading -> makeSuccessSnack("Yuklanmoqda...")
                item.file.exists() -> requireActivity().openFile(item.file)
                else -> try {
                    beginDownload(item)
                } catch (e: Exception) {
                    makeErrorSnack("Xatolik: ${e.message}")
                }
            }
        } catch (e: Exception) {
            makeErrorSnack("Xatolik: ${e.message}")
        }
    }

    override fun onLocalImageClick(item: FilesItem) {}

    override fun onRemoteImageClick(item: FilesItem) {}

    override fun onLocalFileDelete(item: FilesItem) {
        dataHolder.filesForAdapter?.remove(item)
        newFiles.removeAll { it.path == item.path }
        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        filesAdapter.submitList(dataHolder.filesForAdapter?.toMutableList())
    }

    override fun onRemoteFileDelete(item: FilesItem) {
        dataHolder.filesForAdapter?.remove(item)
        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        deletedFiles.add(item.remoteId)
        filesAdapter.submitList(dataHolder.filesForAdapter?.toMutableList())
    }

    override fun onLocalImageDelete(item: FilesItem) {
        dataHolder.filesForAdapter?.remove(item)
        newFiles.removeAll { it.path == item.path }
        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        filesAdapter.submitList(dataHolder.filesForAdapter?.toMutableList())
    }

    override fun onRemoteImageDelete(item: FilesItem) {
        dataHolder.filesForAdapter?.remove(item)
        deletedFiles.add(item.remoteId)
        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        filesAdapter.submitList(dataHolder.filesForAdapter?.toMutableList())
    }

    protected fun isCreator() = originalData?.creator == storage.userId ||
            originalData?.creatorObject?.id == storage.userId || storage.role == RoleEnum.OWNER.text

    private fun isModerator(): Boolean {
        var c = false
        originalData?.members?.forEachIndexed { index, meetingMember ->
            if (meetingMember.user?.id == storage.userId) {
                c = meetingMember.moderator
            }
        }
        return c
    }

    protected fun isCreatorOrPerformer() =
        isCreator() || originalData?.performer?.id == storage.userId

    private fun beginDownload(filesItem: FilesItem) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                filesAdapter.setDownloading(filesItem, true)
            }
            val path = filesItem.path
            val request = DownloadManager.Request(Uri.parse(path))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(
                    Uri.fromFile(
                        filesItem.file
                    )
                )
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setDescription("Downloading")
            val downloadManager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)

            var finishDownload = false
            var progress: Int
            while (!finishDownload) {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_FAILED -> {
                            finishDownload = true
                            withContext(Dispatchers.Main) {
                                filesAdapter.setDownloading(filesItem, false)
                            }
                        }
                        DownloadManager.STATUS_PAUSED -> {
                        }
                        DownloadManager.STATUS_PENDING -> {
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            val total =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                val downloaded =
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                progress = (downloaded * 100L / total).toInt()
                                withContext(Dispatchers.Main) {
                                    filesAdapter.setProgress(filesItem, progress)
                                }
                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100
                            finishDownload = true
                            withContext(Dispatchers.Main) {
                                filesAdapter.setDownloading(filesItem, false)
                                makeSuccessSnack("Fayl yuklandi")
                            }
                        }
                    }
                }
            }
        }
    }

    private val fileSelectResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    val uri = result.data?.data!!
                    val file = if (uri.isImageFile(requireContext())) {
                        requireContext().getCompressorFile(uri) ?: FileHelper.fromFile(
                            requireContext(),
                            uri
                        )
                    } else {
                        FileHelper.fromFile(requireContext(), uri)
                    }

                    withContext(Dispatchers.Main) {
                        val newFile = FilesItem(
                            id = filesAdapter.currentList.size,
                            title = file.name,
                            path = file.path,
                            type = "",
                            viewType = if (file.name.isImageFile()) VIEW_TYPE_IMAGE_NEW else VIEW_TYPE_OTHER_NEW,
                            size = file.length(),
                            remoteId = -1
                        )

                        if (dataHolder.files == null) {
                            dataHolder.files = mutableListOf()
                        }
                        dataHolder.files?.add(file)
                        newFiles.add(file)
                        if (dataHolder.filesForAdapter == null) {
                            dataHolder.filesForAdapter = mutableListOf()
                        }
                        dataHolder.filesForAdapter?.add(newFile)
                        filesAdapter.submitList(dataHolder.filesForAdapter?.toMutableList())
//                        filesAdapter.notifyDataSetChanged()
                        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
                    }
                }
            }
        }
}

interface BaseDetails {
    val id: Int
    val title: String?
    val description: String?
    val yaqm: String?
    val leader: PersonData?
    val performer: PersonData?
    val startTime: String?
    val endTime: String?
    val files: List<uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.File>?
    val status: StatusData?
    val importance: String?
    val participants: List<PersonData>?
    val members: List<MeetingMember>?
    val spectators: List<PersonData>?
    val functionalGroup: List<PersonData>?
    val repeat: String?
    val reminderDate: String?
    val creator: Int?
    val creatorObject: PersonData?
    val canEditTime: Boolean?
    val repeatRule: Int?
}

interface DetailUpdateViewModel {

//    fun initStatusList(): Job
}

@Parcelize
data class FilesItem(
    val id: Int,
    val title: String,
    val path: String,
    val type: String,
    val viewType: Int,
    val size: Long,
    var progress: Int = 0,
    var isDownloading: Boolean = false,
    val remoteId: Int,
    val originalFile: File? = null
) : Parcelable {
    val uriFile: Uri
        get() = Uri.parse(path)
    val file: File
        get() = File(
            App.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            uriFile.lastPathSegment
        )
}

