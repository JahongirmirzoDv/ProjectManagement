package uz.perfectalgorithm.projects.tezkor.utils.adding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ColorRes
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.File
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_dating.CreateDatingFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_goal.CreateGoalFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_meeting.CreateMeetingFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_project.CreateProjectFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_task.CreateTaskFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.DashboardFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.BaseDetails
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.goal_map.GoalDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.offers.addOffers.AddOffersFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.dating.DatingDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.functional.TaskDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.meeting.MeetingDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.project.ProjectDetailUpdateFragmentDirections
import uz.perfectalgorithm.projects.tezkor.utils.BACKEND_DATE_TIME_FORMAT
import uz.perfectalgorithm.projects.tezkor.utils.DASHBOARD_DEPARTMENT
import uz.perfectalgorithm.projects.tezkor.utils.DASHBOARD_STAFF
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toTwoDigit
import java.util.*

/**
 *Created by farrukh_kh on 8/9/21 8:22 PM
 *uz.rdo.projects.projectmanagement.utils.adding
 **/
fun Fragment.getFileFromDevice(resultLauncher: ActivityResultLauncher<Intent>) {
    Permissions.check(
        requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE,
        null,
        object :
            PermissionHandler() {
            override fun onGranted() {
//                makeSuccessSnack("Permission Granted")
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

suspend fun Context.getCompressorFile(uri: Uri) = try {
    val imageFile = FileHelper.fromFile(this, uri)
    Compressor.compress(
        this.applicationContext!!,
        imageFile.absoluteFile,
        Dispatchers.IO
    ) {
        quality(90)
    }
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun LocalTime.toUiTime() = "${hourOfDay.toTwoDigit()}:${minuteOfHour.toTwoDigit()}"

fun LocalDate.toUiDate() = "${year}-${monthOfYear.toTwoDigit()}-${dayOfMonth.toTwoDigit()}"

fun LocalDateTime.toUiDateTime() = "${toLocalDate().toUiDate()} ${toLocalTime().toUiTime()}"

fun Fragment.showSelectPersonFragment(title: String) {
    val navController = findNavController()
    when (navController.currentDestination?.id) {
        R.id.createMeetingFragment -> findNavController().navigate(
            CreateMeetingFragmentDirections.actionCreateMeetingFragmentToSelectPersonFragment(
                title
            )
        )
        R.id.createDatingFragment -> findNavController().navigate(
            CreateDatingFragmentDirections.actionCreateDatingFragmentToSelectPersonFragment(
                title
            )
        )
        R.id.createTaskFragment -> findNavController().navigate(
            CreateTaskFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.createProjectFragment -> findNavController().navigate(
            CreateProjectFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.createGoalFragment -> findNavController().navigate(
            CreateGoalFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.navigation_add_offers -> findNavController().navigate(
            AddOffersFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.taskDetailUpdateFragment -> {
            findNavController().navigate(
                TaskDetailUpdateFragmentDirections.toSelectPersonFragment(
                    title
                )
            )
        }
        R.id.projectDetailUpdateFragment -> findNavController().navigate(
            ProjectDetailUpdateFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.goalDetailsFragment -> findNavController().navigate(
            GoalDetailUpdateFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.meetingDetailsFragment -> findNavController().navigate(
            MeetingDetailUpdateFragmentDirections.toSelectPersonFragment(
                title
            )
        )
        R.id.datingDetailsFragment -> findNavController().navigate(
            DatingDetailUpdateFragmentDirections.toSelectPersonFragment(
                title
            )
        )
    }
}

fun Fragment.showSelectPersonFragmentDashboard() {
    findNavController().navigate(
        DashboardFragmentDirections.toSelectPersonDashboardFragment(
            "Xodim yoki bo'limni tanlang"
        )
    )
}

fun Fragment.setResultDataListener(key: String, onPersonGet: SingleBlock<PersonData?>) {
    childFragmentManager.setFragmentResultListener(key, this) { _, bundle ->
        onPersonGet(bundle.getParcelable<PersonData?>(key))
    }
}

fun Fragment.setResultListListener(key: String, onListGet: SingleBlock<List<PersonData>>) {
    childFragmentManager.setFragmentResultListener(key, this) { _, bundle ->
        bundle.getParcelableArrayList<PersonData>(key)?.let {
            onListGet(it)
        }
    }
}

fun Fragment.setResultStaffDashboardListener(onPersonGet: SingleBlock<StaffBelow?>) {
    childFragmentManager.setFragmentResultListener(DASHBOARD_STAFF, this) { _, bundle ->
        onPersonGet(bundle.getParcelable<StaffBelow?>(DASHBOARD_STAFF))
    }
}

fun Fragment.setResultDepartmentDashboardListener(onPersonGet: SingleBlock<DepartmentStructureBelow?>) {
    childFragmentManager.setFragmentResultListener(DASHBOARD_DEPARTMENT, this) { _, bundle ->
        onPersonGet(bundle.getParcelable<DepartmentStructureBelow?>(DASHBOARD_DEPARTMENT))
    }
}

fun Fragment.setResultData(key: String, person: AllWorkersShort?) {
    setFragmentResult(
        key,
        bundleOf(
            key to if (person == null) null else PersonData(
                person.id!!,
                person.fullName ?: "",
                person.image ?: "",
                leader = person.leader
            )
        )
    )
}

fun Fragment.setDeletedFilesResult(deletedFiles: List<Int>) {
    setFragmentResult(
        "deleted_files",
        bundleOf(
            "deleted_files" to deletedFiles
        )
    )
}

fun Fragment.setNewFilesResult(newFiles: List<java.io.File>) {
    setFragmentResult(
        "new_files",
        bundleOf(
            "new_files" to newFiles
        )
    )
}

fun Fragment.setDeletedFilesListener(onDeletedFilesGet: SingleBlock<List<Int>>) {
    childFragmentManager.setFragmentResultListener("deleted_files", this) { _, bundle ->
        onDeletedFilesGet(bundle.getIntegerArrayList("deleted_files") ?: emptyList())
    }
}

fun Fragment.setNewFilesListener(onNewFilesGet: SingleBlock<List<java.io.File>>) {
    childFragmentManager.setFragmentResultListener("new_files", this) { _, bundle ->
        onNewFilesGet(bundle.getSerializable("new_files") as? List<java.io.File> ?: emptyList())
    }
}

fun Fragment.setResultDataDashboard(person: StaffBelow?) {
    setFragmentResult(
        DASHBOARD_STAFF,
        bundleOf(DASHBOARD_STAFF to person)
    )
}

fun Fragment.setResultDataDashboard(department: DepartmentStructureBelow?) {
    setFragmentResult(
        DASHBOARD_DEPARTMENT,
        bundleOf(DASHBOARD_DEPARTMENT to department)
    )
}

fun Fragment.setResultList(key: String, persons: List<AllWorkersShort>) {
    setFragmentResult(
        key,
        Bundle().apply {
            putParcelableArrayList(
                key,
                persons.map {
                    PersonData(
                        it.id!!,
                        it.fullName ?: "",
                        it.image ?: "",
                        it.position
                    )
                } as ArrayList<out Parcelable>
            )
        }
    )
}

private fun LocalDate.getLocalDateTime(time: LocalTime? = null) =
    toLocalDateTime(time ?: LocalTime(23, 59))

private fun LocalDateTime.isAfterDateTime(dateTime: LocalDateTime?): Boolean {
    if (dateTime == null) return true
    return isAfter(dateTime)
}

private fun LocalDateTime.isBeforeDateTime(dateTime: LocalDateTime?): Boolean {
    if (dateTime == null) return true
    return isBefore(dateTime)
}

private fun Pair<LocalDate?, LocalTime?>.isAfter(pair: Pair<LocalDate?, LocalTime?>): Boolean {
    return if (first == null) true else first?.getLocalDateTime(second)
        ?.isAfterDateTime(pair.first?.getLocalDateTime(pair.second)) == true
}

private fun Pair<LocalDate?, LocalTime?>.isBefore(pair: Pair<LocalDate?, LocalTime?>): Boolean {
    return if (first == null) true else first?.getLocalDateTime(second)
        ?.isBeforeDateTime(pair.first?.getLocalDateTime(pair.second)) == true
}

// TODO: 8/25/21 WTH...? needs code refactor
fun LocalDate.isStartDateValid(
    dataHolder: AddingDataHolder,
    originalData: BaseDetails? = null
): Pair<Boolean, String> {
    return when {
        !Pair(this, dataHolder.startTime ?: originalData?.startTime?.parseLocalTime())
            .isBefore(
                Pair(
                    dataHolder.endDate ?: originalData?.endTime?.parseLocalDate(),
                    dataHolder.endTime ?: originalData?.endTime?.parseLocalTime()
                )
            ) ->
            Pair(false, "Boshlanish vaqti tugash vaqtidan kichik bo'lishi kerak")
        (dataHolder.repeat?.key ?: originalData?.repeat) != "once" && !Pair(
            this,
            dataHolder.startTime ?: originalData?.startTime?.parseLocalTime()
        ).isBefore(
            Pair(
                dataHolder.reminderDate?.toLocalDate()
                    ?: originalData?.reminderDate?.parseLocalDate(),
                dataHolder.reminderDate?.toLocalTime()
                    ?: originalData?.reminderDate?.parseLocalTime()
            )
        ) ->
            Pair(false, "Boshlanish vaqti eslatma vaqtidan kichik bo'lishi kerak")
        else -> {
            dataHolder.startDate = this
            Pair(true, "")
        }
    }
}

fun LocalTime.isStartTimeValid(dataHolder: AddingDataHolder, originalData: BaseDetails? = null) =
    when {
        !Pair(dataHolder.startDate ?: originalData?.startTime?.parseLocalDate(), this)
            .isBefore(
                Pair(
                    dataHolder.endDate ?: originalData?.endTime?.parseLocalDate(),
                    dataHolder.endTime ?: originalData?.endTime?.parseLocalTime()
                )
            ) ->
            Pair(false, "Boshlanish vaqti tugash vaqtidan kichik bo'lishi kerak")
        (dataHolder.repeat?.key ?: originalData?.repeat) != "once" && !Pair(
            dataHolder.startDate ?: originalData?.startTime?.parseLocalDate(), this
        ).isBefore(
            Pair(
                dataHolder.reminderDate?.toLocalDate()
                    ?: originalData?.reminderDate?.parseLocalDate(),
                dataHolder.reminderDate?.toLocalTime()
                    ?: originalData?.reminderDate?.parseLocalTime()
            )
        ) ->
            Pair(false, "Boshlanish vaqti eslatma vaqtidan kichik bo'lishi kerak")
        else -> {
            dataHolder.startTime = this
            Pair(true, "")
        }
    }

fun LocalDate.isEndDateValid(dataHolder: AddingDataHolder, originalData: BaseDetails? = null) =
    if (Pair(this, dataHolder.endTime ?: originalData?.endTime?.parseLocalTime()).isAfter(
            Pair(
                dataHolder.startDate ?: originalData?.startTime?.parseLocalDate(),
                dataHolder.startTime ?: originalData?.startTime?.parseLocalTime()
            )
        )
    ) {
        dataHolder.endDate = this
        Pair(true, "")
    } else {
        Pair(false, "Tugash vaqti boshlanish vaqtidan katta bo'lishi kerak")
    }

fun LocalTime.isEndTimeValid(dataHolder: AddingDataHolder, originalData: BaseDetails? = null) =
    if (Pair(dataHolder.endDate ?: originalData?.endTime?.parseLocalDate(), this).isAfter(
            Pair(
                dataHolder.startDate ?: originalData?.startTime?.parseLocalDate(),
                dataHolder.startTime ?: originalData?.startTime?.parseLocalTime()
            )
        )
    ) {
        dataHolder.endTime = this
        Pair(true, "")
    } else {
        Pair(false, "Tugash vaqti boshlanish vaqtidan katta bo'lishi kerak")
    }

fun LocalDateTime.isReminderDateValid(
    dataHolder: AddingDataHolder,
    originalData: BaseDetails? = null
) =
    if (Pair(toLocalDate(), toLocalTime())
            .isAfter(
                Pair(
                    dataHolder.startDate ?: originalData?.startTime?.parseLocalDate(),
                    dataHolder.startTime ?: originalData?.startTime?.parseLocalTime()
                )
            )
    ) {
        dataHolder.reminderDate = this
        Pair(true, "")
    } else {
        Pair(false, "Eslatma vaqti boshlanish vaqtidan katta bo'lishi lozim")
    }

private val formatter = DateTimeFormat.forPattern(BACKEND_DATE_TIME_FORMAT)

fun String.parseLocalDate() = try {
    LocalDateTime.parse(this, formatter).toLocalDate()
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun String.parseLocalDateTime() = try {
    LocalDateTime.parse(this, formatter)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun String.parseLocalTime() = try {
    LocalDateTime.parse(this, formatter).toLocalTime()
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun View.showTooltip(
    message: String,
    @ColorRes color: Int = R.color.snack_bar_color,
    arrowPosition: Float = 0.3f,
) {
    val tooltip = createBalloon(context) {
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setArrowPosition(arrowPosition)
        setArrowOrientation(ArrowOrientation.TOP)
        setCornerRadius(4f)
        setMargin(8)
        setPadding(8)
        setText(message)
        setTextColorResource(R.color.white)
        setBackgroundColorResource(color)
        setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        setAutoDismissDuration(2_000)
        setLifecycleOwner(lifecycleOwner)
    }
    tooltip.showAlignBottom(this)
}

fun View.showPersonTooltip(text: String?) =
    showTooltip(text ?: "Noma'lum xodim", R.color.blue, 0.5f)


fun isInputCompletedWithoutScroll(
    triples: List<Triple<Boolean, TextView, String>>,
): Boolean {
    var isInputCompleted = true
    val scrollXY = IntArray(2) { Integer.MAX_VALUE }
    val xy = IntArray(2) { Integer.MAX_VALUE }

    triples.forEach { triple ->
        if (triple.first) {
            isInputCompleted = false
            triple.second.getLocationOnScreen(xy)
            if (xy[1] <= scrollXY[1]) {
                triple.second.getLocationOnScreen(scrollXY)
            }
            if (triple.second is EditText) {
                triple.second.error = triple.third
            } else {
                triple.second.showTooltip(triple.third)
            }
        }
    }
    return isInputCompleted
}

fun isInputCompleted(
    triples: List<Triple<Boolean, TextView, String>>,
    scrollView: NestedScrollView?
): Boolean {
    var isInputCompleted = true
    val scrollXY = IntArray(2) { Integer.MAX_VALUE }
    val xy = IntArray(2) { Integer.MAX_VALUE }

    triples.forEach { triple ->
        if (triple.first) {
            isInputCompleted = false
            triple.second.getLocationOnScreen(xy)
            if (xy[1] <= scrollXY[1]) {
                triple.second.getLocationOnScreen(scrollXY)
            }
            if (triple.second is EditText) {
                triple.second.error = triple.third
            } else {
                triple.second.showTooltip(triple.third)
            }
        }
    }

    if (scrollXY[0] != Integer.MAX_VALUE && scrollXY[1] != Integer.MAX_VALUE) {
        scrollView?.smoothScrollTo(
            scrollXY[0],
            if (scrollXY[1] > 100) scrollXY[1] - 100 else scrollXY[1]
        )
    }

    return isInputCompleted
}

fun isInputCompletedAnyView(
    triples: List<Triple<Boolean, TextView, String>>,
    scrollView: NestedScrollView?
): Boolean {
    var isInputCompleted = true
    val scrollXY = IntArray(2) { Integer.MAX_VALUE }
    val xy = IntArray(2) { Integer.MAX_VALUE }

    triples.forEach { triple ->
        if (triple.first) {
            isInputCompleted = false
            triple.second.getLocationOnScreen(xy)
            if (xy[1] <= scrollXY[1]) {
                triple.second.getLocationOnScreen(scrollXY)
            }
            if (triple.second is EditText) {
                triple.second.error = triple.third
            } else {
                triple.second.showTooltip(triple.third)
            }
        }
    }

    if (scrollXY[0] != Integer.MAX_VALUE && scrollXY[1] != Integer.MAX_VALUE) {
        scrollView?.smoothScrollTo(
            scrollXY[0],
            if (scrollXY[1] > 100) scrollXY[1] - 100 else scrollXY[1]
        )
    }

    return isInputCompleted
}