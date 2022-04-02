package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.CreateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.UpdateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.*

/**
 *Created by farrukh_kh on 7/29/21 10:40 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.apis
 **/
interface TacticPlanApi {

    @GET("api/v1/tactic-plan/tactic-plan-list/")
    suspend fun getTacticPlans(): Response<List<TacticPlan>>

    @GET("api/v1/tactic-plan/tactic-plan-retrieve/{id}/")
    suspend fun getTacticPlanById(
        @Path("id") tacticPlanId: Int
    ): Response<TacticPlanDetails>

    @GET("api/v1/tactic-plan/tactic-plan-status-list/")
    suspend fun getTacticPlanStatuses(): Response<List<Status>>

    @POST("api/v1/tactic-plan/tactic-plan-create/")
    suspend fun createTacticPlan(@Body createTacticPlanRequest: CreateTacticPlanRequest): Response<CreateTacticPlanResponse>

    @DELETE("api/v1/tactic-plan/tactic-plan-delete/{id}/")
    suspend fun deleteTacticPlan(@Path("id") tacticPlanId: Int): Response<Any>

    @PUT("api/v1/tactic-plan/tactic-plan-update/{id}/")
    suspend fun updateTacticPlan(
        @Path("id") tacticPlanId: Int,
        @Body tacticPlan: UpdateTacticPlanRequest
    ): Response<Any>

    @PATCH("api/v1/tactic-plan/tactic-plan-update/{id}/")
    suspend fun changeStatus(
        @Path("id") tacticPlanId: Int,
        @Body changeStatusRequest: ChangeStatusRequest
    ): Response<UpdateTacticPlanResponse>
}