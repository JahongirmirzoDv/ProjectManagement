package uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan


interface QuickPlanRepository {
    /**
     * ushbu repo dagi barcha funksiyalar QuickPlanApi dagilar
     * bilan bir xil vazifa bajaradi
     */

    suspend fun createQuickPlan(createQuickPlanRequest: CreateQuickPlanRequest): DataWrapper<QuickPlan>

    suspend fun getQuickPlanById(id: Int): DataWrapper<QuickPlan>

    suspend fun getQuickPlansOfDay(day: String): DataWrapper<List<QuickPlan>>

    suspend fun getQuickPlansOfWeek(startDay: String): DataWrapper<List<QuickPlan>>

    suspend fun getQuickPlansOfMonth(month: String): DataWrapper<List<QuickPlan>>

    suspend fun getQuickPlansOfYear(year: String): DataWrapper<List<QuickPlan>>

    suspend fun updateQuickPlan(
        id: Int,
        updateQuickPlanRequest: UpdateQuickPlanRequest
    ): DataWrapper<QuickPlan>

    suspend fun updateToTopQuickPlan(
        id: Int,
    ): DataWrapper<QuickPlan>

    suspend fun updatePositionQuickPlan(
        id: Int,
        upper: Int?,
        lower: Int?
    ):DataWrapper<QuickPlan>

    suspend fun deleteQuickPlan(id: Int): DataWrapper<Any>

    fun getAllQuickPlans(): Flow<PagingData<QuickPlan>>
}