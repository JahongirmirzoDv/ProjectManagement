package uz.perfectalgorithm.projects.tezkor.domain.home.workers

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.positons.AllPositionsResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import java.io.File

/**
 * Created by Davronbek Daximjanov on 17.06.2021
 **/

interface WorkersRepository {

    suspend fun getStaff(staffId: Int): DataWrapper<AllWorkersResponse.DataItem>

    fun getTeamWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>>

    fun getTeamWorkersShortFlow(): Flow<Result<List<AllWorkersShort>>>

    suspend fun getTeamWorkersNew(): DataWrapper<List<AllWorkersResponse.DataItem>>

    suspend fun getTeamWorkersShort(): DataWrapper<List<AllWorkersShort>>

    fun getUserData(): Flow<Result<UserDataResponse.Data>>

    fun getAllWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>>

    fun getAllWorkersShortFlow(): Flow<Result<List<AllWorkersShort>>>

    suspend fun getAllWorkersNew(): DataWrapper<List<AllWorkersResponse.DataItem>>

    suspend fun getAllWorkersShort(): DataWrapper<List<AllWorkersShort>>

    fun getStructure(): Flow<Result<List<StructureResponse.DataItem>>>

    fun getStructureShortFlow(): Flow<Result<List<DepartmentStructureShort>>>

    suspend fun getStructureNew(): DataWrapper<List<StructureResponse.DataItem?>>

    suspend fun getStructureShort(): DataWrapper<List<DepartmentStructureShort?>>

    fun createWorker(
        name: String,
        lastName: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        password: String,
        positions: List<Int>,
        gender: String,
        isOutsource: Boolean
    ): Flow<Result<CreateWorkerResponse.DataItem>>

    fun createWorkerWithImage(
        name: String,
        lastName: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        password: String,
        positions: List<Int>,
        gender: String,
        file: File,
        isOutsource: Boolean
    ): Flow<Result<CreateWorkerResponse.DataItem>>

    fun getStructureWithPositions(): Flow<Result<List<StructurePositionsResponse.DataItem>>>


    fun createPosition(createPositionRequest: CreatePositionRequest): Flow<Result<CreatePositionResponse.PositionData>>

    fun addUserToFavourites(addUserToFavouritesRequest: AddUserToFavouritesRequest): Flow<Result<WorkerDataResponse.WorkerData>>

    fun removeUserFromFavourites(removeUserFromFavouritesRequest: RemoveUserFromFavouritesRequest): Flow<Result<WorkerDataResponse.WorkerData>>

    fun getOutsourceWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>>

    suspend fun getOutsourceWorkersNew(): DataWrapper<List<AllWorkersResponse.DataItem>>

    fun getPermissionsList(): Flow<Result<List<PermissionsListResponse.PermissionData>>>

    fun getBelongsToUserPermissions(): Flow<Result<List<String>>>

    fun deleteWorker(id: Int): Flow<Result<Unit>>

    fun updateContactData(updateContactDataRequest: UpdateContactDataRequest): Flow<Result<UpdateContactDataResponse.Data>>

    fun editWorkerData(
        id: Int,
        editWorkerRequest: EditWorkerRequest
    ): Flow<Result<EditWorkerDataResponse.Data>>

    fun getPositionList(): Flow<Result<List<AllPositionsResponse.PositionDataItem>>>


}