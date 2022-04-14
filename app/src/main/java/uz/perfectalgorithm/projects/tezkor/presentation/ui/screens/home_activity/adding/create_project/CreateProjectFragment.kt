package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.CreateProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.toFilesItemList
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateProjectBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.DiscussedTopicAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.ImportanceDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RadioGroupDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RepeatRuleWeeklyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.SharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_project.CreateProjectViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.pow

/**
 * Project yaratish ekrani
 * TODO
 * 1. Maqsad ga bog'lash ni ixtiyoriy (not required) qilish kerak
 * 2. O'zgarishlar tarixi ni qo'shish kerak
 * 3. Proyekt rejasi (project_plan) ni proyekt/vazifaga convert qilish
 * logikasini o'zgartirish.
 * Hozir shunchaki create screenga o'tkazib yuborilyapti.
 * Figmadagi holatga keltirish kerak
 */
@AndroidEntryPoint
class CreateProjectFragment : Fragment() {
    private var _binding: FragmentCreateProjectBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private var navController: NavController? = null
    private val createProjectViewModel: CreateProjectViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val addingSharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val discussedTopicAdapter by lazy { DiscussedTopicAdapter(::onDiscussedTopicMoreClick) }
    private val listRepetitionServer by lazy { resources.getStringArray(R.array.list_repeat_server) }
    private val dateTimeFormatter by lazy { DateTimeFormat.forPattern(BACKEND_DATE_TIME_FORMAT) }
    private val sharedPref by lazy { SharedPref(requireContext()) }

    @Inject
    lateinit var importanceDialog: ImportanceDialog

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProjectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getString(CONVERTED_TITLE)?.let { title ->
            createProjectViewModel.title = title
        }
        arguments?.getInt("ID")?.let {
            createProjectViewModel.topicId = it
        }
        arguments?.getInt("PLAN_ID")?.let {
            createProjectViewModel.planId = it
        }

        hideAppBar()
        hideBottomMenu()
        navController = Navigation.findNavController(view)

        loadObservers()
        initData()
        loadViews()
    }

    private fun initData() {
        binding.apply {
            if (createProjectViewModel.repeat != null && createProjectViewModel.repeat != getString(
                    R.string.once
                ) && createProjectViewModel.repeat != getString(
                    R.string.every_day
                )
            ) {
                binding.repeatTypeLayout.visible()
            } else {
                binding.repeatTypeLayout.hide()
            }
            if (createProjectViewModel.repeat != null && createProjectViewModel.repeat != getString(
                    R.string.once
                )
            ) {
                binding.reminderDateLayout.visible()
            } else {
                binding.reminderDateLayout.hide()
            }
            createProjectViewModel.reminderDate?.let { setReminderDate(it) }
            etProjectTitle.setText(createProjectViewModel.title)
            tvStartDate.text = createProjectViewModel.startDate
            tvStartTime.text = createProjectViewModel.startTime
            tvEndDate.text = createProjectViewModel.endDate
            tvEndTime.text = createProjectViewModel.endTime
            tvFilesCount.text = createProjectViewModel.files.size.toString()

            ivImportance.setImageResource(
                when (createProjectViewModel.importance?.first) {
                    "red" -> R.drawable.ic_file_text_red
                    "yellow" -> R.drawable.ic_file_text_yellow
                    "green" -> R.drawable.ic_file_text_green
                    else -> {
                        createProjectViewModel.importance = Pair("green", "Past")
                        R.drawable.ic_file_text_green
                    }
                }
            )
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)?.observe(viewLifecycleOwner) {
                sharedViewModel.master.value = it
            }

            this?.getLiveData<PersonData?>(PERFORMER)?.observe(viewLifecycleOwner) {
                sharedViewModel.performer.value = it
            }

            this?.getLiveData<List<PersonData>?>(PARTICIPANTS)?.observe(viewLifecycleOwner) {
                sharedViewModel.participants.value = it?.toMutableList()
            }

            this?.getLiveData<List<PersonData>?>(OBSERVERS)?.observe(viewLifecycleOwner) {
                sharedViewModel.spectators.value = it?.toMutableList()
            }

            this?.getLiveData<List<File>?>(NEW_FILES)
                ?.observe(viewLifecycleOwner) {
                    createProjectViewModel.files.clear()
                    createProjectViewModel.files.addAll(it)
                    binding.tvFilesCount.text = "${createProjectViewModel.files.size}"
                }
        }

        createProjectViewModel.createProjectLiveData.observe(this, createProjectObserver)
        createProjectViewModel.allGoalLiveData.observe(this, allGoalObserver)
        createProjectViewModel.progressLiveData.observe(this, progressObserver)
        createProjectViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val createProjectObserver = Observer<CreateProjectData> {
        sharedViewModel.clear()
        addingSharedViewModel.setProjectNeedsRefresh(true)
        makeSuccessSnack(R.string.created_toast.toString())
        findNavController().navigateUp()
    }

    private val allGoalObserver = Observer<List<GoalData>> { goalList ->
        createProjectViewModel.goalList = ArrayList()

        if (goalList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter<String>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    goalList.map { it.title }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spGoals.adapter = spinnerArrayAdapter
            createProjectViewModel.goalList?.addAll(goalList)
        }
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val progressObserver = Observer<Boolean> { isLoading ->
        binding.progressLayout.progressLoader.let {
            if (isLoading) it.show()
            else it.gone()
        }
    }


    private fun loadSharedObservers() = with(sharedViewModel) {
        performer.observe(viewLifecycleOwner, { performer ->
            createProjectViewModel.performer = performer?.id
            when {
                performer?.image != null -> binding.ivPerformer.loadImageUrl(
                    performer.image,
                    R.drawable.ic_person
                )
                performer != null -> binding.ivPerformer.setImageResource(R.drawable.ic_person)
                else -> binding.ivPerformer.setImageResource(R.drawable.ic_adding_person)
            }
        })

        master.observe(viewLifecycleOwner, { master ->
            createProjectViewModel.leader = master?.id
            when {
                master?.image != null -> binding.ivLeader.loadImageUrl(
                    master.image,
                    R.drawable.ic_person
                )
                master != null -> binding.ivLeader.setImageResource(R.drawable.ic_person)
                else -> binding.ivLeader.setImageResource(R.drawable.ic_adding_person)
            }
        })

        spectators.observe(viewLifecycleOwner, { observerList ->
            createProjectViewModel.spectators = observerList?.map { it.id }
            if (observerList.isNullOrEmpty()) {
                binding.tvObserversCount.gone()
                binding.ivObservers.setImageResource(R.drawable.ic_adding_person)
            } else {
                binding.tvObserversCount.show()
                binding.tvObserversCount.text = observerList.size.toString()
                observerList[0].image.let {
                    if (it != null) {
                        binding.ivObservers.loadImageUrl(it, R.drawable.ic_person)
                    } else {
                        binding.ivObservers.setImageResource(R.drawable.ic_person)
                    }
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadViews() {
        binding.ivAuthor.loadImageUrl(storage.userImage, R.drawable.ic_person)
        if (createProjectViewModel.goalList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter<String>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    createProjectViewModel.goalList?.toList()!!.map { it.title }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spGoals.adapter = spinnerArrayAdapter
        }

        binding.apply {
            rvPlans.adapter = discussedTopicAdapter

            plansLayout.setDropDownClick(
                requireContext(),
                rvPlans,
                null
            )

            etProjectTitle.addTextChangedListener {
                createProjectViewModel.title = it.toString()
            }

            createProjectBlueButton.setOnClickListener {
                hideKeyboard()
                createProject()
            }
            btnCreateProject.setOnClickListener {
                hideKeyboard()
                createProject()
            }
            imgBackButton.setOnClickListener {
                hideKeyboard()
                sharedViewModel.clear()
                findNavController().popBackStack()
            }

            createProjectViewModel.importance = Pair("green", "Past")
            ivImportance.setImageResource(R.drawable.ic_file_text_green)
            cvImportance.setOnClickListener {
                importanceDialog.showStatusDialog(
                    requireContext(),
                    {
                        createProjectViewModel.importance = Pair("red", "Yuqori")
                        ivImportance.setImageResource(R.drawable.ic_file_text_red)
                    },
                    {
                        createProjectViewModel.importance = Pair("yellow", "O'rta")
                        ivImportance.setImageResource(R.drawable.ic_file_text_yellow)
                    },
                    {
                        createProjectViewModel.importance = Pair("green", "Past")
                        ivImportance.setImageResource(R.drawable.ic_file_text_green)
                    },
                    requireParentFragment()
                )
            }

            setRepeat(R.string.once.toString())
            repeatLayout.setOnClickListener {
                showRepeatNoteDialog(this@CreateProjectFragment::setRepeat)
            }

            repeatTypeLayout.setOnClickListener {
                val repeat = binding.tvRepeatText.text.toString()
                showRepeatRule(repeat)
            }

            reminderDateLayout.setOnClickListener {
                showReminderDateDialog(
                    if (reminderDateText.text.isNullOrBlank()) null else
                        LocalDateTime.parse(reminderDateText.text.toString(), dateTimeFormatter),
                    this@CreateProjectFragment::setReminderDate
                )
            }

            spGoals.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener,
                    AdapterView.OnItemClickListener {

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        createProjectViewModel.goal =
                            createProjectViewModel.goalList?.get(position)?.id
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
                }

            getDateTime()
            startDateTimeLayout.setOnClickListener {
                dateAndTimePickerDialogProject(
                    tvStartDate,
                    tvStartTime,
                    requireContext(),
                    createProjectViewModel,
                    requireParentFragment()
                ,)
            }

            endDateTimeLayout.setOnClickListener {
                dateAndTimePickerDialogProject(
                    tvEndDate,
                    tvEndTime,
                    requireContext(),
                    createProjectViewModel,
                    requireParentFragment()

                )
            }

            tvStartTime.setOnClickListener {
                timePickerDialogProject(
                    tvStartTime,
                    requireContext(),
                    createProjectViewModel,
                    requireParentFragment()
                )
            }

            tvStartDate.setOnClickListener {
                datePickerDialogProject(
                    tvStartDate,
                    requireContext(),
                    createProjectViewModel,
                    requireParentFragment()
                )
            }

            tvEndTime.setOnClickListener {
                timePickerDialogProject(
                    tvEndTime,
                    requireContext(),
                    createProjectViewModel,
                    requireParentFragment()
                )
            }

            tvEndDate.setOnClickListener {
                datePickerDialogProject(
                    tvEndDate,
                    requireContext(),
                    createProjectViewModel,
                    requireParentFragment()
                )
            }

            createProjectViewModel.canEditTime = true
            ivPinDateTime.setOnClickListener {
                showEditRuleDialog(createProjectViewModel.canEditTime ?: true) {
                    createProjectViewModel.canEditTime = it
                    if (it) {
                        ivPinDateTime.rotation = 0f
                    } else {
                        ivPinDateTime.rotation = -45f
                    }
                }
            }

            cvFiles.setOnClickListener {
                hideKeyboard()
                findNavController().navigate(
                    CreateProjectFragmentDirections.toSelectedFilesFragment(
                        createProjectViewModel.files.toFilesItemList().toTypedArray(),
                        true
                    )
                )
            }

            cvObservers.setOnClickListener {
                storage.participant = OBSERVERS
                if (sharedViewModel.spectators.value.isNullOrEmpty()) {
                    storage.persons = emptySet()
                } else {
                    storage.persons =
                        sharedViewModel.spectators.value?.map { it.id.toString() }?.toSet()
                            ?: emptySet()
                }
                showSelectPersonFragment("Kuzatuvchilarni tanlang")
            }

            cvPerformer.setOnClickListener {
                storage.participant = PERFORMER
                if (sharedViewModel.performer.value == null) {
                    storage.persons = emptySet()
                } else {
                    storage.persons = setOf(sharedViewModel.performer.value!!.id.toString())
                }
                showSelectPersonFragment("Ijrochini tanlang")
            }

            cvLeader.setOnClickListener {
                storage.participant = MASTER
                if (sharedViewModel.master.value == null) {
                    storage.persons = emptySet()
                } else {
                    storage.persons = setOf(sharedViewModel.master.value!!.id.toString())
                }
                showSelectPersonFragment("Rahbarni tanlang")
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
                    if (createProjectViewModel.discussedTopics == null) {
                        createProjectViewModel.discussedTopics = mutableListOf()
                    }
                    createProjectViewModel.discussedTopics?.add(
                        DiscussedTopic(
                            createProjectViewModel.discussedTopics?.size ?: 0,
                            etDiscussedTopic.text.toString()
                        )
                    )
                    discussedTopicAdapter.submitList(createProjectViewModel.discussedTopics?.toMutableList())
                    tvPlansCount.text =
                        createProjectViewModel.discussedTopics?.size?.toString() ?: "0"
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
        }
    }

    private fun getDateTime() {
        binding.apply {
            val startDate = SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))
            val endDate = SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis() + 3600 * 1000))
            val endTime =
                SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis() + 3600 * 1000))
            val startTime = SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis()))
            createProjectViewModel.startDate = startDate
            createProjectViewModel.endDate = endDate
            createProjectViewModel.startTime = startTime
            createProjectViewModel.endTime = endTime

            tvStartDate.text = createProjectViewModel.startDate
            tvEndDate.text = createProjectViewModel.endDate
            tvStartTime.text = createProjectViewModel.startTime
            tvEndTime.text = createProjectViewModel.endTime
        }
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

    private fun setReminderDate(dateTime: LocalDateTime) {
        createProjectViewModel.reminderDate = dateTime
        binding.reminderDateText.text = dateTime.toUiDateTime()
    }

    private fun setRepeat(repeat: String) {
        binding.tvRepeatText.text = repeat
        createProjectViewModel.repeat = changeRepeatMode(repeat)

        when (repeat) {
            getString(R.string.every_week) -> {
                createProjectViewModel.repeatWeekRule =
                    2.0.pow(
                        (createProjectViewModel.startDate?.parseLocalDate()
                            ?: LocalDate()).dayOfWeek.toDouble() - 1
                    )
                        .toInt()
                repeatWeekText(createProjectViewModel.repeatWeekRule)
            }
            getString(R.string.every_month) -> {
                createProjectViewModel.repeatMonthRule = 1
                repeatMonthYearText(createProjectViewModel.repeatMonthRule)
            }
            getString(R.string.every_year) -> {
                createProjectViewModel.repeatMonthRule = 1
                repeatMonthYearText(createProjectViewModel.repeatMonthRule)
            }
        }

        if (repeat != getString(R.string.once) && repeat != getString(R.string.every_day)) {
            binding.repeatTypeLayout.visible()
        } else {
            binding.repeatTypeLayout.hide()
        }
        if (repeat != getString(R.string.once)) {
            binding.reminderDateLayout.visible()
        } else {
            binding.reminderDateLayout.hide()
        }

        showRepeatRule(repeat)
    }

    private fun showRepeatRule(repeat: String) {
        when (repeat) {
            getString(R.string.every_week) -> {
                RepeatRuleWeeklyDialog(
                    requireContext(),
                    createProjectViewModel.repeatWeekRule
                ) { it ->
                    createProjectViewModel.repeatWeekRule = it
                    repeatWeekText(it)
                }.show()
            }
            getString(R.string.every_month) -> {
                RadioGroupDialog(
                    requireContext(),
                    createProjectViewModel.startDate?.parseLocalDate()?.toDateTime(LocalTime())
                        ?: DateTime(),
                    createProjectViewModel.repeatMonthRule,
                    true
                ) { it ->
                    createProjectViewModel.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
            getString(R.string.every_year) -> {
                RadioGroupDialog(
                    requireContext(),
                    createProjectViewModel.startDate?.parseLocalDate()?.toDateTime(LocalTime())
                        ?: DateTime(),
                    createProjectViewModel.repeatMonthRule,
                    false
                ) { it ->
                    createProjectViewModel.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
        }
    }

    private fun repeatMonthYearText(it: Int) {
        when (it) {
            REPEAT_SAME_DAY -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_day)
            }
            REPEAT_ORDER_WEEKDAY -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_dayofweek)
            }
            REPEAT_ORDER_WEEKDAY_USE_LAST -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_end_week)
            }
            REPEAT_LAST_DAY -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_end)
            }
        }
    }

    private fun repeatWeekText(it: Int) {
        when (it) {
            WEEKEND_DAY -> {
                binding.repeatTypeText.text = getString(R.string.weekend_day)
            }
            WORK_DAY -> {
                binding.repeatTypeText.text = getString(R.string.work_day)
            }
            EVERY_DAY -> {
                binding.repeatTypeText.text = getString(R.string.every_day)
            }
            else -> {
                binding.repeatTypeText.text = getSelectedDaysString(it)
            }

        }
    }

    private fun getFileFromDevice() {
        Permissions.check(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    resultLauncher.launch(intent)
                }

                override fun onDenied(
                    context: Context?,
                    deniedPermissions: ArrayList<String>?
                ) {
                    makeErrorSnack("Fayllarni o'qishga ruxsat berilmadi")
                }
            })
    }

    private fun createProject() {
        binding.apply {
            val isInputCompleted = isInputCompleted(
                listOf(
                    Triple(
                        etProjectTitle.text.isNullOrBlank(),
                        etProjectTitle,
                        getString(R.string.input_project_name)
                    ),
                    Triple(
                        sharedViewModel.master.value == null,
                        tvTitleLeader,
                        getString(R.string.input_leader)
                    ),
                    Triple(
                        sharedViewModel.performer.value == null,
                        tvTitlePerformer,
                        getString(R.string.input_performer)
                    ),
                    Triple(
                        tvStartDate.text.isNullOrBlank() || tvStartTime.text.isNullOrBlank(),
                        tvTitleStartDateTime,
                        getString(R.string.input_start_time)
                    ),
                    Triple(
                        tvEndDate.text.isNullOrBlank() || tvEndTime.text.isNullOrBlank(),
                        tvTitleEndDateTime,
                        getString(R.string.input_end_time)
                    ),
                    Triple(
                        createProjectViewModel.importance == null,
                        tvTitleImportance,
                        getString(R.string.input_importance)
                    ),
                    Triple(
                        createProjectViewModel.goal == null,
                        tvTitleLinking,
                        getString(R.string.goal_list_error)
                    ),
                    Triple(
                        binding.tvRepeatText.text.isBlank(),
                        binding.tvRepeatText,
                        getString(R.string.error_repeat)
                    ),
                    Triple(
                        binding.repeatTypeText.text.isBlank() && createProjectViewModel.repeat != listRepetitionServer[0] && createProjectViewModel.repeat != listRepetitionServer[1] && createProjectViewModel.repeat != null,
                        binding.repeatTypeTitle,
                        getString(R.string.error_repeat_type)
                    ),
                ), nestedScroll
            )

            if (isInputCompleted) {
                createProjectViewModel.createProject(
                    createProjectViewModel.title!!,
                    createProjectViewModel.performer!!,
                    createProjectViewModel.leader!!,
                    start_time = if (sharedPref.startTime != null) sharedPref.startTime.toString() else createProjectViewModel.startDate + " " + createProjectViewModel.startTime,
                    end_time = if (sharedPref.endTime != null) sharedPref.endTime.toString() else createProjectViewModel.endDate + " " + createProjectViewModel.endTime,
                    createProjectViewModel.files,
                    createProjectViewModel.goal!!,
                    createProjectViewModel.importance!!.first,
                    createProjectViewModel.spectators,
                    createProjectViewModel.canEditTime ?: false,
                    createProjectViewModel.repeat ?: "once",
                    createProjectViewModel.discussedTopics?.map { it.title } ?: emptyList(),
                    createProjectViewModel.reminderDate?.toUiDateTime(),
                    if (createProjectViewModel.repeat == listRepetitionServer[2]) {
                        createProjectViewModel.repeatWeekRule
                    } else {
                        createProjectViewModel.repeatMonthRule
                    },
                )
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                makeSuccessSnack("reslut ok")
                binding.tvFilesCount.text =
                    (binding.tvFilesCount.text.toString().toInt() + 1).toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val data: Intent? = result.data
                    val uri = data?.data!!
                    val compressedImageFile = getCompressorFile(requireContext(), uri)
                    withContext(Dispatchers.Main) {
                        compressedImageFile?.let {
                            createProjectViewModel.files.add(it)
                        }
                    }
                }
            }
        }

    private suspend fun getCompressorFile(context: Context, uri: Uri): File? {
        return try {
            val imageFile = FileHelper.fromFile(context, uri)
            Compressor.compress(
                context.applicationContext!!,
                imageFile.absoluteFile,
                Dispatchers.IO
            ) {
                quality(90)
            }
        } catch (e: Exception) {
            try {
                FileHelper.fromFile(requireContext(), uri)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun onDeleteDiscussedTopic(topic: DiscussedTopic) {
        createProjectViewModel.discussedTopics?.remove(topic)
        discussedTopicAdapter.submitList(createProjectViewModel.discussedTopics?.toMutableList())
        binding.tvPlansCount.text = createProjectViewModel.discussedTopics?.size?.toString() ?: "0"
    }

    private fun onDiscussedTopicMoreClick(topic: DiscussedTopic) {
        showConvertToDialog(
            {
                findNavController().navigate(
                    R.id.createProjectFragment_to_createProjectFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title
                    )
                )
            },
            {
                findNavController().navigate(
                    R.id.createProjectFragment_to_createTaskFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title
                    )
                )
            },
            true,
            {
                onDeleteDiscussedTopic(topic)
            }
        )
    }

    override fun onResume() {
        super.onResume()
        loadSharedObservers()
    }

    override fun onPause() {
        super.onPause()
        sharedPref.startTime = "${binding.tvStartDate.text} ${binding.tvStartTime.text}"
        sharedPref.endTime = "${binding.tvEndDate.text} ${binding.tvEndTime.text}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.clear()
    }
}