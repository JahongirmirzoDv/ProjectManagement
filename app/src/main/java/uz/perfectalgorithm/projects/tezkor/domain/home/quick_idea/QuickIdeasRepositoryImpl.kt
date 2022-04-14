package uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.QuickIdeasApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.*
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import java.io.File
import javax.inject.Inject


class QuickIdeasRepositoryImpl @Inject constructor(
    private val quickIdeasApi: QuickIdeasApi
) : QuickIdeasRepository {
    override fun getAllQuickIdeasBoxes(): Flow<Result<GetQuickIdeasBoxesResponse>> = flow {
        try {
            val response = quickIdeasApi.getAllIdeaBoxes()
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<GetQuickIdeasBoxesResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<GetQuickIdeasBoxesResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<GetQuickIdeasBoxesResponse>(e))
        }
    }


    override fun createQuickIdeasBox(title: String): Flow<Result<CreateQuickIdeaBoxResponse>> =
        flow {
            try {
                val response = quickIdeasApi.createIdeasBox(title)
                if (response.code() == 201) response.body()?.let {
                    if (it.data != null) {
                        emit(Result.success(it))
                    } else {
                        emit(
                            Result.failure<CreateQuickIdeaBoxResponse>(
                                Exception(
                                    App.instance.resources.getString(
                                        R.string.error
                                    )
                                )
                            )
                        )
                    }
                } else {
                    emit(
                        Result.failure<CreateQuickIdeaBoxResponse>(
                            HttpException(response)
                        )
                    )
                }

            } catch (e: Exception) {
                emit(Result.failure<CreateQuickIdeaBoxResponse>(e))
            }
        }

    override fun updateQuickIdeaBox(
        id: Int,
        data: UpdateQuickIdeasBoxData
    ): Flow<Result<CreateQuickIdeaBoxResponse>> = flow {
        try {
            val response = quickIdeasApi.updateIdeaBox(id, data)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<CreateQuickIdeaBoxResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<CreateQuickIdeaBoxResponse>(
                        HttpException(response)
                    )
                )
            }

        } catch (e: Exception) {
            emit(Result.failure<CreateQuickIdeaBoxResponse>(e))
        }
    }

    override fun deleteQuickIdeaBox(id: Int): Flow<Result<DeleteQuickIdeaBoxResponse>> = flow {
        try {
            val response = quickIdeasApi.deleteIdeaBox(id)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<DeleteQuickIdeaBoxResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<DeleteQuickIdeaBoxResponse>(
                        HttpException(response)
                    )
                )
            }

        } catch (e: Exception) {
            emit(Result.failure<DeleteQuickIdeaBoxResponse>(e))
        }
    }

    override fun createQuickIdea(
        title: String,
        description: String?,
        files: List<File>?,
        folder: Int?,
    ): Flow<Result<QuickIdeaResponse>> = flow {
        try {

            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            if (files != null) {
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }
            val response = quickIdeasApi.createQuickIdea(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                folder = folder.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
            )
            if (response.code() == 201) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<QuickIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<QuickIdeaResponse>(
                        HttpException(response)
                    )
                )
            }

        } catch (e: Exception) {
            emit(Result.failure<QuickIdeaResponse>(e))
        }
    }

    override fun createQuickIdeaWithinBox(
        title: String,
        description: String?,
        files: List<File>?,
        to_idea_box: Boolean
    ): Flow<Result<QuickIdeaResponse>> = flow {
        try {

            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            if (files != null) {
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }
            val response = quickIdeasApi.createQuickIdeaWithin(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                to_idea_box = to_idea_box.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
            )
            if (response.code() == 201) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<QuickIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<QuickIdeaResponse>(
                        HttpException(response)
                    )
                )
            }

        } catch (e: Exception) {
            emit(Result.failure<QuickIdeaResponse>(e))
        }
    }

    override fun getQuickIdea(id: Int): Flow<Result<GetQuickIdeaResponse>> = flow {
        try {
            val response = quickIdeasApi.getQuickIdea(id)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<GetQuickIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<GetQuickIdeaResponse>(
                        HttpException(response)
                    )
                )
            }

        } catch (e: Exception) {
            emit(Result.failure<GetQuickIdeaResponse>(e))
        }
    }

    override fun updateQuickIdea(
        id: Int,
        title: String,
        description: String,
        files: List<File>?,
        folder: Int,
        to_idea_box: Boolean
    ): Flow<Result<UpdateQuickIdeaResponse>> = flow {

        try {

            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            if (files != null) {
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }
            val response = quickIdeasApi.updateQuickIdea(
                id = id,
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                folder = folder.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
                to_idea_box = to_idea_box.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            )
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<UpdateQuickIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<UpdateQuickIdeaResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<UpdateQuickIdeaResponse>(e))
        }
    }


    override fun deleteQuickIdea(id: Int): Flow<Result<DeleteQuickIdeaResponse>> = flow {
        try {
            val response = quickIdeasApi.deleteIdea(id)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<DeleteQuickIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<DeleteQuickIdeaResponse>(
                        HttpException(response)
                    )
                )
            }

        } catch (e: Exception) {
            emit(Result.failure<DeleteQuickIdeaResponse>(e))
        }
    }

    override fun getQuickIdeaByRating(id: Int): Flow<Result<List<RatingIdeaData>>> = flow {
        try {
            val response = quickIdeasApi.getIdeasByRating(id)
            if (response.code() == 200) response.body()?.let {
                if (it.ideas != null) {
                    emit(Result.success(it.ideas))
                }
            } else {
                emit(
                    Result.failure<List<RatingIdeaData>>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<List<RatingIdeaData>>(e))
        }
    }

    /*   override fun getQuickIdeaByFinished(id: Int): Flow<Result<List<FinishedIdeaItem>>> = flow {
           try {
               val response = quickIdeasApi.getIdeasByFinished(id)
               if (response.code() == 200) response.body()?.let {
                   if (it.ideas != null) {
                       emit(Result.success(it.ideas))
                   }
               } else {
                   emit(
                       Result.failure<List<FinishedIdeaItem>>(
                           Exception(
                               App.instance.resources.getString(
                                   R.string.error
                               )
                           )
                       )
                   )
               }
           } catch (e: Exception) {
               emit(Result.failure<List<FinishedIdeaItem>>(e))
           }
       }*/

    override fun getGeneralQuickIdeas(): Flow<Result<List<RatingIdeaData>>> = flow {
        try {
            val response = quickIdeasApi.getGeneralIdeas()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<List<RatingIdeaData>>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<List<RatingIdeaData>>(e))
        }
    }

    override fun getGeneralQuickIdeaByFinished(): Flow<Result<List<FinishedIdeaItem>>> = flow {
        try {
            val response = quickIdeasApi.getGeneralFinishedIdeas()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<List<FinishedIdeaItem>>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<List<FinishedIdeaItem>>(e))
        }
    }
}