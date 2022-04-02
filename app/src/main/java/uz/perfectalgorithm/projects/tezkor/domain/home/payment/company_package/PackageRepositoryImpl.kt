package uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.PackageApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.BuyPackageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.*
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 8/22/2021 2:01 PM
 **/
class PackageRepositoryImpl @Inject constructor(
    private val packageApi: PackageApi
) : PackageRepository {


    override fun buyPackage(count: Int, month: Int): Flow<Result<BuyPackageResponse>> = flow {
        try {
            val response = packageApi.buyPackage(count, month)
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<BuyPackageResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get Package error = $e")
            emit(Result.failure<BuyPackageResponse>(e))
        }
    }


    override fun getPackage(): Flow<Result<GetPackageResponse>> = flow {
        try {
            val response = packageApi.getPackage()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<GetPackageResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get Package error = $e")
            emit(Result.failure<GetPackageResponse>(e))
        }
    }

    override fun getTransactionList(): Flow<Result<GetTransactionResponse>> = flow {
        try {
            val response = packageApi.getTransactionList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<GetTransactionResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get TransactionList error = $e")
            emit(Result.failure<GetTransactionResponse>(e))
        }

    }

    override fun getStaffList(): Flow<Result<GetStaffListResponse>> = flow {
        try {
            val response = packageApi.getStaffList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<GetStaffListResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get StaffList error = $e")
            emit(Result.failure<GetStaffListResponse>(e))
        }
    }

    override fun addStaff(count: Int): Flow<Result<AddStaffResponse>> = flow {
        try {
            val response = packageApi.addStaff(count)
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<AddStaffResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get StaffList error = $e")
            emit(Result.failure<AddStaffResponse>(e))
        }
    }

    override fun addDate(month: Int): Flow<Result<AddDateResponse>> = flow {
        try {
            val response = packageApi.addDate(month)
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<AddDateResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get StaffList error = $e")
            emit(Result.failure<AddDateResponse>(e))
        }
    }

    override fun userActivate(
        userId: Int,
        activeStatus: Int
    ): Flow<Result<UserActivateResponse>> =
        flow {
            try {
                val response = packageApi.userActivate(/*userId, activeStatus*/ActiveDataBody(
                    userId,
                    activeStatus
                )
                )
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<UserActivateResponse>(
                            HttpException(response)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Get StaffList error = $e")
                emit(Result.failure<UserActivateResponse>(e))
            }
        }

    override fun getTariffPlans(): Flow<Result<GetTariffPlansListResponse>> = flow {
        try {
            val response = packageApi.getTariffPlans()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<GetTariffPlansListResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get TariffList error = $e")
            emit(Result.failure<GetTariffPlansListResponse>(e))
        }
    }

    override fun getPurchasedPackageList(): Flow<Result<GetPackageListResponse>> = flow {
        try {
            val response = packageApi.getPurchasedPackageList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<GetPackageListResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get TransactionList error = $e")
            emit(Result.failure<GetPackageListResponse>(e))
        }
    }
}