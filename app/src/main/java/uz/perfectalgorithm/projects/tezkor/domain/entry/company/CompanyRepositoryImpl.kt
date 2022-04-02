package uz.perfectalgorithm.projects.tezkor.domain.entry.company

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.CompanyApi
import java.io.File
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val api: CompanyApi
) : CompanyRepository {
//
//    @ExperimentalCoroutinesApi
//    override fun createCompany(
//        title: String,
//        image: File?,
//        direction: String,
//        username: String,
//        email: String
//    ): Flow<Result<Unit>> =
//        channelFlow {
//            try {
//                val response = if (image != null) {
//                    val imagePart = MultipartBody.Part.createFormData(
//                        "image", "companyLogo.jpg", RequestBody.create(
//                            "image/JPEG".toMediaTypeOrNull(),
//                            image.readBytes()
//                        )
//                    )
////                    val imagePart = MultipartBody.Part.createFormData(
////                        "image", "companyLogo.jpg", RequestBody.create(
////                            "image/JPEG".toMediaTypeOrNull(),
////                            image.readBytes()
////                        )
////                    )
//                    api.createCompany(
//                        title.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        direction.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        username.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        email.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        part = imagePart
//                    )
//                } else {
//
//                    api.createCompany(
//                        title.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        direction.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        username.toRequestBody("text/plain".toMediaTypeOrNull()),
//                        email.toRequestBody("text/plain".toMediaTypeOrNull())
//                    )
//                }
//                if (response.isSuccessful) response.body()?.let {
//                    send(Result.success(Unit))
//                } else
//                    send(
//                        Result.failure<Unit>(
//                            Exception(
//                                App.instance.resources.getString(R.string.error)
//                            )
//                        )
//                    )
//            } catch (e: Exception) {
//                send(Result.failure<Unit>(e))
//            }
//        }

    override suspend fun createCompanyNew(
        title: String,
        image: File?,
        direction: String,
        username: String,
        email: String
    ) = try {
        val response = if (image != null) {
            val imagePart = MultipartBody.Part.createFormData(
                "image", "companyLogo.jpg", RequestBody.create(
                    "image/JPEG".toMediaTypeOrNull(),
                    image.readBytes()
                )
            )
            api.createCompany(
                title.toRequestBody("text/plain".toMediaTypeOrNull()),
                direction.toRequestBody("text/plain".toMediaTypeOrNull()),
                username.toRequestBody("text/plain".toMediaTypeOrNull()),
                email.toRequestBody("text/plain".toMediaTypeOrNull()),
                part = imagePart
            )
        } else {

            api.createCompany(
                title.toRequestBody("text/plain".toMediaTypeOrNull()),
                direction.toRequestBody("text/plain".toMediaTypeOrNull()),
                username.toRequestBody("text/plain".toMediaTypeOrNull()),
                email.toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }

        if (response.isSuccessful) {
            DataWrapper.Success(Unit)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }


    override suspend fun getDirection() = try {
        val direction = api.getDirection()
        if (direction.isSuccessful) {
            DataWrapper.Success(direction.body()?: emptyList())
        } else {
            DataWrapper.Error(HttpException(direction))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getDirection2(id: Int) = try {
        val direction = api.getDirection2(id)
        if (direction.isSuccessful) {
            DataWrapper.Success(direction.body()?: emptyList())
        } else {
            DataWrapper.Error(HttpException(direction))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}


