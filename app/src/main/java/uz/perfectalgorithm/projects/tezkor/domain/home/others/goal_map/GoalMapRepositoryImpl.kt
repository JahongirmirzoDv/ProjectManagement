package uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.GoalsApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.goal.UpdateGoalRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.CreateGoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalsItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import java.io.File
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 28.06.2021 15:22
 **/
class GoalMapRepositoryImpl @Inject constructor(
    private val goalApi: GoalsApi

) : GoalMapRepository {
    override fun getGoalMaps(): Flow<Result<List<ItemGoalMapData>>> = flow {
        try {
            val response = goalApi.getGoalMaps()
            if (response.code() == 200) {
                response.body()?.let {
                    if (it.data != null) {
                        emit(Result.success(it.data))
                    } else {
                        emit(
                            Result.failure<List<ItemGoalMapData>>(
                                Exception(
                                    App.instance.resources.getString(
                                        R.string.error
                                    )
                                )
                            )
                        )
                    }
                }
            } else {
                emit(Result.failure<List<ItemGoalMapData>>(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<ItemGoalMapData>>(e))
        }
    }

    override suspend fun getGoalMapsNew() = try {
        val response = goalApi.getGoalMaps()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getGoalMap(id: Int): Flow<Result<List<GoalsItem>>> = flow {
        try {
            val response = goalApi.getGoalMap(id)
            if (response.code() == 200) {
                response.body()?.let {
                    if (it.data != null) {
                        emit(Result.success(it.data.goals))
                    } else {
                        emit(
                            Result.failure<List<GoalsItem>>(
                                Exception(
                                    App.instance.resources.getString(
                                        R.string.error
                                    )
                                )
                            )
                        )
                    }
                }
            } else {
                emit(Result.failure<List<GoalsItem>>(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<GoalsItem>>(e))
        }
    }

    override fun createGoal(
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
    ): Flow<Result<CreateGoalData>> = flow {
        try {
            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            Log.d("FILE", files?.size.toString())
            if (files != null) {
                Log.d("FILE", "IF CONDITION")
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }

            val participantsBody = participants?.let { it ->
                Array(it.size) {
                    participants[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }


            val spectatorBody = spectators?.let {
                Array(it.size) {
                    spectators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val functionalBody = functional_group?.let {
                Array(it.size) {
                    functional_group[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val response = goalApi.createGoal(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                performer = performer.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                leader = leader.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                start_time = start_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                end_time = end_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
                folder = folder.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                status = status.toRequestBody("text/plain".toMediaTypeOrNull()),
                participants = participantsBody,
                spectators = spectatorBody,
                functional_group = functionalBody,
                canEditTime = canEditTime.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            )
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else {
                Log.d("FILE", response.code().toString())
                emit(Result.failure<CreateGoalData>(HttpException(response)))
            }

        } catch (e: Exception) {
            emit(Result.failure<CreateGoalData>(e))
        }
    }

    override fun getGoals(): Flow<Result<List<GoalData>>> = flow {
        try {
            val response = goalApi.getGoals()
            if (response.code() == 200) {
                response.body()?.let {
                    if (it.data != null) {
                        emit(Result.success(it.data))
                    } else {
                        emit(
                            Result.failure<List<GoalData>>(
                                Exception(
                                    App.instance.resources.getString(
                                        R.string.error
                                    )
                                )
                            )
                        )
                    }
                }
            } else {
                emit(Result.failure<List<GoalData>>(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<GoalData>>(e))
        }
    }

    override suspend fun getGoalsNew() = try {
        val response = goalApi.getGoals()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    private fun prepareImageFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = RequestBody.create(
            App.instance.contentResolver.getType(Uri.fromFile(file))?.toMediaTypeOrNull(), file
        )

        return MultipartBody.Part.createFormData(
            partName, file.name, requestFile
        )
    }

    override fun createGoalMap(title: String): Flow<Result<ItemGoalMapData>> = flow {
        try {
            val response = goalApi.createGoalMap(title)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<ItemGoalMapData>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else emit(
                Result.failure<ItemGoalMapData>(HttpException(response))
            )
        } catch (e: Exception) {
            emit(Result.failure<ItemGoalMapData>(e))
        }

    }

    override suspend fun getGoalById(goalId: Int) = try {
        val response = goalApi.getGoalById(goalId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteGoal(goalId: Int) = try {
        val response = goalApi.deleteGoal(goalId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateGoal(
        goalId: Int,
        updateGoalRequest: UpdateGoalRequest
    ) = try {

        val filesBody = ArrayList<MultipartBody.Part>()
        updateGoalRequest.newFiles.forEach { file ->
            filesBody.add(
                uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart(
                    "files",
                    file
                )
            )
        }

        val participantsBody = Array(updateGoalRequest.participantsId.size) {
            updateGoalRequest.participantsId[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val spectatorBody = Array(updateGoalRequest.observersId.size) {
            updateGoalRequest.observersId[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val functionalBody = Array(updateGoalRequest.functionalGroupId.size) {
            updateGoalRequest.functionalGroupId[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedFilesBody = Array(updateGoalRequest.deletedFiles.size) {
            updateGoalRequest.deletedFiles[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val response = goalApi.updateGoal(
            goalId = goalId,
            title = updateGoalRequest.title.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = updateGoalRequest.description.toRequestBody("text/plain".toMediaTypeOrNull()),
            performer = updateGoalRequest.performerId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            leader = updateGoalRequest.leaderId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            start_time = updateGoalRequest.startTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            end_time = updateGoalRequest.endTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            files = filesBody,
            folder = updateGoalRequest.folderId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            status = updateGoalRequest.status.toRequestBody("text/plain".toMediaTypeOrNull()),
            participants = participantsBody,
            spectators = spectatorBody,
            functional_group = functionalBody,
            deletedFiles = deletedFilesBody,
            canEditTime = updateGoalRequest.canEditTime.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}