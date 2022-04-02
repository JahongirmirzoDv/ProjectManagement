package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_goal

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
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.CreateGoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateGoalBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.FuncTaskAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.ObserverTaskAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.ParticipantTaskAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.StatusDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.SharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_goal.CreateGoalViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.adding.showEditRuleDialog
import uz.perfectalgorithm.projects.tezkor.utils.adding.showSelectPersonFragment
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.setActions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Goal yaratish ekrani
 * to'liq holda tayyor
 */
@AndroidEntryPoint
class CreateGoalFragment : Fragment() {

    private var _binding: FragmentCreateGoalBinding? = null
    private val binding: FragmentCreateGoalBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private var navController: NavController? = null

    private lateinit var funcTaskAdapter: FuncTaskAdapter
    private lateinit var observerTaskAdapter: ObserverTaskAdapter
    private lateinit var participantTaskAdapter: ParticipantTaskAdapter

    private val createGoalViewModel: CreateGoalViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()


    @Inject
    lateinit var statusDialog: StatusDialog

    @Inject
    lateinit var storage: LocalStorage


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()

        navController = Navigation.findNavController(view)

        loadObservers()
        setRecycler()
        initData()
        initDropDownActions()
        loadViews()
    }


    private fun initData() {
        binding.apply {
            etGoalName.setText(createGoalViewModel.title)
            etGoalDescription.setText(createGoalViewModel.description)
            tvBeginningTime.text = createGoalViewModel.startDate
            tvBeginTime.text = createGoalViewModel.startTime
            tvEndingTime.text = createGoalViewModel.endDate
            tvEndTime.text = createGoalViewModel.endTime
            tvFilesCount.text = createGoalViewModel.files.size.toString()

            ivStatus.setImageResource(
                when (createGoalViewModel.status) {
                    "new" -> {
                        R.drawable.ic_new_status
                    }
                    "in_progress" -> {
                        R.drawable.ic_play_circle
                    }
                    "completed" -> {
                        R.drawable.ic_check_circle
                    }
                    else -> {
                        0
                    }
                }
            )
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.master.value = it
                }

            this?.getLiveData<PersonData?>(PERFORMER)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.performer.value = it
                }

            this?.getLiveData<List<PersonData>?>(PARTICIPANTS)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.participants.value = it?.toMutableList()
                }

            this?.getLiveData<List<PersonData>?>(OBSERVERS)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.spectators.value = it?.toMutableList()
                }

            this?.getLiveData<List<PersonData>?>(FUNCTIONAL_GROUP)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.functionalGroup.value = it?.toMutableList()
                }
        }

        createGoalViewModel.createGoalLiveData.observe(this, createGoalObserver)
        createGoalViewModel.allGoalMapLiveData.observe(this, allGoalMapObserver)
        createGoalViewModel.progressLiveData.observe(this, progressObserver)
        createGoalViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private fun List<AllWorkersResponse.DataItem>.getPositionIdList(): List<Int> {
        val ids = ArrayList<Int>()
        for (a in this) {
            a.id?.let { ids.add(it) }
        }
        return ids
    }

    private fun loadSharedObservers() = with(sharedViewModel) {
        performer.observe(viewLifecycleOwner, { performer ->
            createGoalViewModel.performer = performer?.id
//            binding.goalNamePerformer.text = performer?.fullName
            performer?.image?.let { binding.ivPerformer.loadImageUrl(it) }
        })

        master.observe(viewLifecycleOwner, { master ->
            createGoalViewModel.leader = master?.id
//            binding.goalNameMaster.text = master?.fullName
            master?.image?.let { binding.ivLeader.loadImageUrl(it) }
        })

        functionalGroup.observe(viewLifecycleOwner, { funcList ->
            createGoalViewModel.functionalGroup = funcList?.map { it.id }
            funcTaskAdapter.submitList(funcList?.toMutableList())
        })

        participants.observe(viewLifecycleOwner, { participantList ->
            createGoalViewModel.participants = participantList?.map { it.id }
            participantTaskAdapter.submitList(participantList?.toMutableList())
        })

        spectators.observe(viewLifecycleOwner, { observerList ->
            createGoalViewModel.spectators = observerList?.map { it.id }
            observerTaskAdapter.submitList(observerList?.toMutableList())
        })
    }

    private fun loadViews() {
        if (createGoalViewModel.goalFolderList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter<String>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    createGoalViewModel.goalFolderList?.toList()!!.map { it.title }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGoalFolder.adapter = spinnerArrayAdapter
        }

        binding.apply {

            etGoalName.addTextChangedListener {
                createGoalViewModel.title = it.toString()
            }

            etGoalDescription.addTextChangedListener {
                createGoalViewModel.description = it.toString()
            }

            createGoalBlueButton.setOnClickListener {
                hideKeyboard()
                createGoal()
            }
            btnUpload.setOnClickListener {
                hideKeyboard()
                createGoal()
            }
            imgBackButton.setOnClickListener {
                sharedViewModel.clear()
                findNavController().popBackStack()
            }

            spinnerGoalFolder.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener,
                    AdapterView.OnItemClickListener {

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        createGoalViewModel.folder =
                            createGoalViewModel.goalFolderList?.get(position)?.id
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    }
                }

            getDateTime()

            tvBeginTime.setOnClickListener {
                timePickerDialogGoal(
                    tvBeginTime,
                    requireContext(),
                    createGoalViewModel,
                    requireParentFragment()
                )
            }

            tvBeginningTime.setOnClickListener {
                datePickerDialogGoal(
                    tvBeginningTime,
                    requireContext(),
                    createGoalViewModel,
                    requireParentFragment()
                )
            }

            tvEndTime.setOnClickListener {
                timePickerDialogGoal(
                    tvEndTime,
                    requireContext(),
                    createGoalViewModel,
                    requireParentFragment()
                )
            }

            tvEndingTime.setOnClickListener {
                datePickerDialogGoal(
                    tvEndingTime,
                    requireContext(),
                    createGoalViewModel,
                    requireParentFragment()
                )
            }

            goalBeginningTimeLayout.setOnClickListener {
                dateAndTimePickerDialogGoal(
                    tvBeginningTime,
                    tvBeginTime,
                    requireContext(),
                    createGoalViewModel,
                    requireParentFragment()
                )
            }

            goalEndingTimeLayout.setOnClickListener {
                dateAndTimePickerDialogGoal(
                    tvEndingTime,
                    tvEndTime,
                    requireContext(),
                    createGoalViewModel,
                    requireParentFragment()

                )
            }

            createGoalViewModel.canEditTime = true
            ivTimeEditRule.setOnClickListener {
                showEditRuleDialog(createGoalViewModel.canEditTime ?: true) {
                    createGoalViewModel.canEditTime = it
                    if (it) {
                        ivTimeEditRule.rotation = 0f
                    } else {
                        ivTimeEditRule.rotation = -45f
                    }
                }
            }

            cvFiles.setOnClickListener {
                hideKeyboard()
                getFileFromDevice()
            }

            cvParticipants.setOnClickListener {
                storage.participant = PARTICIPANTS
                if (sharedViewModel.participants.value.isNullOrEmpty()) {
                    storage.persons = emptySet()
                } else {
                    storage.persons =
                        sharedViewModel.participants.value?.map { it.id.toString() }?.toSet()
                            ?: emptySet()
                }
                showSelectPersonFragment("Ishtirokchilarni tanlang")
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

            functionalGroupAdd.setOnClickListener {
                storage.participant = FUNCTIONAL_GROUP
                if (sharedViewModel.functionalGroup.value.isNullOrEmpty()) {
                    storage.persons = emptySet()
                } else {
                    storage.persons =
                        sharedViewModel.functionalGroup.value?.map { it.id.toString() }?.toSet()
                            ?: emptySet()
                }
                showSelectPersonFragment("Funksional guruhni tanlang")
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
        }
        participantTaskAdapter.setOnItemDeleteClickListener { particiapntData ->
            val ls = participantTaskAdapter.currentList.toMutableList()
            val pos = ls.indexOfFirst { it.id == particiapntData.id }
            ls.removeAt(pos)
            sharedViewModel.participants.value = ls
        }

        funcTaskAdapter.setOnItemDeleteClickListener { functionalData ->
            val ls = funcTaskAdapter.currentList.toMutableList()
            val pos = ls.indexOfFirst { it.id == functionalData.id }
            ls.removeAt(pos)
            sharedViewModel.functionalGroup.value = ls
        }

        observerTaskAdapter.setOnItemDeleteClickListener { observerData ->
            val ls = observerTaskAdapter.currentList.toMutableList()
            val pos = ls.indexOfFirst { it.id == observerData.id }
            ls.removeAt(pos)
            sharedViewModel.spectators.value = ls
        }
    }

    private fun getDateTime() {
        binding.apply {
            val startDate = SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))
            val endDate = SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis() + 3600 * 1000))
            val endTime =
                SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis() + 3600 * 1000))
            val startTime = SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis()))
            createGoalViewModel.startDate = startDate
            createGoalViewModel.endDate = endDate
            createGoalViewModel.startTime = startTime
            createGoalViewModel.endTime = endTime

            tvBeginningTime.text = createGoalViewModel.startDate
            tvEndingTime.text = createGoalViewModel.endDate
            tvBeginTime.text = createGoalViewModel.startTime
            tvEndTime.text = createGoalViewModel.endTime
        }
    }


    private fun getFileFromDevice() {
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
                            createGoalViewModel.files.add(it)
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

            val isInputCompleted = isInputCompleted(
                listOf(
                    Triple(
                        etGoalName.text.isNullOrBlank(),
                        etGoalName,
                        getString(R.string.input_goal_name)
                    ),
//                    Triple(
//                        goalNamePerformer.text.isNullOrBlank(),
//                        tvPerformer,
//                        getString(R.string.input_executor)
//                    ),
//                    Triple(
//                        goalNameMaster.text.isNullOrBlank(),
//                        tvMaster,
//                        getString(R.string.input_leader)
//                    ),
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
                        createGoalViewModel.status.isNullOrBlank(),
                        tvTitleStatus,
                        getString(R.string.input_status)
                    ),
                    Triple(
                        createGoalViewModel.goalFolderList?.size == 0,
                        linkToMapTitle,
                        getString(R.string.goal_foler_error)
                    )
                ), nestedScroll
            )
//
//            if (etGoalName.text.isNullOrEmpty()) {
//                etGoalName.error = getString(R.string.input_goal_name)
//                isInputCompleted = false
//            }
//            if (goalNamePerformer.text.toString().isEmpty()) {
//                tvPerformer.showTooltip(getString(R.string.input_executor))
//                isInputCompleted = false
//            }
//            if (goalNameMaster.text.toString().isEmpty()) {
//                tvMaster.showTooltip(getString(R.string.input_leader))
//                isInputCompleted = false
//            }
//            if (tvBeginTime.text.isEmpty() && tvBeginTime.text.isEmpty()) {
//                tvBegingTimeTitle.showTooltip(getString(R.string.input_start_time))
//                isInputCompleted = false
//            }
//            if (tvEndingTime.text.isEmpty() && tvEndTime.text.isEmpty()) {
//                tvEndingTimeTitle.showTooltip(getString(R.string.input_end_time))
//                isInputCompleted = false
//            }
//            if (createGoalViewModel.status?.isEmpty() == true) {
//                statusTitle.showTooltip(getString(R.string.input_status))
//                isInputCompleted = false
//            }
//            if (createGoalViewModel.goalFolderList?.size == 0) {
//                linkToMapTitle.showTooltip(getString(R.string.goal_foler_error))
//                isInputCompleted = false
//            }

            if (isInputCompleted) {
                createGoalViewModel.createGoal(
                    createGoalViewModel.title!!,
                    createGoalViewModel.description,
                    createGoalViewModel.performer!!,
                    createGoalViewModel.leader!!,
                    createGoalViewModel.startDate + " " + createGoalViewModel.startTime,
                    createGoalViewModel.endDate + " " + createGoalViewModel.endTime,
                    createGoalViewModel.files,
                    createGoalViewModel.folder!!,
                    createGoalViewModel.status!!,
                    createGoalViewModel.participants,
                    createGoalViewModel.spectators,
                    createGoalViewModel.functionalGroup,
                    createGoalViewModel.canEditTime ?: false
                )
            }
        }
    }

    private fun setRecycler() {
        funcTaskAdapter = FuncTaskAdapter()
        binding.functionalRecycler.adapter = funcTaskAdapter
        binding.functionalRecycler.requestFocus()

        observerTaskAdapter = ObserverTaskAdapter()
//        binding.recyclerObserve.adapter = observerTaskAdapter
//        binding.recyclerObserve.requestFocus()

        participantTaskAdapter = ParticipantTaskAdapter()
//        binding.participantRecycler.adapter = participantTaskAdapter
//        binding.participantRecycler.requestFocus()
    }

    private val createGoalObserver = Observer<CreateGoalData> {
        sharedViewModel.clear()
        makeSuccessSnack("Muvaffaqqiyatli yaratildi")
        findNavController().navigateUp()
    }

    private val allGoalMapObserver = Observer<List<ItemGoalMapData>> { goalMapList ->
        createGoalViewModel.goalFolderList = ArrayList()

        if (goalMapList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter<String>(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    goalMapList.map { it.title }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGoalFolder.adapter = spinnerArrayAdapter
            createGoalViewModel.goalFolderList?.addAll(goalMapList)
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
        val progressView = binding.progressLayout.progressLoader
        if (it) progressView.show()
        else progressView.gone()
    }


    private fun initDropDownActions() {
//        binding.goalParticipantLayout.setOnClickListener {
//            setActions(
//                requireContext(),
//                binding.participantRecycler,
//                binding.imageViewPart
//            )
//        }
//        binding.goalObserversLayout.setOnClickListener {
//            setActions(
//                requireContext(),
//                binding.recyclerObserve,
//                binding.imgDropDownOb
//            )
//        }
        binding.goalFunctionalGroupLayout.setOnClickListener {
            setActions(
                requireContext(),
                binding.functionalRecycler,
                binding.imgDropDownFun
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadSharedObservers()
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

}