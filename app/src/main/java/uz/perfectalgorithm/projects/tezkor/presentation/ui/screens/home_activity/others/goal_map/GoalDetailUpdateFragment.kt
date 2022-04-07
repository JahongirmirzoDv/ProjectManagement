package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.goal_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.goal.UpdateGoalRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentGoalDetailsBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateBaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map.GoalDetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map.GoalDetailUpdateViewModelFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map.provideFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.adding.showStaticStatusDialog
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/7/21 9:14 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.others.goal_map
 **/
/**
 * Goal (maqsad) detail-edit oynasi
 */
@AndroidEntryPoint
class GoalDetailUpdateFragment : DetailUpdateBaseFragment<GoalDetails>() {

    private var _binding: FragmentGoalDetailsBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val goalId by lazy { GoalDetailUpdateFragmentArgs.fromBundle(requireArguments()).goalId }
    private val folderList by lazy { mutableListOf<ItemGoalMapData>() }
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    @Inject
    lateinit var viewModelFactory: GoalDetailUpdateViewModelFactory
    private val viewModel by viewModels<GoalDetailUpdateViewModel> {
        provideFactory(viewModelFactory, goalId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalDetailsBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
    }

    override fun setupViews() = with(binding) {
        ivBackButton = imgBackButton
        etName = etGoalName
        etDescription = etGoalDescription
        tvFileCount = fileCount
        ivPerformerAvatar = goalPerformerImageAvatar
        ivLeaderAvatar = goalMasterImageAvatar
        tvPerformerName = goalNamePerformer
        tvLeaderName = goalNameMaster
        ivSelectTimeRule = ivTimeEditRule
        this@GoalDetailUpdateFragment.tvStartDate = tvBeginningTime
        this@GoalDetailUpdateFragment.tvStartTime = tvBeginTime
        this@GoalDetailUpdateFragment.tvEndDate = tvEndingTime
        this@GoalDetailUpdateFragment.tvEndTime = tvEndTime
        ivStatusIcon = statusAdd
        this@GoalDetailUpdateFragment.tvStatusTitle = tvStatusTitle
        ivBlueButton = createGoalBlueButton
        this@GoalDetailUpdateFragment.rvFiles = rvFiles
        rvParticipants = participantRecycler
        rvObservers = recyclerObserve
        rvFunctionalGroup = functionalRecycler
        ivAddParticipant = participantsAdd
        ivAddObserver = observersAdd
        ivAddFunctionalGroup = functionalGroupAdd
        addFile = fileAdd
        vgStartTime = goalBeginningTimeLayout
        vgEndTime = goalEndingTimeLayout
        vgParticipants = goalParticipantLayout
        vgObservers = goalObserversLayout
        vgFunctionalGroup = goalFunctionalGroupLayout
        ivArrowParticipants = imageViewPart
        ivArrowObservers = imgDropDownOb
        ivFunctionalGroup = imgDropDownFun
        vgLeader = goalMasterLayout
        vgPerformer = goalPerformerLayout
        baseViewModel = viewModel
    }

    override fun setClickListeners() = with(binding) {
        super.setClickListeners()

        btnUpload.setOnClickListener {
            findNavController().navigate(
                GoalDetailUpdateFragmentDirections.toDeleteDialogFragment(
                    DeleteDialogEnum.GOAL,
                    goalId
                )
            )
        }
        statusLayout.setOnClickListener {
            if (isEditorMode && isCreatorOrPerformer()) {
                showStaticStatusDialog(this@GoalDetailUpdateFragment::setStaticStatus)
            }
        }
        spinnerGoalFolder.onItemSelected { position ->
            folderList.getOrNull(position)?.let {
                dataHolder.folder = FolderData(it.id!!, it.title!!)
            }
        }
        spinnerGoalFolder.setOnTouchListener { _, event ->
            if (isEditorMode && folderList.isEmpty() && event.action == MotionEvent.ACTION_DOWN) {
                viewModel.initFolders()
            } else {
                spinnerGoalFolder.performClick()
            }
            false
        }
        ivBlueButton.setOnClickListener {
            if (!isEditorMode) {
                switchMode()
            } else {
                val isInputCompleted = if (etName != null) isInputCompleted(
                    listOf(
                        Triple(
                            etName?.text.isNullOrBlank(),
                            etName!!,
                            "Maqsad nomini kiriting"
                        ),
                    ), nsvRoot
                ) else true

                if (isInputCompleted) {
                    viewModel.updateGoal(
                        UpdateGoalRequest(
                            etName?.text.toString(),
                            etDescription?.text.toString(),
                            dataHolder.performer?.id ?: originalData!!.performer.id,
                            dataHolder.leader?.id ?: originalData!!.leader.id,
                            tvStartDate.text.toString() + " " + tvStartTime.text.toString(),
                            tvEndDate.text.toString() + " " + tvEndTime.text.toString(),
                            deletedFiles,
                            newFiles,
                            dataHolder.staticStatus?.first ?: originalData!!.statusString,
                            dataHolder.folder?.id ?: originalData!!.folder.id,
                            if (dataHolder.participants != null) dataHolder.participants!!.map { it.id } else originalData!!.participants.map { it.id },
                            if (dataHolder.observers != null) dataHolder.observers!!.map { it.id } else originalData!!.spectators.map { it.id },
                            if (dataHolder.functionalGroup != null) dataHolder.functionalGroup!!.map { it.id } else originalData!!.functionalGroup.map { it.id },
                            dataHolder.canEditTime ?: originalData?.canEditTime ?: false
                        )
                    )
                }
            }
        }
    }

    override fun setObservers() {
        super.setObservers()

        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)
                ?.observe(viewLifecycleOwner, this@GoalDetailUpdateFragment::setLeader)

            this?.getLiveData<PersonData?>(PERFORMER)
                ?.observe(viewLifecycleOwner, this@GoalDetailUpdateFragment::setPerformer)

            this?.getLiveData<List<PersonData>?>(PARTICIPANTS)
                ?.observe(viewLifecycleOwner, this@GoalDetailUpdateFragment::setParticipants)

            this?.getLiveData<List<PersonData>?>(OBSERVERS)
                ?.observe(viewLifecycleOwner, this@GoalDetailUpdateFragment::setObserverPersons)

            this?.getLiveData<List<PersonData>?>(FUNCTIONAL_GROUP)
                ?.observe(viewLifecycleOwner, this@GoalDetailUpdateFragment::setFunctionalGroup)
        }

        with(viewModel) {
            goal.simpleCollect(
                this@GoalDetailUpdateFragment,
                binding.createGoalProgressBar
            ) { goal ->
                originalData = goal
                setDataToViews(goal)
                binding.createGoalProgressBar.isVisible = false
                binding.btnUpload.isVisible = isCreator()
                ivBlueButton.isVisible = isCreatorOrPerformer() || goal.canEditTime == true
            }
            updateResponse.simpleCollect(
                this@GoalDetailUpdateFragment,
                binding.createGoalProgressBar
            ) {
                makeSuccessSnack("Muvaffaqiyatli o'zgartirildi")
                deletedFiles.clear()
                newFiles.clear()
                dataHolder.clear()
                if (isEditorMode) {
                    switchMode()
                }
                viewModel.initGoal()
                binding.createGoalProgressBar.isVisible = false
                clearUpdateResponse()
            }
            folders.simpleCollect(
                this@GoalDetailUpdateFragment,
                binding.pbLoadingFolders
            ) { folders ->
                folderList.clear()
                folderList.addAll(folders)
                if (isEditorMode) {
                    binding.spinnerGoalFolder.apply {
                        setItems(folderList.map { it.title })
//                        setSelection(folderList.map { it.id }.indexOf(originalData?.folder?.id))
                    }
                }
                binding.pbLoadingFolders.isVisible = false
            }

            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.isGoalDeleted.collect { isChanged ->
                    if (isChanged) {
                        findNavController().navigateUp()
                        sharedViewModel.setGoalDeleted(false)
                    }
                }
            }
        }
    }

    override fun setDataToViews(data: GoalDetails) = with(binding) {
        super.setDataToViews(data)

        when (data.statusString) {
            "new" -> {
                tvStatusTitle.text = R.string.new_uz.toString()
                statusAdd.setImageResource(R.drawable.ic_new)
            }
            "in_progress" -> {
                tvStatusTitle.text = R.string.in_progress.toString()
                statusAdd.setImageResource(R.drawable.ic_play_circle)
            }
            "done" -> {
                tvStatusTitle.text = R.string.done_1.toString()
                statusAdd.setImageResource(R.drawable.ic_check_circle)
            }
            else -> {
                tvStatusTitle.text = ""
                statusAdd.isVisible = false
            }
        }

        spinnerGoalFolder.setItems(listOf(data.folder.title))
    }

    override fun switchMode() = with(binding) {
        super.switchMode()
        if (isEditorMode) {
            if (folderList.isEmpty()) {
                viewModel.initFolders()
            } else {
                spinnerGoalFolder.apply {
                    setItems(folderList.map { it.title })
                    setSelection(folderList.map { it.id }.indexOf(originalData?.folder?.id))
                }
            }
        }
        spinnerGoalFolder.isEnabled = isEditorMode && isCreator()
    }

    override fun onRemoteImageClick(item: FilesItem) {
        findNavController().navigate(
            GoalDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onLocalImageClick(item: FilesItem) {
        findNavController().navigate(
            GoalDetailUpdateFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setStaticStatus(pair: Pair<String, String>) {
        dataHolder.staticStatus = pair
        binding.tvStatusTitle.text = pair.second
        binding.statusAdd.setImageResource(
            when (pair.first) {
                "new" -> R.drawable.ic_new
                "in_progress" -> R.drawable.ic_play_circle
                "done" -> R.drawable.ic_check_circle
                else -> 0
            }
        )
    }
}