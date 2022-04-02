package uz.perfectalgorithm.projects.tezkor.domain.home.task.note

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NotePostRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteDeleteOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteEditOnesData

interface NoteRepository {

    fun postNote(notePostRequest: NotePostRequest): Flow<Result<NoteData>>
    fun updateNote(id: Int, notePostRequest: NoteEditRequest): Flow<Result<NoteData>>
    fun getNote(id: Int): Flow<Result<NoteData>>
    fun deleteNote(id: Int): Flow<Result<Boolean>>
    fun deleteNoteOnes(deleteRequest: NoteDeleteOnesRequest): Flow<Result<Boolean>>
    fun editNoteOnes(
        noteEditOnesRequest: NoteEditOnesRequest
    ): Flow<Result<NoteEditOnesData>>

}