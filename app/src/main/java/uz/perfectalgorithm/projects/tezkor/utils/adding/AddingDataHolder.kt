package uz.perfectalgorithm.projects.tezkor.utils.adding

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.CauseCommentsResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.ParentData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskCommentData
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import java.io.File

/**
 *Created by farrukh_kh on 8/9/21 8:22 PM
 *uz.rdo.projects.projectmanagement.utils.adding
 **/

/**
 * Create va Edit qismlardagi user input ni saqlash uchun holder class
 */
class AddingDataHolder {
    var files: MutableList<File>? = null
    var filesForAdapter: MutableList<FilesItem>? = null
    var participants: MutableList<PersonData>? = null
    var observers: MutableList<PersonData>? = null
    var functionalGroup: MutableList<PersonData>? = null
    var leader: PersonData? = null
    var performer: PersonData? = null
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    var startTime: LocalTime? = null
    var endTime: LocalTime? = null
    var importance: Pair<String, String>? = null
    var status: StatusData? = null
    var internalStatus: String? = null
    var comment: String? = null
    var commentList: ArrayList<TaskCommentData>? = null
    var meetingCommentList: ArrayList<CreateMeetingComment>? = null

    //        var commentList: ArrayList<CauseCommentsResponse> = ArrayList()
    var staticStatus: Pair<String, String>? = null
    var reminder: Pair<Int, String>? = null
    var reminders: MutableSet<NoteReminder>? = null
    var repeat: RepetitionData? = null
    var repeatString: String? = null
    var reminderDate: LocalDateTime? = null
    var partnerIn: List<PersonData>? = null
    var folder: FolderData? = null
    var parent: ParentData? = null
    var goal: GoalData? = null
    var isSelectedPartner = false
    var canEditTime: Boolean? = null
    var discussedTopics: MutableList<DiscussedTopic>? = null

    var repeatWeekRule = 0
    var repeatMonthRule = 0

    fun clear() {
        files = null
        participants = null
        observers = null
        functionalGroup = null
        leader = null
        performer = null
        startDate = null
        startTime = null
        endDate = null
        endTime = null
        importance = null
        status = null
        reminder = null
        reminders = null
        repeat = null
        reminderDate = null
        partnerIn = null
//        discussedTopics = null

        repeatString = null
        repeatWeekRule = 0
        canEditTime = null
        repeatMonthRule = 0
    }
}