package uz.perfectalgorithm.projects.tezkor.data.sources.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.QuickPlanApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.utils.DEFAULT_PAGE_INDEX

/**
 *Created by farrukh_kh on 8/21/21 4:43 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.source
 **/
class QuickPlanPagingSource(private val quickPlanApi: QuickPlanApi) :
    PagingSource<Int, QuickPlan>() {

    override fun getRefreshKey(state: PagingState<Int, QuickPlan>) =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuickPlan> {
        val page = params.key ?: DEFAULT_PAGE_INDEX

        return try {
            val response = quickPlanApi.getAllQuickPlans(page)
            if (response.isSuccessful) {
                LoadResult.Page(
                    response.body()?.quickPlans ?: emptyList(),
                    prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                    nextKey = if (response.body()?.next == null) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}