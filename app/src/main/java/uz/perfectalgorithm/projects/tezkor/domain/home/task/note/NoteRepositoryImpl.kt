package uz.perfectalgorithm.projects.tezkor.domain.home.task.note

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.NoteApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteDeleteOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NotePostRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteEditOnesData
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val noteApi: NoteApi) : NoteRepository {
    override fun postNote(notePostRequest: NotePostRequest): Flow<Result<NoteData>> = flow {

        try {
            val response = noteApi.createNote(notePostRequest)
            if (response.isSuccessful) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<NoteData>(HttpException(response))
            )
        } catch (e: Exception) {
            emit(Result.failure<NoteData>(e))
        }

    }

    override fun updateNote(id: Int, noteEditRequest: NoteEditRequest): Flow<Result<NoteData>> =
        flow {

            try {
                val response = noteApi.updateNote(id, noteEditRequest)
                if (response.isSuccessful) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<NoteData>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                emit(Result.failure<NoteData>(e))
            }

        }

    override fun getNote(id: Int): Flow<Result<NoteData>> = flow {
        try {
            val response = noteApi.getNote(id)
            if (response.isSuccessful) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<NoteData>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            emit(Result.failure<NoteData>(e))
        }
    }

    override fun deleteNote(id: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = noteApi.deleteNoteAll(id)
            if (response.code() == 204) {
                emit(Result.success(true))
            } else emit(
                Result.failure<Boolean>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            emit(Result.failure<Boolean>(e))
        }
    }

    override fun deleteNoteOnes(deleteRequest: NoteDeleteOnesRequest): Flow<Result<Boolean>> =
        flow {
            try {
                val response = noteApi.deleteNoteOnes(deleteRequest)
                if (response.isSuccessful) {
                    emit(Result.success(true))
                } else emit(
                    Result.failure<Boolean>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                emit(Result.failure<Boolean>(e))
            }
        }

    override fun editNoteOnes(
        noteEditOnesRequest: NoteEditOnesRequest
    ): Flow<Result<NoteEditOnesData>> =
        flow {
            try {
                val response = noteApi.editNoteOnes(noteEditOnesRequest)
                if (response.isSuccessful) response.body()?.let {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<NoteEditOnesData>(
                            HttpException(response)
                        )
                    )
                }
            } catch (e: Exception) {
                emit(Result.failure<NoteEditOnesData>(e))
            }
        }
}