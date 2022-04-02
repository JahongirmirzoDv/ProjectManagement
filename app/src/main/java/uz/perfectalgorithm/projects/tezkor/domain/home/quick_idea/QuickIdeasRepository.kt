package uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.*
import java.io.File

/**
 * Created by Jasurbek Kurganbaev on 20.07.2021 15:19
 **/
interface QuickIdeasRepository {

    fun getAllQuickIdeasBoxes(): Flow<Result<GetQuickIdeasBoxesResponse>>

    fun createQuickIdeasBox(title: String): Flow<Result<CreateQuickIdeaBoxResponse>>

    fun updateQuickIdeaBox(
        id: Int,
        data: UpdateQuickIdeasBoxData
    ): Flow<Result<CreateQuickIdeaBoxResponse>>

    fun deleteQuickIdeaBox(id: Int): Flow<Result<DeleteQuickIdeaBoxResponse>>

    fun createQuickIdea(
        title: String,
        description: String?,
        files: List<File>?,
        folder: Int?,
    ): Flow<Result<QuickIdeaResponse>>


    fun createQuickIdeaWithinBox(
        title: String,
        description: String?,
        files: List<File>?,
        to_idea_box: Boolean
    ): Flow<Result<QuickIdeaResponse>>

    fun getQuickIdea(id: Int): Flow<Result<GetQuickIdeaResponse>>

    fun updateQuickIdea(
        id: Int,/*, data: UpdateQuickIdeaData*/
        title: String,
        description: String,
        files: List<File>?,
        folder: Int,
        to_idea_box: Boolean

    ): Flow<Result<UpdateQuickIdeaResponse>>

    fun deleteQuickIdea(id: Int): Flow<Result<DeleteQuickIdeaResponse>>

    fun getQuickIdeaByRating(id: Int): Flow<Result<List<RatingIdeaData>>>

//    fun getQuickIdeaByFinished(id: Int): Flow<Result<List<FinishedIdeaItem>>>


    fun getGeneralQuickIdeas(): Flow<Result<List<RatingIdeaData>>>

    fun getGeneralQuickIdeaByFinished(): Flow<Result<List<FinishedIdeaItem>>>


}