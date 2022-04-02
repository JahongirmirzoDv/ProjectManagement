package uz.perfectalgorithm.projects.tezkor.domain.entry

import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.RegistrationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.TokenRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.VerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password.ResetPasswordVerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.AuthResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.CompaniesResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.RegistrationResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.VerificationResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.ResetPasswordResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.VerificationNewPasswordResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.save_company.UserDataWithCompanyResponse

interface AuthRepository {
    //    fun signIn(tokenRequest: TokenRequest): Flow<Result<AuthResponse.UserData>>
//    fun refreshToken(): Flow<Result<Unit>>
//    fun getCompanies(): Flow<Result<List<CompaniesResponse.CompanyData>>>
//    fun saveCompanyToUser(companyId: Int): Flow<Result<UserDataWithCompanyResponse.UserWData>>
//    fun registrationUser(registrationRequest: RegistrationRequest): Flow<Result<RegistrationResponse.Result>>
//    fun verificationUser(verificationRequest: VerificationRequest): Flow<Result<VerificationResponse.Result>>

    suspend fun signInNew(tokenRequest: TokenRequest): DataWrapper<AuthResponse.UserData>
    suspend fun registrationUserNew(registrationRequest: RegistrationRequest): DataWrapper<RegistrationResponse.Result>
    suspend fun verificationUserNew(verificationRequest: VerificationRequest): DataWrapper<VerificationResponse.Result>
    suspend fun getCompaniesNew(): DataWrapper<List<CompaniesResponse.CompanyData>>
    suspend fun saveCompanyToUserNew(companyId: Int): DataWrapper<UserDataWithCompanyResponse.UserWData>
    suspend fun refreshTokenNew(): DataWrapper<Unit>

    suspend fun resetPhoneNumberForNewPasswordVerification(resetPasswordRequest: RegistrationRequest): DataWrapper<ResetPasswordResponse.ResetPasswordNotifyResponse>
    suspend fun resetPassword(verificationRequest: ResetPasswordVerificationRequest): DataWrapper<VerificationNewPasswordResponse>
}