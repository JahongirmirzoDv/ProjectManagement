package uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan

import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.TacticPlanApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.CreateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.UpdateTacticPlanRequest
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/29/21 10:55 AM
 *uz.rdo.projects.projectmanagement.domain.home.tactic_plan
 **/
class TacticPlanRepositoryImpl @Inject constructor(
    private val tacticPlanApi: TacticPlanApi
) : TacticPlanRepository {
    override suspend fun getTacticPlans() = try {
        val response = tacticPlanApi.getTacticPlans()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getTacticPlanById(tacticPlanId: Int) = try {
        val response = tacticPlanApi.getTacticPlanById(tacticPlanId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getTacticPlanStatuses() = try {
        val response = tacticPlanApi.getTacticPlanStatuses()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun createTacticPlan(createTacticPlanRequest: CreateTacticPlanRequest) = try {
        val response = tacticPlanApi.createTacticPlan(createTacticPlanRequest)

        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteTacticPlan(tacticPlanId: Int) = try {
        val response = tacticPlanApi.deleteTacticPlan(tacticPlanId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateTacticPlan(
        tacticPlanId: Int,
        tacticPlan: UpdateTacticPlanRequest
    ) = try {
        val response = tacticPlanApi.updateTacticPlan(tacticPlanId, tacticPlan)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun changeStatus(
        tacticPlanId: Int,
        changeStatusRequest: ChangeStatusRequest
    ) = try {
        val response = tacticPlanApi.changeStatus(tacticPlanId, changeStatusRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}