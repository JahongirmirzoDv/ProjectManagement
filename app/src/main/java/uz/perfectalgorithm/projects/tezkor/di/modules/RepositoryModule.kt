package uz.perfectalgorithm.projects.tezkor.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.entry.company.CompanyRepository
import uz.perfectalgorithm.projects.tezkor.domain.entry.company.CompanyRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.CreateProjectRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.CreateProjectRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.status.StatusRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.status.StatusRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_task.CreateTaskRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_task.CreateTaskRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.calendar.CalendarRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.calendar.CalendarRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.channel.ChannelRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.channel.ChannelRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.chat_services.ChatServicesRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.chat_services.ChatServicesRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.compass.CompassChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.compass.CompassChatRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.dashboard.DashboardRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.dashboard.DashboardRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.department.DepartmentRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.department.DepartmentRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.idea_comment.IdeaCommentRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.idea_comment.IdeaCommentRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.manage_users.ManageUsersRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.manage_users.ManageUsersRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.notification.NotificationRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.notification.NotificationRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.others.OthersRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.others.OthersRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints.ComplaintsRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints.ComplaintsRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.PaymentRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.PaymentRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package.PackageRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package.PackageRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan.QuickPlanRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan.QuickPlanRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.rating_idea.RatingRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.rating_idea.RatingRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan.TacticPlanRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan.TacticPlanRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.task.dating.DatingRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.dating.DatingRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.task.note.NoteRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.note.NoteRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status.ProjectStatusRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status.ProjectStatusRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.task.task_status.TaskStatusRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.task_status.TaskStatusRepositoryImpl
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun getLoginRepository(loginRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun getWorkersRepository(workersRepositoryImpl: WorkersRepositoryImpl): WorkersRepository

    @Binds
    fun getCompassChatRepository(compassChatRepositoryImpl: CompassChatRepositoryImpl): CompassChatRepository

    @Binds
    fun getOthersRepository(othersRepositoryImpl: OthersRepositoryImpl): OthersRepository

    @Binds
    fun getProjectsRepository(projectsRepositoryImpl: ProjectRepositoryImpl): ProjectRepository

    @Binds
    fun getChannelsRepository(channelsRepositoryImpl: ChannelRepositoryImpl): ChannelRepository

    @Binds
    @Singleton
    fun getChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    fun getChatSocketRepository(chatSocketRepositoryImpl: ChatSocketRepositoryImpl): ChatSocketRepository

    /* @Binds
     fun getPersonalChatRepository(personalChatRepositoryImpl: PersonalChatRepositoryImpl): PersonalChatRepository
   */

    @Binds
    fun getFunctionalTaskRepository(functionalTaskRepositoryImpl: FunctionalTaskRepositoryImpl): FunctionalTaskRepository

    @Binds
    fun getCreateGoalMapRepository(createGoalRepositoryImpl: GoalMapRepositoryImpl): GoalMapRepository

    @Binds
    fun getComplaintRepository(complaintsRepository: ComplaintsRepositoryImpl): ComplaintsRepository

    @Binds
    fun getCalendarRepository(calendarRepository: CalendarRepositoryImpl): CalendarRepository

    /* @Binds
     fun getPersonalConversationRepository(personalConversationRepositoryImpl: PersonalConversationRepositoryImpl): PersonalConversationRepository
 */
    @Binds
    fun getCreateProjectRepository(createProjectRepositoryImpl: CreateProjectRepositoryImpl): CreateProjectRepository

    @Binds
    fun getStatusRepository(statusRepositoryImpl: StatusRepositoryImpl): StatusRepository

    @Binds
    fun getCreateTaskRepository(createTaskRepositoryImpl: CreateTaskRepositoryImpl): CreateTaskRepository

    @Binds
    fun getRepetitionRepository(repetitionRepositoryImp: RepetitionRepositoryImpl): RepetitionRepository

    @Binds
    fun getQuickIdeaRepository(quickIdeaRepositoryImpl: QuickIdeasRepositoryImpl): QuickIdeasRepository

    @Binds
    fun getMeetingRepository(meetingRepository: MeetingRepositoryImpl): MeetingRepository

    @Binds
    fun getDatingRepository(datingRepository: DatingRepositoryImpl): DatingRepository

    @Binds
    fun getTaskStatusRepository(taskStatusRepository: TaskStatusRepositoryImpl): TaskStatusRepository

    @Binds
    fun getProjectStatusRepository(projectStatusRepository: ProjectStatusRepositoryImpl): ProjectStatusRepository

    @Binds
    fun getTacticPlanRepository(tacticPlanRepository: TacticPlanRepositoryImpl): TacticPlanRepository

    @Binds
    fun getNoteRepository(noteRepository: NoteRepositoryImpl): NoteRepository

    @Binds
    fun getRatingRepository(ratingRepository: RatingRepositoryImpl): RatingRepository

    @Binds
    fun getIdeaCommentRepository(ideaCommentRepository: IdeaCommentRepositoryImpl): IdeaCommentRepository

    @Binds
    fun getCompanyRepository(companyRepositoryImpl: CompanyRepositoryImpl): CompanyRepository

    @Binds
    fun getNotificationRepository(notificationRepository: NotificationRepositoryImpl): NotificationRepository

    @Binds
    fun getChatServicesRepository(chatServicesRepositoryImpl: ChatServicesRepositoryImpl): ChatServicesRepository

    @Binds
    fun getQuickPlanRepository(quickPlanRepository: QuickPlanRepositoryImpl): QuickPlanRepository

    @Binds
    fun getPaymentRepository(paymentRepository: PaymentRepositoryImpl): PaymentRepository

    @Binds
    fun getManageUsersRepository(manageUsersRepository: ManageUsersRepositoryImpl): ManageUsersRepository

    @Binds
    fun getPackageRepository(packageRepository: PackageRepositoryImpl): PackageRepository

    @Binds
    fun getDashboardRepository(dashboardRepository: DashboardRepositoryImpl): DashboardRepository

    @Binds
    fun getDepartmentRepository(department: DepartmentRepositoryImpl): DepartmentRepository
}

