package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.SerializedName

/**
 * Created by Jasurbek Kurganbaev on 8/26/2021 11:28 AM
 **/
data class ActiveDataBody(

    @field:SerializedName("staff_id")
    val staff_id: Int? = null,

    @field:SerializedName("activity")
    val activity: Int? = null,


    )