package uz.perfectalgorithm.projects.tezkor.domain.entry.company

import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.DirectionResponse
import java.io.File

interface CompanyRepository {
    //    fun getCompanies(): Flow<Result<List<CompaniesResponse.CompanyData>>>
//    fun clearAllData()
//    fun createCompany(
//        title: String,
//        image: File?,
//        direction: String,
//        username: String,
//        email: String
//    ): Flow<Result<Unit>>

    suspend fun createCompanyNew(
        title: String,
        image: File?,
        direction: String,
        username: String,
        email: String
    ): DataWrapper<Unit>

    suspend fun getDirection(): DataWrapper<List<DirectionResponse.Direction>>

    suspend fun getDirection2(id: Int): DataWrapper<List<DirectionResponse.Direction>>
}