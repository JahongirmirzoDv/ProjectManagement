package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.folder

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/24/21 11:13 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.tasks.folder
 **/
data class CreateFolderRequest(
    @field:SerializedName("title")
    val title: String
)
