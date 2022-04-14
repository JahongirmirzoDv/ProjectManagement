package uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan

import androidx.paging.Pager
import androidx.paging.PagingConfig
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.QuickPlanApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.Position
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateToTopQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.source.QuickPlanPagingSource
import javax.inject.Inject


class QuickPlanRepositoryImpl @Inject constructor(
    private val quickPlanApi: QuickPlanApi
) : QuickPlanRepository {
    override suspend fun createQuickPlan(createQuickPlanRequest: CreateQuickPlanRequest) = try {
        val response = quickPlanApi.createQuickPlan(createQuickPlanRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!.data)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getQuickPlanById(id: Int) = try {
        val response = quickPlanApi.getQuickPlanById(id)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getQuickPlansOfDay(day: String) = try {
        val response = quickPlanApi.getQuickPlansOfDay(day)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getQuickPlansOfWeek(startDay: String) = try {
        val response = quickPlanApi.getQuickPlansOfWeek(startDay)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getQuickPlansOfMonth(month: String) = try {
        val response = quickPlanApi.getQuickPlansOfMonth(month)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getQuickPlansOfYear(year: String) = try {
        val response = quickPlanApi.getQuickPlansOfYear(year)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateQuickPlan(
        id: Int,
        updateQuickPlanRequest: UpdateQuickPlanRequest
    ) = try {
        val response = quickPlanApi.updateQuickPlan(id, updateQuickPlanRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateToTopQuickPlan(
        id: Int,
    ) = try {
        val updateQuickPlanRequest = UpdateToTopQuickPlanRequest(parentId = "")
        val response = quickPlanApi.updatePositionQuickPlan(id, updateQuickPlanRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updatePositionQuickPlan(
        id: Int, upper: Int?, lower: Int?
    ) = try {
        val updateQuickPlanRequest =
            UpdateToTopQuickPlanRequest(position = Position(upper = upper, lower = lower))
        val response = quickPlanApi.updatePositionQuickPlan(id, updateQuickPlanRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteQuickPlan(id: Int) = try {
        val response = quickPlanApi.deleteQuickPlan(id)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getAllQuickPlans() = Pager(
        config = PagingConfig(20),
        pagingSourceFactory = { QuickPlanPagingSource(quickPlanApi) }
    ).flow
}