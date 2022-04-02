package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_task

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.SendTaskTrue
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateTaskBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.ImportanceDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.StatusDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RadioGroupDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RepeatRuleWeeklyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.SharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_task.CreateTaskViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow


/***
 * Kurganbayev Jasurbek 11/09/2021
 * Bu Vazifa yartishdagi umumiy fragment
 * TODO
 * 1. O'zgarishlar tarixini
 * 2. Kommentariya yozish imkoniyati(tahririlar, o'chirish imkoniyatlari bilan birga)
 */
@AndroidEntryPoint
class CreateTaskFragment : Fragment(), CoroutineScope {
    private var _binding: FragmentCreateTaskBinding? = null
    private val binding: FragmentCreateTaskBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private var navController: NavController? = null

//    private val args: CreateTaskFragmentArgs by navArgs()

    private val createTaskViewModel: CreateTaskViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val addingSharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val dateTimeFormatter by lazy { DateTimeFormat.forPattern(BACKEND_DATE_TIME_FORMAT) }
    private val listRepetitionServer by lazy { resources.getStringArray(R.array.list_repeat_server) }

    @Inject
    lateinit var statusDialog: StatusDialog
    @Inject
    lateinit var credential: GoogleAccountCredential
    @Inject
    lateinit var client: com.google.api.services.calendar.Calendar

    @Inject
    lateinit var importanceDialog: ImportanceDialog

    @Inject
    lateinit var storage: LocalStorage
    private val job = Job()

    var taskTitle: String? = null
    var messageID: Int? = null
    var perform_id: PersonData? = null
    var isQuickTask: String? = null
    var quickTaskID: String? = null
    private var startDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskTitle = it.getString("titleTask")
            messageID = it.getInt("messageID")
            perform_id = it.getParcelable("personData")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        startDate = arguments?.getString(START_DATE)
        val offerTask = arguments?.getString("offer_description", "")
//        taskTitle = arguments?.getString("titleTask") as String
//        messageID = arguments?.getInt("messageID")
//        perform_id = arguments?.getParcelable("personData")

        if (startDate != null) {
            createTaskViewModel.description = offerTask
            val startDateTime = DateTime(startDate)
            val endDateTime = DateTime(startDate).plusHours(1)
            createTaskViewModel.startDate = startDateTime.toString(Formatter.SIMPLE_DAY_PATTERN)
            createTaskViewModel.startTime = startDateTime.toString(Formatter.PATTERN_HOURS_24)
            createTaskViewModel.endDate = endDateTime.toString(Formatter.SIMPLE_DAY_PATTERN)
            createTaskViewModel.endTime = endDateTime.toString(Formatter.PATTERN_HOURS_24)
        }
        arguments?.getString(CONVERTED_DATE)?.let { date ->
            createTaskViewModel.startDate = date
            createTaskViewModel.endDate = date
        }
        arguments?.getString(CONVERTED_DATE)?.let { date ->
            createTaskViewModel.startDate = date
            createTaskViewModel.endDate = date
        }
        arguments?.getString("quickTask")?.let { istask ->
            isQuickTask = istask
        }
        arguments?.getString("quickTaskID")?.let { istaskID ->
            quickTaskID = istaskID
        }
        arguments?.getInt("ID")?.let {
            createTaskViewModel.topicId = it
        }
        arguments?.getInt("PLAN_ID")?.let {
            createTaskViewModel.planId = it
        }

        /**
         * Umumiy activitydagi konfiguratsiyalar uchun tayyorlangan extension function
         * **/
        hideAppBar()
        hideBottomMenu()

        navController = Navigation.findNavController(view)

        loadObservers()
//        setRecycler()
        initData()
//        initDropDownActions()
        loadViews()
        loadArgs()
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)?.observe(viewLifecycleOwner) {
                sharedViewModel.master.value = it
            }

            this?.getLiveData<PersonData?>(PERFORMER)?.observe(viewLifecycleOwner) {
                sharedViewModel.performer.value = it
//                if (it.leader != null) {
//                    sharedViewModel.master.value = it.leader
//                }
            }

            this?.getLiveData<List<PersonData>?>(OBSERVERS)?.observe(viewLifecycleOwner) {
                sharedViewModel.spectators.value = it?.toMutableList()
            }


        }

        createTaskViewModel.createTaskLiveData.observe(this, createTaskObserver)
        createTaskViewModel.makeTaskLiveData.observe(this, makeTaskObserver)
//        createTaskViewModel.allTaskLiveData.observe(this, allTaskObserver)
        createTaskViewModel.allTaskFolderLiveData.observe(this, allTaskFunctionalFolderObservers)
        createTaskViewModel.allProjectLiveData.observe(this, allProjectObserver)
//        createTaskViewModel.allRepetitionLiveData.observe(this, allRepetitionObserver)
        createTaskViewModel.progressLiveData.observe(this, progressObserver)
        createTaskViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
//        createTaskViewModel.allStatusLiveData.observe(viewLifecycleOwner, allStatusObserver)
    }

    private val allTaskFunctionalFolderObservers =
        Observer<List<TaskFolderListItem>> { list ->

            createTaskViewModel.folderList = ArrayList()
            if (list != null) {
                val spinnerArrayAdapter =
                    ArrayAdapter(
                        this.requireContext(),
                        android.R.layout.simple_spinner_item,
                        list.map { it.title }
                    )
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTaskFolder.adapter = spinnerArrayAdapter
                createTaskViewModel.folderList?.addAll(list)
            }
        }

    private val allProjectObserver = Observer<List<ProjectData>> { list ->
        createTaskViewModel.projectList = ArrayList()

        if (list != null) {
            val spinnerArrayAdapter =
                ArrayAdapter<String>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    arrayListOf("Proyekt tanlash").apply {
                        addAll(list.map { it.title ?: "" })
                    }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = spinnerArrayAdapter
            createTaskViewModel.projectList?.addAll(list)

        }
    }

    private val makeTaskObserver = Observer<SendTaskTrue> {
        makeDestroy()
    }

    private val createTaskObserver = Observer<TaskData> {
        if(isQuickTask == "true") {
            createTaskViewModel.sendToQuickTask(quickTaskID?.toInt()?:0)
        }
        if (startDate != null) {
            credential.selectedAccountName = storage.googleCalendarAccountName
            syncGoogleCalendar()
        }
        makeDestroy()
    }

    private fun makeDestroy() {
        sharedViewModel.clear()
        addingSharedViewModel.setTaskNeedsRefresh(true)
        makeSuccessSnack("Muvaffaqqiyatli yaratildi")
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "taskSuccessful",
            true
        )
        findNavController().navigateUp()
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.progressLayout.progressLoader
        if (it) progressView.show()
        else progressView.gone()
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }


    private fun loadViews() {
//        listRepetitionServer.addAll(resources.getStringArray(R.array.list_repeat_server))


        if (createTaskViewModel.folderList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    createTaskViewModel.folderList?.toList()!!.map { it.title }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerTaskFolder.adapter = spinnerArrayAdapter
        }

        if (createTaskViewModel.projectList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter<String>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    arrayListOf("Proyekt tanlash").apply {
                        addAll(createTaskViewModel.projectList?.toList()!!.map { it.title ?: "" })
                    }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = spinnerArrayAdapter
        }

        binding.apply {


            cvMaster.setOnClickListener {
                if (sharedViewModel.master.value != null) {
                    sharedViewModel.master.value?.fullName?.let { it1 -> it.showPersonTooltip(it1) }
                }
            }
            cvPerformer.setOnClickListener {
                if (sharedViewModel.performer.value != null) {
                    sharedViewModel.performer.value?.fullName?.let { it1 -> it.showPersonTooltip(it1) }
                }
            }

            personAuthorImageView.setOnClickListener {
                it.showPersonTooltip(storage.userFirstName + " " + storage.userLastName)
            }
            cvOwner.setOnClickListener {
                it.showPersonTooltip(storage.userFirstName + " " + storage.userLastName)
            }

            etTaskName.addTextChangedListener {
                createTaskViewModel.title = it.toString()
            }


            createTaskBlueButton.setOnClickListener { createGoal() }

            btnTaskUpload.setOnClickListener { createGoal() }
            imgBackButton.setOnClickListener {
                sharedViewModel.clear()
                findNavController().popBackStack()
            }

            createTaskViewModel.importance = "green"
            createTaskViewModel.importanceText = "Past"
            importanceIcon.setImageResource(R.drawable.ic_file_text_green)
            importanceLayout.setOnClickListener {
                importanceDialog.showStatusDialog(
                    requireContext(),
                    {
                        createTaskViewModel.importance = "red"
                        createTaskViewModel.importanceText = "Yuqori"
                        importanceIcon.setImageResource(R.drawable.ic_file_text_red)
                    },
                    {
                        createTaskViewModel.importance = "yellow"
                        createTaskViewModel.importanceText = "O'rta"
                        importanceIcon.setImageResource(R.drawable.ic_file_text_yellow)
                    },
                    {
                        createTaskViewModel.importance = "green"
                        createTaskViewModel.importanceText = "Past"
                        importanceIcon.setImageResource(R.drawable.ic_file_text_green)
                    },
                    requireParentFragment()
                )
            }

            setRepeat(requireActivity().resources.getString(R.string.once))
            repeatLayout.setOnClickListener {
                showRepeatNoteDialog(this@CreateTaskFragment::setRepeat)
            }

            repeatTypeLayout.setOnClickListener {
                val repeat = binding.repeatText.text.toString()
                showRepeatRule(repeat)
            }

            reminderDateLayout.setOnClickListener {
                showReminderDateDialog(
                    if (reminderDateText.text.isNullOrBlank()) null else
                        LocalDateTime.parse(reminderDateText.text.toString(), dateTimeFormatter),
                    this@CreateTaskFragment::setReminderDate
                )
            }


            spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener,
                    AdapterView.OnItemClickListener {

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        if (position >0)
                            createTaskViewModel.project =
                                createTaskViewModel.projectList?.get(position-1)?.id.toString()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    }
                }

            spinnerTaskFolder.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener,
                    AdapterView.OnItemClickListener {

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        createTaskViewModel.folder =
                            createTaskViewModel.folderList?.get(position)?.id
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    }
                }

            getDateTime()

            taskBeginningTimeLayout.setOnClickListener {
                dateAndTimePickerDialogTask(
                    tvBeginningTime,
                    tvBeginTime,
                    requireContext(),
                    createTaskViewModel,
                    requireParentFragment()
                )
            }

            taskEndingTimeLayout.setOnClickListener {
                dateAndTimePickerDialogTask(
                    tvEndingTime,
                    tvEndTime,
                    requireContext(),
                    createTaskViewModel,
                    requireParentFragment()

                )
            }

            tvBeginTime.setOnClickListener {
                timePickerDialogTask(
                    tvBeginTime,
                    requireContext(),
                    createTaskViewModel,
                    requireParentFragment()
                )
            }

            tvBeginningTime.setOnClickListener {
                datePickerDialogTask(
                    tvBeginningTime,
                    requireContext(),
                    createTaskViewModel,
                    requireParentFragment()
                )
            }

            tvEndTime.setOnClickListener {
                timePickerDialogTask(
                    tvEndTime,
                    requireContext(),
                    createTaskViewModel,
                    requireParentFragment()
                )
            }

            tvEndingTime.setOnClickListener {
                datePickerDialogTask(
                    tvEndingTime,
                    requireContext(),
                    createTaskViewModel,
                    requireParentFragment()
                )
            }

            createTaskViewModel.canEditTime = true
            ivTimeEditRule.setOnClickListener {
                showEditRuleDialog(createTaskViewModel.canEditTime ?: true) {
                    createTaskViewModel.canEditTime = it
                    if (it) {
                        ivTimeEditRule.rotation = 0f
                    } else {
                        ivTimeEditRule.rotation = -45f
                    }
                }
            }

            fileCount.setOnClickListener {
                hideKeyboard()
                Permissions.check(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    null,
                    object :
                        PermissionHandler() {
                        override fun onGranted() {
                            makeSuccessSnack("Permission Granted")
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "*/*"
                            resultLauncher.launch(intent)
                        }

                        override fun onDenied(
                            context: Context?,
                            deniedPermissions: ArrayList<String>?
                        ) {
                            makeErrorSnack("Permission Dined")
                        }
                    })
            }


            addObserverPerson.setOnClickListener {
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


            personPerformerImageView.setOnClickListener {
                storage.participant = PERFORMER
                if (sharedViewModel.performer.value == null) {
                    storage.persons = emptySet()
                } else {
                    storage.persons = setOf(sharedViewModel.performer.value!!.id.toString())
                }
                showSelectPersonFragment("Ijrochini tanlang")
            }

            personMasterImageView.setOnClickListener {
                storage.participant = MASTER
                if (sharedViewModel.master.value == null) {
                    storage.persons = emptySet()
                } else {
                    storage.persons = setOf(sharedViewModel.master.value!!.id.toString())
                }
                showSelectPersonFragment("Rahbarni tanlang")
            }

            createTaskViewModel.isShowCalendar = false
            swShowCalendar.setOnCheckedChangeListener { buttonView, isChecked ->
                createTaskViewModel.isShowCalendar = isChecked
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
            createTaskViewModel.startDate = startDate
            createTaskViewModel.endDate = endDate
            createTaskViewModel.startTime = startTime
            createTaskViewModel.endTime = endTime

            tvBeginningTime.text = createTaskViewModel.startDate
            tvEndingTime.text = createTaskViewModel.endDate
            tvBeginTime.text = createTaskViewModel.startTime
            tvEndTime.text = createTaskViewModel.endTime
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
//        dateTime.isReminderDateValid(datingDataHolder).let { resultPair ->
//            if (resultPair.first) {
        createTaskViewModel.reminderDate = dateTime
        binding.reminderDateText.text = dateTime.toUiDateTime()
//            } else {
//                makeErrorSnack(resultPair.second)
//            }
//        }
    }

    private fun setRepeat(repeat: String) {
        binding.repeatText.text = repeat
        createTaskViewModel.repeat = changeRepeatMode(repeat)

        when (repeat) {
            getString(R.string.every_week) -> {
//                if (datingDataHolder.startDate != null) {
                createTaskViewModel.repeatWeekRule =
                    2.0.pow(
                        (createTaskViewModel.startDate?.parseLocalDate()
                            ?: LocalDate()).dayOfWeek.toDouble() - 1
                    )
                        .toInt()
                repeatWeekText(createTaskViewModel.repeatWeekRule)
//                }
            }
            getString(R.string.every_month) -> {
                createTaskViewModel.repeatMonthRule = 1
                repeatMonthYearText(createTaskViewModel.repeatMonthRule)
            }
            getString(R.string.every_year) -> {
                createTaskViewModel.repeatMonthRule = 1
                repeatMonthYearText(createTaskViewModel.repeatMonthRule)
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
                    createTaskViewModel.repeatWeekRule
                ) { it ->
                    createTaskViewModel.repeatWeekRule = it
                    repeatWeekText(it)
                }.show()
            }
            getString(R.string.every_month) -> {
                RadioGroupDialog(
                    requireContext(),
                    createTaskViewModel.startDate?.parseLocalDate()?.toDateTime(LocalTime())
                        ?: DateTime(),
                    createTaskViewModel.repeatMonthRule,
                    true
                ) { it ->
                    createTaskViewModel.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
            getString(R.string.every_year) -> {
                RadioGroupDialog(
                    requireContext(),
                    createTaskViewModel.startDate?.parseLocalDate()?.toDateTime(LocalTime())
                        ?: DateTime(),
                    createTaskViewModel.repeatMonthRule,
                    false
                ) { it ->
                    createTaskViewModel.repeatMonthRule = it
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

    private fun loadArgs() {
        if (taskTitle != null) {
            binding.etTaskName.setText(taskTitle)
            createTaskViewModel.fromMessageId = messageID
        }
        if (perform_id != null) {
            sharedViewModel.performer.value = perform_id
            sharedViewModel.master.value = perform_id?.leader
        }

    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.fileCount.text =
                    (binding.fileCount.text.toString().toInt() + 1).toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val data: Intent? = result.data
                    val uri = data?.data!!
                    val compressedImageFile = getCompressorFile(requireContext(), uri)
                    withContext(Dispatchers.Main) {
                        compressedImageFile?.let {
//                            createTaskViewModel.files.add(it)
                            sharedViewModel.files.value?.add(it)
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

    private fun createGoal() {
        binding.apply {
            val isInputCompleted = isInputCompletedAnyView(
                listOf(
                    Triple(
                        etTaskName.text.isNullOrBlank(),
                        etTaskName,
                        getString(R.string.input_task_name)
                    ),
                    Triple(
                        createTaskViewModel.leader == null,
                        hintMaster,
                        getString(R.string.input_leader)
                    ),
                    Triple(
                        createTaskViewModel.performer == null,
                        hintPerformer,
                        getString(R.string.input_leader)
                    ),
                    Triple(
                        tvBeginTime.text.isNullOrBlank() && tvBeginTime.text.isNullOrBlank(),
                        tvBegingTimeTitle,
                        getString(R.string.input_start_time)
                    ),
                    Triple(
                        tvEndingTime.text.isNullOrBlank() && tvEndTime.text.isNullOrBlank(),
                        tvEndingTimeTitle,
                        getString(R.string.input_end_time)
                    ),
                    Triple(
                        createTaskViewModel.repeat.isNullOrEmpty(),
                        repeatTitle,
                        getString(R.string.input_repetition)
                    ),
                    Triple(
                        createTaskViewModel.importance.isNullOrBlank(),
                        hintImportance,
                        getString(R.string.input_importance)
                    ),
                    Triple(
                        binding.repeatText.text.isBlank(),
                        binding.repeatText,
                        getString(R.string.error_repeat)
                    ),
                    Triple(
                        binding.repeatTypeText.text.isBlank() && createTaskViewModel.repeat != listRepetitionServer[0] && createTaskViewModel.repeat != listRepetitionServer[1] && createTaskViewModel.repeat != null,
                        binding.repeatTypeTitle,
                        getString(R.string.error_repeat_type)
                    ),
                ), nestedScroll
            )

            if (isInputCompleted) {
                createTaskViewModel.createTask(
                    title = createTaskViewModel.title!!,
                    description = createTaskViewModel.description,
                    yaqm = createTaskViewModel.yaQM,
                    performer = createTaskViewModel.performer!!,
                    leader = createTaskViewModel.leader!!,
                    start_time = createTaskViewModel.startDate + " " + createTaskViewModel.startTime,
                    end_time = createTaskViewModel.endDate + " " + createTaskViewModel.endTime,
//                    files = createTaskViewModel.files,
                    files = sharedViewModel.files.value,
                    project = createTaskViewModel.project,
                    parent = createTaskViewModel.parent,
                    folder = createTaskViewModel.folder!!,
                    importance = createTaskViewModel.importance!!,
                    participants = createTaskViewModel.participants,
                    spectators = createTaskViewModel.spectators,
                    functional_group = createTaskViewModel.functionalGroups,
                    repeat = createTaskViewModel.repeat!!,
                    canEditTime = createTaskViewModel.canEditTime ?: false,
                    reminderDate = createTaskViewModel.reminderDate?.toUiDateTime(),
                    repeatRule = if (createTaskViewModel.repeat == listRepetitionServer[2]) {
                        createTaskViewModel.repeatWeekRule
                    } else {
                        createTaskViewModel.repeatMonthRule
                    },
                )
            }
        }
    }

    private fun syncGoogleCalendar() {
        launch {

            val event: Event = Event()
                .setSummary("${createTaskViewModel.title}"?:"")
                .setDescription("${createTaskViewModel.description}")

            val startDateTime = com.google.api.client.util.DateTime("${createTaskViewModel.startDate}T${createTaskViewModel.startTime}:00-07:00")
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setStart(start)

            val endDateTime = com.google.api.client.util.DateTime("${createTaskViewModel.endDate}T${createTaskViewModel.endTime}:00-07:00")
            val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setEnd(end)

            val recurrence = arrayOf("RRULE:FREQ=DAILY","COUNT=2")
            event.recurrence = listOf(recurrence) as MutableList<String>

            val arrayList = ArrayList<EventAttendee>()
            arrayList.add(EventAttendee().setEmail(storage.calendarID))
            event.attendees = arrayList

            val remindersOverrides = ArrayList<EventReminder>()
            remindersOverrides.add(EventReminder().setMethod("email").setMinutes(24 * 60))
            remindersOverrides.add(EventReminder().setMethod("popup").setMinutes(10))

            val reminders = Event.Reminders().setUseDefault(false).setOverrides(remindersOverrides)
            event.reminders = reminders


            try {

                val e = client.events().insert("${storage.calendarID}", event).execute()
                // Do whatever you want with the Drive service
            } catch (e: UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, 1)
            }
        }
    }

    private fun initData() {
        binding.apply {
            if (createTaskViewModel.repeat != null && createTaskViewModel.repeat != getString(
                    R.string.once
                ) && createTaskViewModel.repeat != getString(
                    R.string.every_day
                )
            ) {
                binding.repeatTypeLayout.visible()
            } else {
                binding.repeatTypeLayout.hide()
            }
            if (createTaskViewModel.repeat != null && createTaskViewModel.repeat != getString(
                    R.string.once
                )
            ) {
                binding.reminderDateLayout.visible()
            } else {
                binding.reminderDateLayout.hide()
            }
            createTaskViewModel.reminderDate?.let { setReminderDate(it) }
            etTaskName.setText(createTaskViewModel.title)
            tvBeginningTime.text = createTaskViewModel.startDate
            tvBeginTime.text = createTaskViewModel.startTime
            tvEndingTime.text = createTaskViewModel.endDate
            tvEndTime.text = createTaskViewModel.endTime
//            fileCount.text = createTaskViewModel.files.size.toString()
            if (sharedViewModel.files.value == null) {
                fileCount.text = "0"
            } else {
                fileCount.text = sharedViewModel.files.value?.size.toString()
            }

            importanceIcon.setImageResource(
                when (createTaskViewModel.importance) {
                    "red" ->
                        R.drawable.ic_file_text_red
                    "yellow" ->
                        R.drawable.ic_file_text_yellow
                    "green" ->
                        R.drawable.ic_file_text_green
                    else -> 0
                }
            )
        }
    }

    private fun loadSharedObservers() = with(sharedViewModel) {
        performer.observe(viewLifecycleOwner, { performer ->
            if (performer != null) {
                createTaskViewModel.performer = performer.id
                performer.image?.let { binding.personPerformerImageView.loadImageUrl(it) }
            } else {
                binding.personPerformerImageView.setBackgroundResource(R.drawable.ic_adding_person)
            }
        })

        master.observe(viewLifecycleOwner, { master ->
            createTaskViewModel.leader = master?.id
            if (master != null) {
                master.image?.let { binding.personMasterImageView.loadImageUrl(it) }
            } else {
                binding.personMasterImageView.setBackgroundResource(R.drawable.ic_adding_person)
            }
        })

        spectators.observe(viewLifecycleOwner, { observerList ->
            createTaskViewModel.spectators = observerList?.map { it.id }
            if (observerList.isNullOrEmpty()) {
                binding.observerCount.gone()
            } else {
                binding.observerCount.show()
                binding.observerCount.text = observerList.size.toString()
                observerList[0].image.let {
                    if (it != null) {
                        binding.addObserverPerson.loadImageUrl(it)
                    } else {
                        binding.addObserverPerson.setImageResource(R.drawable.ic_user)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadSharedObservers()
        binding.personAuthorImageView.loadImageUrl(storage.userImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        sharedViewModel.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = job
}
