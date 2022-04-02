package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteDeleteOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NotePostRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteEditOnesResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NotePostResponse

interface NoteApi {

    @POST("/api/v1/notes/note/")
    suspend fun createNote(@Body note: NotePostRequest): Response<NotePostResponse>

    @PUT("/api/v1/notes/note/{id}/")
    suspend fun updateNote(
        @Path("id") id: Int,
        @Body note: NoteEditRequest
    ): Response<NotePostResponse>

    @GET("/api/v1/notes/note-retrieve/{id}/")
    suspend fun getNote(@Path("id") id: Int): Response<NotePostResponse>

    @DELETE("/api/v1/notes/note/{id}/")
    suspend fun deleteNoteAll(@Path("id") id: Int): Response<Any>


    @POST("/api/v1/notes/exception-create/")
    suspend fun deleteNoteOnes(@Body deleteRequest: NoteDeleteOnesRequest): Response<Any>


    @POST("/api/v1/notes/exception-edit/")
    suspend fun editNoteOnes(
        @Body noteEditOnesRequest: NoteEditOnesRequest
    ): Response<NoteEditOnesResponse>

}