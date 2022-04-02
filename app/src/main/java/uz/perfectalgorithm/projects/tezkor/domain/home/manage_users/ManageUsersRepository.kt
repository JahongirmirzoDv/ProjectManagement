package uz.perfectalgorithm.projects.tezkor.domain.home.manage_users

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user.ManageUsers

interface ManageUsersRepository {

    fun getManageUsers(): Flow<Result<List<ManageUsers>>>
}