package uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.goal.UpdateGoalRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.*
import java.io.File

/**
 * Created by Jasurbek Kurganbaev on 28.06.2021 15:22
 **/
interface GoalMapRepository {

    fun createGoal(
        title: String,
        description: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        folder: Int,
        status: String,
        participants: List<Int>?,
        spectators: List<Int>?,
        functional_group: List<Int>?,
        canEditTime: Boolean
    ): Flow<Result<CreateGoalData>>

    fun getGoals(): Flow<Result<List<GoalData>>>

    suspend fun getGoalsNew(): DataWrapper<List<GoalData>>

    fun createGoalMap(title: String): Flow<Result<ItemGoalMapData>>

    fun getGoalMaps(): Flow<Result<List<ItemGoalMapData>>>

    suspend fun getGoalMapsNew(): DataWrapper<List<ItemGoalMapData>>

    fun getGoalMap(id: Int): Flow<Result<List<GoalsItem>>>

    suspend fun getGoalById(goalId: Int): DataWrapper<GoalDetails>

    suspend fun deleteGoal(goalId: Int): DataWrapper<Any>

    suspend fun updateGoal(
        goalId: Int,
        updateGoalRequest: UpdateGoalRequest
    ): DataWrapper<Any>
}