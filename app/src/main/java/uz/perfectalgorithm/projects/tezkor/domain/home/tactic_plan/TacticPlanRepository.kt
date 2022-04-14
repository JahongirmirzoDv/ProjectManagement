package uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan

import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.CreateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.UpdateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.*


interface TacticPlanRepository {

    suspend fun getTacticPlans(): DataWrapper<List<TacticPlan>>

    suspend fun getTacticPlanById(tacticPlanId: Int): DataWrapper<TacticPlanDetails>

    suspend fun getTacticPlanStatuses(): DataWrapper<List<Status>>

    suspend fun createTacticPlan(
        createTacticPlanRequest: CreateTacticPlanRequest
    ): DataWrapper<CreateTacticPlanResponse>

    suspend fun deleteTacticPlan(tacticPlanId: Int): DataWrapper<Any>

    suspend fun updateTacticPlan(
        tacticPlanId: Int,
        tacticPlan: UpdateTacticPlanRequest
    ): DataWrapper<Any>

    suspend fun changeStatus(
        tacticPlanId: Int,
        changeStatusRequest: ChangeStatusRequest
    ): DataWrapper<UpdateTacticPlanResponse>
}