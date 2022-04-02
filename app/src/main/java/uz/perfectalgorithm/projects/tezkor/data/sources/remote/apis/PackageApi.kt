package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.BuyPackageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.*

/**
 * Created by Jasurbek Kurganbaev on 8/22/2021 2:01 PM
 **/
interface PackageApi {

    @Multipart
    @POST("/api/v1/company/buy-package/")
    suspend fun buyPackage(
        @Part("count") count: Int,
        @Part("month") month: Int
    ): Response<BuyPackageResponse>

    @GET("/api/v1/company/my-package/")
    suspend fun getPackage(): Response<GetPackageResponse>

    @GET("/api/v1/paycom/transaction-list/")
    suspend fun getTransactionList(): Response<GetTransactionResponse>

    @GET("/api/v1/user/user-list-activity/")
    suspend fun getStaffList(): Response<GetStaffListResponse>

    @Multipart
    @POST("/api/v1/company/add-staff-package/")
    suspend fun addStaff(
        @Part("count") count: Int
    ): Response<AddStaffResponse>

    @Multipart
    @POST("/api/v1/company/add-date-package/")
    suspend fun addDate(
        @Part("month") month: Int
    ): Response<AddDateResponse>

    //    @Multipart
    @POST("/api/v1/user/user-activate/")
    suspend fun userActivate(
        @Body user_data: ActiveDataBody
        /*@Part("user_id") user_id: String,
        @Part("active_status") active_status: String*/
    ): Response<UserActivateResponse>

    @GET("/api/v1/company/price-list/")
    suspend fun getTariffPlans(): Response<GetTariffPlansListResponse>


    @GET("/api/v1/company/package-list/")
    suspend fun getPurchasedPackageList(): Response<GetPackageListResponse>

}