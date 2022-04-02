package uz.perfectalgorithm.projects.tezkor.domain.entry

import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.AuthApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password.ResetPasswordVerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.VerificationNewPasswordResponse
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val storage: LocalStorage
) : AuthRepository {

//
//    override fun signIn(tokenRequest: TokenRequest): Flow<Result<AuthResponse.UserData>> =
//        flow {
//            try {
//                tokenRequest.firebaseToken = storage.firebaseToken
//                val response = api.getToken(tokenRequest)
//                if (response.code() == 200) response.body()?.let {
//                    storage.token = it.access
//                    storage.refreshToken = it.refresh
//                    storage.role = "${it.user.role}"
//                    storage.logged = true
//                    storage.userFirstName = it.user.firstName.toString()
//                    storage.userLastName = it.user.lastName.toString()
//                    it.user.id?.let { userId_ ->
//                        storage.userId = userId_
//                        storage
//                    }
//
//                    emit(Result.success(it.user))
//                } else emit(
//                    Result.failure<AuthResponse.UserData>(
//                        Exception(
//                            App.instance.resources.getString(R.string.error)
//                        )
//                    )
//                )
//            } catch (e: Exception) {
//                timberLog("AuthRepositoryImpl in function refreshToken error = $e")
//                emit(Result.failure<AuthResponse.UserData>(e))
//            }
//        }
//
//    override fun registrationUser(registrationRequest: RegistrationRequest): Flow<Result<RegistrationResponse.Result>> =
//        flow {
//            try {
//                val response = api.registrationUser(registrationRequest)
//                if (response.isSuccessful) response.body()?.let {
//                    emit(Result.success(it))
//                } else if (response.code() == 400) {
//                    val gson = Gson()
//                    val errorModel =
//                        gson.fromJson(
//                            response.errorBody()!!.stringSuspending(),
//                            RegistrationResponse.ErrorModel::class.java
//                        )
//                    if (errorModel != null && errorModel.errorCode == 400) {
//                        emit(
//                            Result.failure<RegistrationResponse.Result>(
//                                Exception(
//                                    App.instance.resources.getString(R.string.error_phone_number_exist)
//                                )
//                            )
//                        )
//                    } else {
//                        emit(
//                            Result.failure<RegistrationResponse.Result>(
//                                Exception(
//                                    App.instance.resources.getString(R.string.error)
//                                )
//                            )
//                        )
//                    }
//                } else {
//                    emit(
//                        Result.failure<RegistrationResponse.Result>(
//                            Exception(
//                                App.instance.resources.getString(R.string.error)
//                            )
//                        )
//                    )
//                }
//
//            } catch (e: Exception) {
//                emit(Result.failure<RegistrationResponse.Result>(e))
//            }
//        }
//
//    @Suppress("BlockingMethodInNonBlockingContext")
//    suspend fun ResponseBody.stringSuspending() =
//        withContext(Dispatchers.IO) { string() }
//
//    override fun verificationUser(verificationRequest: VerificationRequest): Flow<Result<VerificationResponse.Result>> =
//        flow {
//            try {
//                val response = api.verificationUser(verificationRequest)
//                when {
//                    response.isSuccessful -> response.body()?.let {
//                        emit(Result.success(it))
//                    }
//                    response.code() == 400 -> {
//                        emit(
//                            Result.failure<VerificationResponse.Result>(
//                                Exception(
//                                    App.instance.resources.getString(R.string.error_code)
//                                )
//                            )
//                        )
//                    }
//                    else -> {
//                        emit(
//                            Result.failure<VerificationResponse.Result>(
//                                Exception(
//                                    App.instance.resources.getString(R.string.error)
//                                )
//                            )
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                emit(Result.failure<VerificationResponse.Result>(e))
//            }
//        }
//
//    override fun refreshToken(): Flow<Result<Unit>> =
//        flow {
//            try {
//                val response = api.refreshToken(RefreshTokenRequest(storage.refreshToken))
//                if (response.code() == 200) response.body()?.let {
//                    storage.token = it.access
//                    storage.logged = true
//                    timberLog("signIn refreshToken = ${storage.refreshToken}")
//                    emit(Result.success(Unit))
//                } else emit(
//                    Result.failure<Unit>(
//                        Exception(
//                            App.instance.resources.getString(R.string.error)
//                        )
//                    )
//                )
//            } catch (e: Exception) {
//                timberLog("AuthRepositoryImpl in function refreshToken error = $e")
//                emit(Result.failure<Unit>(e))
//            }
//        }
//
//    override fun getCompanies(): Flow<Result<List<CompaniesResponse.CompanyData>>> =
//        flow {
//            try {
//                val response = api.getCompanies()
//                if (response.code() == 200) response.body()?.let {
//                    emit(Result.success(it.data))
//                } else emit(
//                    Result.failure<List<CompaniesResponse.CompanyData>>(
//                        Exception(
//                            App.instance.resources.getString(R.string.error)
//                        )
//                    )
//                )
//            } catch (e: Exception) {
//                timberLog("AuthRepositoryImpl in function refreshToken error = $e")
//                emit(Result.failure<List<CompaniesResponse.CompanyData>>(e))
//            }
//        }
//
//    override fun saveCompanyToUser(companyId: Int): Flow<Result<UserDataWithCompanyResponse.UserWData>> =
//        flow {
//            try {
//                val changeCompanyRequest = ChangeCompanyRequest(companyId)
//                val response = api.changeCompany(changeCompanyRequest)
//                if (response.isSuccessful) response.body()?.let {
//                    emit(Result.success(it.data))
//                } else
//                    emit(
//                        Result.failure<UserDataWithCompanyResponse.UserWData>(
//                            Exception(
//                                App.instance.resources.getString(R.string.error_code)
//                            )
//                        )
//                    )
//            } catch (e: Exception) {
//                timberLog("Change company error = $e")
//                emit(Result.failure<UserDataWithCompanyResponse.UserWData>(e))
//            }
//        }

    override suspend fun signInNew(tokenRequest: TokenRequest) = try {
        tokenRequest.firebaseToken = storage.firebaseToken
        val response = api.getToken(tokenRequest)
        if (response.isSuccessful) {
            response.body()?.let {
                storage.token = it.access
                storage.refreshToken = it.refresh
                storage.role = "${it.user.role}"
                storage.logged = true
                storage.userFirstName = it.user.firstName.toString()
                storage.userLastName = it.user.lastName.toString()
                storage.userImage = it.user.image.toString()
                it.user.id?.let { userId_ ->
                    storage.userId = userId_
                    storage
                }
            }
            DataWrapper.Success(response.body()?.user!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun registrationUserNew(registrationRequest: RegistrationRequest) = try {
        val response = api.registrationUser(registrationRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun verificationUserNew(verificationRequest: VerificationRequest) = try {
        val response = api.verificationUser(verificationRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getCompaniesNew() = try {
        val response = api.getCompanies()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun saveCompanyToUserNew(companyId: Int) = try {
        val response = api.changeCompany(ChangeCompanyRequest(companyId))
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun refreshTokenNew() = try {
        val response = api.refreshToken(RefreshTokenRequest(storage.refreshToken))
        if (response.isSuccessful) {
            response.body()?.let {
                storage.token = it.access
                storage.logged = true
            }
            DataWrapper.Success(Unit)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun resetPhoneNumberForNewPasswordVerification(resetPasswordRequest: RegistrationRequest) =
        try {
            val response = api.sendPhoneAndPassword(resetPasswordRequest)
            if (response.isSuccessful) {
                DataWrapper.Success(response.body()!!)
            } else {
                DataWrapper.Error(HttpException(response))
            }
        } catch (e: Exception) {
            DataWrapper.Error(e)
        }

    override suspend fun resetPassword(verificationRequest: ResetPasswordVerificationRequest): DataWrapper<VerificationNewPasswordResponse> =
        try {
            val response = api.verificationNewPassword(verificationRequest)
            if (response.isSuccessful) {
                DataWrapper.Success(response.body()!!)
            } else {
                DataWrapper.Error(HttpException(response))
            }
        } catch (e: Exception) {
            DataWrapper.Error(e)
        }
}


