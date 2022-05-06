package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.dating

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.UpdateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDatingDetailUpdateBinding
import uz.perfectalgorithm.projects.tezkor.databinding.LayoutDatingPartnerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.ReminderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.ReminderNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateBaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.dating.DatingDetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.dating.DatingDetailUpdateViewModelFactory
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.dating.provideFactory
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import javax.inject.Inject


/**
 * Uchrashuv detail-edit oynasi
 *  * TODO:
 * 1. Figmadagi kabi ishtirokchilarni qo'shish kerak
 * (Kim bilan ni tagiga) (partner_in o'rniga participants)
 * 2. Figmadagi kabi status qo'shish kerak
 */
// TODO: 8/26/21 some part of code to base frag
@AndroidEntryPoint
class DatingDetailUpdateFragment : DetailUpdateBaseFragment<DatingDetails>() {

    private var _binding: FragmentDatingDetailUpdateBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val datingId by lazy { DatingDetailUpdateFragmentArgs.fromBundle(requireArguments()).datingId }
    private val repeatList by lazy { mutableListOf<RepetitionData>() }
    private val deletedReminders = mutableSetOf<Int>()
    private val newReminders = mutableSetOf<Int>()
    private val reminderAdapter by lazy { ReminderAdapter(::onDeleteReminder) }

    @Inject
    lateinit var viewModelFactory: DatingDetailUpdateViewModelFactory
    private val viewModel by viewModels<DatingDetailUpdateViewModel> {
        provideFactory(viewModelFactory, datingId)
    }

    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatingDetailUpdateBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    override fun setupViews() = with(binding) {
        this@DatingDetailUpdateFragment.repeatLayout = repeatLayout
        this@DatingDetailUpdateFragment.repeatTypeLayout = repeatTypeLayout
        this@DatingDetailUpdateFragment.reminderDateLayout = reminderDateLayout
        this@DatingDetailUpdateFragment.repeatText = repeatText
        this@DatingDetailUpdateFragment.repeatTypeText = repeatTypeText
        this@DatingDetailUpdateFragment.reminderDateText = reminderDateText
        this@DatingDetailUpdateFragment.ivBackButton = imgBackButton
        this@DatingDetailUpdateFragment.etDescription = etDatingDescription
        this@DatingDetailUpdateFragment.tvFileCount = tvFilesCount
//        this@DatingDetailUpdateFragment.ivLeaderAvatar = partnerImageAvatar
//        this@DatingDetailUpdateFragment.tvLeaderName = partnerInName
        this@DatingDetailUpdateFragment.tvStartDate = tvStartDate
        this@DatingDetailUpdateFragment.tvStartTime = tvStartTime
        this@DatingDetailUpdateFragment.tvEndDate = tvEndDate
        this@DatingDetailUpdateFragment.tvEndTime = tvEndTime
        this@DatingDetailUpdateFragment.ivImportanceIcon = ivImportance
//        this@DatingDetailUpdateFragment.tvImportanceTitle = importanceText
        this@DatingDetailUpdateFragment.ivBlueButton = createDatingBlueButton
//        this@DatingDetailUpdateFragment.rvFiles = rvFiles
//        this@DatingDetailUpdateFragment.rvParticipants = participantRecycler
//        this@DatingDetailUpdateFragment.ivAddParticipant = participantsAdd
//        this@DatingDetailUpdateFragment.addParticipants = cvParticipants
        this@DatingDetailUpdateFragment.addFileLayout = cvFiles
//        this@DatingDetailUpdateFragment.addFile = fileAdd
        this@DatingDetailUpdateFragment.vgImportance = cvImportance
        this@DatingDetailUpdateFragment.vgStartTime = startDateTimeLayout
        this@DatingDetailUpdateFragment.vgEndTime = endDateTimeLayout
//        this@DatingDetailUpdateFragment.vgParticipants = participantsLayout
//        this@DatingDetailUpdateFragment.ivArrowParticipants = imageViewPart
        this@DatingDetailUpdateFragment.baseViewModel = viewModel
    }

    override fun initViews() = with(binding) {
        super.initViews()

        rvReminders.adapter = reminderAdapter
        ivAddReminder.isVisible = isEditorMode

        listOf(ivSelectPartner).forEach { it.isClickable = isEditorMode }
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

    override fun setClickListeners() = with(binding) {
        super.setClickListeners()
        btnCreateDating.setOnClickListener {
            findNavController().navigate(
                DatingDetailUpdateFragmentDirections.actionDatingDetailsFragmentToDeleteDialogFragment(
                    DeleteDialogEnum.DATING,
                    datingId
                )
            )
        }
//        repeatLayout.setOnClickListener {
//            if (isEditorMode && repeatList.isEmpty() && isCreator()) {
//                viewModel.initRepeats()
//            } else if (isEditorMode && isCreator()) {
//                showRepeatDialog(repeatList, this@DatingDetailUpdateFragment::setRepeat)
//            }
//        }
//        reminderLayout.setOnClickListener {
//            if (isEditorMode && isCreator()) {
//                ReminderNoteDialog(
//                    requireContext(),
//                    this@DatingDetailUpdateFragment::setReminder
//                ).show()
//            }
//        }
        reminderLayout.setDropDownClick(
            requireContext(),
            rvReminders,
            null
        )
//        reminderDateLayout.setOnClickListener {
//            if (isEditorMode && isCreator()) {
//                showReminderDateDialog(
//                    if (reminderDateText.text.isNullOrBlank()) null else LocalDateTime.parse(
//                        reminderDateText.text.toString(),
//                        formatter
//                    ), this@DatingDetailUpdateFragment::setReminderDate
//                )
//            }
//        }
//        removePartner.setOnClickListener {
//            if (isEditorMode && isCreator()) {
//                dataHolder.partnerIn = null
//                binding.partnerInLayout.isVisible = false
//                dataHolder.isSelectedPartner = false
//            }
//        }
        ivSelectPartner.setOnClickListener {
            if (isEditorMode && isCreator()) {
                storage.participant = OBSERVERS
                if (dataHolder.partnerIn == null) {
                    storage.persons = emptySet()
                } else {
                    storage.persons = dataHolder.partnerIn!!.map { it.id.toString() }.toSet()
                }
                showSelectPersonFragment("Rahbarni tanlang")
            }
        }
        ivAddReminder.setOnClickListener {
            if (isEditorMode && isCreator()) {
                ReminderNoteDialog(
                    requireContext(),
                    this@DatingDetailUpdateFragment::addReminder,
                ).show()
            }
        }
        cvAuthor.setOnClickListener {
            it.showPersonTooltip(originalData?.creatorObject?.fullName)
        }
        ivBlueButton.setOnClickListener {
            if (!isEditorMode) {
                switchMode()
            } else {
                val isInputCompleted = isInputCompleted(
                    listOf(
                        Triple(
                            etPartnerOut.text.isNullOrBlank() && dataHolder.partnerIn == null && originalData?.partnerIn == null,
                            etPartnerOut,
                            "Kim bilan uchrashishni tanlang"
                        ),
                        Triple(
                            etAddress.text.isNullOrBlank(),
                            etAddress,
                            "Uchrashuv joyini kiriting"
                        ),
                    ), nsvRoot
                )

                if (isInputCompleted) {
                    viewModel.updateDating(
                        UpdateDatingRequest(
                            etDescription?.text.toString(),
                            etAddress.text.toString(),
                            dataHolder.partnerIn?.map { it.id }
                                ?: originalData!!.partnerIn?.map { it.id },
                            etPartnerOut.text.toString(),
                            tvStartDate.text.toString() + " " + tvStartTime.text.toString(),
                            tvEndDate.text.toString() + " " + tvEndTime.text.toString(),
                            deletedFiles,
                            newFiles,
                            dataHolder.importance?.first ?: originalData!!.importance,
//                            if (dataHolder.participants != null) dataHolder.participants!!.map { it.id } else originalData!!.participants.map { it.id },
//                            dataHolder.reminder?.first ?: originalData?.reminder,
                            dataHolder.reminderDate?.toUiDateTime()
                                ?: originalData?.reminderDate,
                            deletedReminders.toList(),
                            newReminders.toList(),
                            dataHolder.repeatString ?: originalData?.repeat,
                            if ((dataHolder.repeatString ?: originalData?.repeat)
                                == listRepetitionServer[2]
                            ) {
                                if (dataHolder.repeatWeekRule != 0) dataHolder.repeatWeekRule else originalData?.repeatRule
                            } else {
                                if (dataHolder.repeatMonthRule != 0) dataHolder.repeatMonthRule else originalData?.repeatRule
                            },
                        )
                    )
                }
            }
        }
    }

    override fun setObservers() {
        super.setObservers()
        with(viewModel) {
            dating.simpleCollect(
                this@DatingDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) { dating ->
                originalData = dating
                setDataToViews(dating)
                binding.progressLayout.progressLoader.isVisible = false
                binding.btnCreateDating.isVisible = isCreator()
                ivBlueButton.isVisible = isCreator()
            }

            repeats.simpleCollect(
                this@DatingDetailUpdateFragment,
                binding.pbLoadingRepetitions
            ) { repeats ->
                repeatList.clear()
                repeatList.addAll(repeats)
                binding.pbLoadingRepetitions.isVisible = false
            }

            updateResponse.simpleCollect(
                this@DatingDetailUpdateFragment,
                binding.progressLayout.progressLoader
            ) {
                makeSuccessSnack(getString(R.string.edited_toast))
                deletedFiles.clear()
                newFiles.clear()
                dataHolder.clear()
                if (isEditorMode) {
                    switchMode()
                }
                viewModel.initDating()
                binding.progressLayout.progressLoader.isVisible = false
                clearUpdateResponse()
                sharedViewModel.setDatingNeedsRefresh(true)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isDatingDeleted.collect { isDeleted ->
                if (isDeleted) {
                    findNavController().navigateUp()
                    sharedViewModel.setDatingDeleted(false)
                }
            }
        }

        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(MASTER)
                ?.observe(viewLifecycleOwner, this@DatingDetailUpdateFragment::setLeader)

            this?.getLiveData<List<PersonData>?>(OBSERVERS)
                ?.observe(viewLifecycleOwner) {
                    dataHolder.partnerIn = it
                    binding.partnerInLayout.removeAllViews()
                    for (i in it.indices) {
                        if (i < 3)
                            setPartnerIn(it[i], i)
                        else {
                            setPartnerCount(it.size - 3)
                            break
                        }
                    }
                }

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

    override fun setDataToViews(data: DatingDetails) = with(binding) {
        super.setDataToViews(data)
        etAddress.setText(data.address)

        dataHolder.isSelectedPartner = data.partnerIn != null
//        partnerInLayout.isVisible = dataHolder.isSelectedPartner

        dataHolder.partnerIn = data.partnerIn
        data.partnerIn?.let {
            binding.partnerInLayout.removeAllViews()
            for (i in it.indices) {
                if (i < 3)
                    setPartnerIn(it[i], i)
                else {
                    setPartnerCount(it.size - 3)
                    break
                }
            }
        }
        etPartnerOut.setText(data.partnerOut)

//        reminderText.text = getReminderText(data.reminder ?: 0)
        val reminders = data.reminders.map { NoteReminder(getReminderText(it), it) }
        reminderAdapter.submitList(reminders)
        if (dataHolder.reminders == null) {
            dataHolder.reminders = reminders.toMutableSet()
        }
//        reminderDateText.text = data.reminderDate
//        repeatText.text = when (data.repeat) {
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

        listOf(
            etPartnerOut,
            etAddress
        ).forEach { view ->
            view.isEnabled = isEditorMode && isCreator()
        }

        deletedReminders.clear()
        newReminders.clear()
        reminderAdapter.changeMode(isEditorMode)
        ivAddReminder.isVisible = isEditorMode
        btnCreateDating.isVisible = !isEditorMode
        val reminders = originalData?.reminders?.map { NoteReminder(getReminderText(it), it) }
        reminderAdapter.submitList(reminders)
        if (dataHolder.reminders == null) {
            dataHolder.reminders = reminders?.toMutableSet()
        }
    }

    override fun setLeader(leader: PersonData?) {
//        dataHolder.partnerIn = leader
//        Pair(ivLeaderAvatar, tvLeaderName).setPerson(leader)
//        binding.partnerInLayout.isVisible = leader != null
//        dataHolder.isSelectedPartner = leader != null
//        if (leader != null) {
//            binding.etPartnerOut.setText("")
//        }
    }

//    private fun setRepeat(repeat: RepetitionData) {
//        binding.repeatText.text = repeat.title
//        dataHolder.repeat = repeat
//        binding.reminderDateLayout.isVisible = repeat.key != "once"
//    }

//    private fun setReminder(reminder: Pair<String, Int>) {
//        val newPair = Pair(reminder.second, reminder.first)
//        binding.reminderText.text = newPair.second
//        dataHolder.reminder = newPair
//    }

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

//    private fun setReminderDate(dateTime: LocalDateTime) {
//        dateTime.isReminderDateValid(dataHolder, originalData).let { resultPair ->
//            if (resultPair.first) {
//                binding.reminderDateText.text = dateTime.toUiDateTime()
//            } else {
//                makeErrorSnack(resultPair.second)
//            }
//        }
//    }

    //    override fun onRemoteImageClick(item: FilesItem) {
//        findNavController().navigate(
//            DatingDetailUpdateFragmentDirections.toImageDetailsFragment(
//                item
//            )
//        )
//    }
//
//    override fun onLocalImageClick(item: FilesItem) {
//        findNavController().navigate(
//            DatingDetailUpdateFragmentDirections.toImageDetailsFragment(
//                item
//            )
//        )
//    }
    private fun setPartnerIn(person: PersonData?, index: Int) = with(binding) {
        if (partnerInLayout.childCount < 3) {
            val partner = LayoutDatingPartnerBinding.inflate(LayoutInflater.from(requireContext()))
            person?.image?.let {
                partner.partnerImageAvatar.loadImageUrl(it)
            }
            partner.root.layoutParams = FrameLayout.LayoutParams(100, 100).apply {
                marginStart = (index + 1) * (90)
            }
            partnerInLayout.addView(partner.root)
        }
//        if (person != null) {
//            etPartnerOut.setText("")
//        }
//        partnerInLayout.isVisible = person != null
//        etPartnerOut.isVisible = person == null
//        datingDataHolder.partnerIn = person
//        partnerInName.text = person?.fullName
    }

    private fun setPartnerCount(count: Int) {
        val text = TextView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(100, 100).apply {
                marginStart = (4) * (90)
            }
            background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_person_blue_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            text = "+$count"
            gravity = Gravity.CENTER
        }
        binding.partnerInLayout.addView(text)
    }

    private fun getReminderText(minutes: Int) = when {
        minutes == 0 -> getString(R.string.on_time)
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