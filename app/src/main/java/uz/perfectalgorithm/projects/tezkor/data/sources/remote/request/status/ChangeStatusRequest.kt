package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/5/21 10:28 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.status
 **/
data class ChangeStatusRequest(
    @field:SerializedName("status")
    val statusId: Int
)