package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password.ResetPasswordVerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.RefreshTokenResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.AuthResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.CompaniesResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.RegistrationResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.VerificationResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.ResetPasswordResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.VerificationNewPasswordResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.save_company.UserDataWithCompanyResponse



interface AuthApi {

    @POST("/api/v1/user/token/obtain")
    suspend fun getToken(
        @Body tokenRequest: TokenRequest
    ): Response<AuthResponse.TokenResponse>

    @POST("/api/v1/user/token/refresh")
    suspend fun refreshToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @GET("/api/v1/company/companies")
    suspend fun getCompanies(): Response<CompaniesResponse.Result>

    @POST("/api/v1/user/change-company")
    suspend fun changeCompany(
        @Body changeCompanyRequest: ChangeCompanyRequest
    ): Response<UserDataWithCompanyResponse.Result>

    @POST("/api/v1/user/notify")
    suspend fun registrationUser(
        @Body registrationRequest: RegistrationRequest
    ): Response<RegistrationResponse.Result>

    @POST("/api/v1/user/register/")
    suspend fun verificationUser(
        @Body verificationRequest: VerificationRequest
    ): Response<VerificationResponse.Result>

    @POST("/api/v1/user/reset-notify/")
    suspend fun sendPhoneAndPassword(
        @Body resetPasswordRequest: RegistrationRequest
    ): Response<ResetPasswordResponse.ResetPasswordNotifyResponse>

    @POST("/api/v1/user/reset-password/")
    suspend fun verificationNewPassword(
        @Body verificationRequest: ResetPasswordVerificationRequest
    ): Response<VerificationNewPasswordResponse>


}