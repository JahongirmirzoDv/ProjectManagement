package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard.DashboardGoalsRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard.DashboardStatisticsRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.*



interface DashboardApi {

    /**
     * Dashboard da xodim tanlash uchun
     * O'zidan kichik lavozimdagi xodimlar ro'yxatini olish
     */
    @GET("api/v1/goal/dashboard-staff-in-manage/")
    suspend fun getStaffBelow(): Response<List<StaffBelow>>

    /**
     * Dashboard da xodim tanlash uchun
     * O'zidan kichik lavozimdagi outsource xodimlar ro'yxatini olish
     */
    @GET("api/v1/goal/dashboard-outsource-in-manage/")
    suspend fun getOutsourceBelow(): Response<List<StaffBelow>>

    /**
     * Dashboard da xodim tanlash uchun
     * O'zidan kichik lavozimdagi sevimli xodimlar ro'yxatini olish
     */
    @GET("api/v1/goal/dashboard-favourite-staff-in-manage/")
    suspend fun getFavoritesBelow(): Response<List<StaffBelow>>

    /**
     * Dashboard da xodim tanlash uchun
     * O'zidan kichik lavozimdagi xodimlar va bo'limlar strukturasini olish
     */
    @GET("api/v1/goal/dashboard-structure-staff-in-manage/")
    suspend fun getStructureBelow(): Response<List<DepartmentStructureBelow>>

    /**
     * Dashabord da maqsad tanlash uchun
     * biror xodim yoki bo'limga tegishli maqsadlar ro'yxatini olish
     */
    @POST("api/v1/goal/dashboard-goals/")
    suspend fun getDashboardGoals(
        @Body request: DashboardGoalsRequest
    ): Response<DashboardGoalsResponseWrapper>

    /**
     * Dashboard da statistikani olish
     * Biror xodim (bo'lim)ga tegishli, malum vaqt oraligidagi
     * data statistikalarini olish
     */
    @POST("api/v1/goal/dashboard-by-goal/")
    suspend fun getStatistics(
        @Body request: DashboardStatisticsRequest
    ): Response<StatisticsResponseWrapper>
}