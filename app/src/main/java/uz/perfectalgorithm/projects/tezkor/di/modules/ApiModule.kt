package uz.perfectalgorithm.projects.tezkor.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.*
import javax.inject.Named
import javax.inject.Singleton


/**
 * Created by Davronbek Raximjanov on 16.06.2021
 **/

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun getAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun getWorkersApi(retrofit: Retrofit): WorkersApi = retrofit.create(WorkersApi::class.java)

    @Provides
    @Singleton
    fun getOthersApi(retrofit: Retrofit): OthersApi = retrofit.create(OthersApi::class.java)

    @Provides
    @Singleton
    fun getProjects(retrofit: Retrofit): ProjectApi = retrofit.create(ProjectApi::class.java)

    @Provides
    @Singleton
    fun getChannels(retrofit: Retrofit): ChannelApi = retrofit.create(ChannelApi::class.java)

    @Provides
    @Singleton
    fun getGroups(retrofit: Retrofit): ChatAllApi = retrofit.create(ChatAllApi::class.java)

    @Provides
    @Singleton
    fun getFunctionalTasks(retrofit: Retrofit): TasksApi = retrofit.create(TasksApi::class.java)

    @Provides
    @Singleton
    fun getGoalMap(retrofit: Retrofit): GoalsApi = retrofit.create(GoalsApi::class.java)

    @Provides
    @Singleton
    fun getOffers(retrofit: Retrofit): OffersApi = retrofit.create(OffersApi::class.java)

    @Provides
    @Singleton
    fun getCalendar(retrofit: Retrofit): CalendarApi = retrofit.create(CalendarApi::class.java)

    @Provides
    @Singleton
    fun getStatus(retrofit: Retrofit): ProjectStatusApi =
        retrofit.create(ProjectStatusApi::class.java)

    @Provides
    @Singleton
    fun getRepetition(retrofit: Retrofit): RepetitionApi =
        retrofit.create(RepetitionApi::class.java)

    @Provides
    @Singleton
    fun getMeeting(retrofit: Retrofit): MeetingApi =
        retrofit.create(MeetingApi::class.java)

    @Provides
    @Singleton
    fun getDating(retrofit: Retrofit): DatingApi =
        retrofit.create(DatingApi::class.java)

    @Provides
    @Singleton
    fun getTaskStatus(retrofit: Retrofit): TaskStatusApi =
        retrofit.create(TaskStatusApi::class.java)

    @Provides
    @Singleton
    fun getQuickIdea(retrofit: Retrofit): QuickIdeasApi =
        retrofit.create(QuickIdeasApi::class.java)

    @Provides
    @Singleton
    fun getTacticPlan(retrofit: Retrofit): TacticPlanApi =
        retrofit.create(TacticPlanApi::class.java)

    @Provides
    @Singleton
    fun getRating(retrofit: Retrofit): RatingApi = retrofit.create(RatingApi::class.java)

    @Provides
    @Singleton
    fun getNote(retrofit: Retrofit): NoteApi =
        retrofit.create(NoteApi::class.java)

    @Provides
    @Singleton
    fun getIdeaComment(retrofit: Retrofit): IdeaCommentApi =
        retrofit.create(IdeaCommentApi::class.java)

    @Provides
    @Singleton
    fun getNotification(retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun getCompany(retrofit: Retrofit): CompanyApi =
        retrofit.create(CompanyApi::class.java)

    @Provides
    @Singleton
    fun getQuickPlan(retrofit: Retrofit): QuickPlanApi =
        retrofit.create(QuickPlanApi::class.java)

    @Provides
    @Singleton
    fun getPaymentApi(
        courseRetrofit: Retrofit
    ): PaymentApi =
        courseRetrofit.create(PaymentApi::class.java)

    @Provides
    @Singleton
    fun getCourseApi(
        @Named("course_base_url")
        courseRetrofit: Retrofit
    ): CourseApi =
        courseRetrofit.create(CourseApi::class.java)

    @Provides
    @Singleton
    fun getManageUsers(retrofit: Retrofit): ManageUsersApi =
        retrofit.create(ManageUsersApi::class.java)

    @Provides
    @Singleton
    fun getPackageApi(retrofit: Retrofit): PackageApi =
        retrofit.create(PackageApi::class.java)

    @Provides
    @Singleton
    fun getDashboardApi(retrofit: Retrofit): DashboardApi =
        retrofit.create(DashboardApi::class.java)

    @Provides
    @Singleton
    fun getDepartmentApi(retrofit: Retrofit): DepartmentApi =
        retrofit.create(DepartmentApi::class.java)
}