package uz.perfectalgorithm.projects.tezkor.domain.home.task.dating

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.DatingApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.CreateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.UpdateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingListItem
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject


class DatingRepositoryImpl @Inject constructor(
    private val datingApi: DatingApi
) : DatingRepository {

    override suspend fun createDating(request: CreateDatingRequest) = try {

        val filesBody = ArrayList<MultipartBody.Part>()
        request.files?.forEach { file ->
            filesBody.add(prepareImageFilePart("files", file))
        }

//        val membersBody = Array(request.members?.size ?: 0) {
//            request.members?.get(it).toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        }
        val remindersBody = Array(request.reminders?.size ?: 0) {
            request.reminders?.get(it).toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }
        val partnerIns = Array(request.partnerIn?.size?:0){
            request.partnerIn?.get(it).toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val response = datingApi.createDating(
            description = request.description.toRequestBody("text/plain".toMediaTypeOrNull()),
            address = request.address.toRequestBody("text/plain".toMediaTypeOrNull()),
            startTime = request.startTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            endTime = request.endTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            files = filesBody,
            repeat = request.repeat?.toRequestBody("text/plain".toMediaTypeOrNull()),
            importance = request.importance.toRequestBody("text/plain".toMediaTypeOrNull()),
//            reminder = request.reminder?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
            untilDate = request.untilDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
//            members = membersBody,
            partnerIn = partnerIns,
            partnerOut = request.partnerOut?.toRequestBody("text/plain".toMediaTypeOrNull()),
            repeatRule = request.repeatRule.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            repeatExceptions = listOf(),
            reminders = remindersBody,
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getDatingList(): Flow<Result<List<DatingListItem>>> = flow {
        try {
            val response = datingApi.getDatingList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else emit(
                Result.failure<List<DatingListItem>>(
                    Exception(
                        App.instance.resources.getString(R.string.error)
                    )
                )
            )
        } catch (e: Exception) {
            timberLog("DatingRepositoryImpl in function refreshToken error = $e")
            emit(Result.failure<List<DatingListItem>>(e))
        }
    }

    override suspend fun getDatingListNew() = try {
        val response = datingApi.getDatingList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getDatingById(datingId: Int) = try {
        val response = datingApi.getDatingById(datingId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteDating(datingId: Int) = try {
        val response = datingApi.deleteDating(datingId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateDating(
        datingId: Int,
        updateDatingRequest: UpdateDatingRequest
    ) = try {

        val filesBody = ArrayList<MultipartBody.Part>()
        updateDatingRequest.newFiles.forEach { file ->
            filesBody.add(prepareImageFilePart("files", file))
        }

//        val participantsBody = Array(updateDatingRequest.participantsId.size) {
//            updateDatingRequest.participantsId[it].toString()
//                .toRequestBody("text/plain".toMediaTypeOrNull())
//        }

        val deletedFilesBody = Array(updateDatingRequest.deletedFiles.size) {
            updateDatingRequest.deletedFiles[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val newReminders = Array(updateDatingRequest.newReminders.size) {
            updateDatingRequest.newReminders[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedReminders = Array(updateDatingRequest.deletedReminders.size) {
            updateDatingRequest.deletedReminders[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }
        val partnerIns = Array(updateDatingRequest.partnerIn?.size?:0){
            updateDatingRequest.partnerIn?.get(it).toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val response = datingApi.updateDating(
            datingId = datingId,
//            title = updateDatingRequest.title.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = updateDatingRequest.description.toRequestBody("text/plain".toMediaTypeOrNull()),
            address = updateDatingRequest.address.toRequestBody("text/plain".toMediaTypeOrNull()),
            partnerIn = partnerIns,
            partnerOut = (updateDatingRequest.partnerOut
                ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
            start_time = updateDatingRequest.startTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            end_time = updateDatingRequest.endTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            files = filesBody,
            importance = updateDatingRequest.importance.toRequestBody("text/plain".toMediaTypeOrNull()),
//            participants = participantsBody,
            repeat = updateDatingRequest.repeat?.toRequestBody("text/plain".toMediaTypeOrNull()),
            deletedFiles = deletedFilesBody,
//            reminder = updateDatingRequest.reminder?.toString()
//                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            untilDate = updateDatingRequest.reminderDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
            deletedReminders = deletedReminders,
            reminders = newReminders,
            repeatRule = updateDatingRequest.repeatRule?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
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