package uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.BuyPackageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.*

/**
 * Created by Jasurbek Kurganbaev on 8/22/2021 2:00 PM
 **/
interface PackageRepository {


    fun buyPackage(count: Int, month: Int): Flow<Result<BuyPackageResponse>>

    fun getPackage(): Flow<Result<GetPackageResponse>>

    fun getTransactionList(): Flow<Result<GetTransactionResponse>>

    fun getStaffList(): Flow<Result<GetStaffListResponse>>

    fun addStaff(count: Int): Flow<Result<AddStaffResponse>>

    fun addDate(month: Int): Flow<Result<AddDateResponse>>

    fun userActivate(userId: Int, activeStatus: Int): Flow<Result<UserActivateResponse>>

    fun getTariffPlans(): Flow<Result<GetTariffPlansListResponse>>

    fun getPurchasedPackageList(): Flow<Result<GetPackageListResponse>>

}