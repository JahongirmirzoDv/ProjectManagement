package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateToTopQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlanCreateResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlanListResponse

/**
 *Created by farrukh_kh on 8/18/21 11:42 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.apis
 **/
interface QuickPlanApi {

    @POST("api/v1/quick-plan/quick-plan/")
    suspend fun createQuickPlan(
        @Body createQuickPlanRequest: CreateQuickPlanRequest
    ): Response<QuickPlanCreateResponse>

    @GET("api/v1/quick-plan/quick-plan-retrieve/{id}/")
    suspend fun getQuickPlanById(
        @Path("id") id: Int
    ): Response<QuickPlan>

    @GET("api/v1/quick-plan/quick-plan-list-day/")
    suspend fun getQuickPlansOfDay(
        @Query("date") day: String
    ): Response<List<QuickPlan>>

    @GET("api/v1/quick-plan/quick-plan-list-week/")
    suspend fun getQuickPlansOfWeek(
        @Query("date") startDay: String
    ): Response<List<QuickPlan>>

    @GET("api/v1/quick-plan/quick-plan-list-month/")
    suspend fun getQuickPlansOfMonth(
        @Query("date") month: String
    ): Response<List<QuickPlan>>

    @GET("api/v1/quick-plan/quick-plan-list-year/")
    suspend fun getQuickPlansOfYear(
        @Query("date") year: String
    ): Response<List<QuickPlan>>

    @GET("api/v1/quick-plan/quick-plan-list/")
    suspend fun getAllQuickPlans(
        @Query("page") page: Int
    ): Response<QuickPlanListResponse>

    @PATCH("api/v1/quick-plan/quick-plan-update/{id}/")
    suspend fun updateQuickPlan(
        @Path("id") id: Int,
        @Body updateQuickPlanRequest: UpdateQuickPlanRequest
    ): Response<QuickPlan>

    @PATCH("api/v1/quick-plan/quick-plan-update/{id}/")
    suspend fun updatePositionQuickPlan(
        @Path("id") id: Int,
        @Body updateQuickPlanRequest: UpdateToTopQuickPlanRequest
    ): Response<QuickPlan>


    @DELETE("api/v1/quick-plan/quick-plan-delete/{id}/")
    suspend fun deleteQuickPlan(
        @Path("id") id: Int
    ): Response<Any>
}