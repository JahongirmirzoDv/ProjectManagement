package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user.ManageUsersResponse

interface ManageUsersApi {

    @GET("/api/v1/user/user-in-manage")
    suspend fun getManageUsers(): Response<ManageUsersResponse>
}