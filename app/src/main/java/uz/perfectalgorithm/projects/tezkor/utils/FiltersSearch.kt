package uz.perfectalgorithm.projects.tezkor.utils

import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import java.util.*
import kotlin.collections.ArrayList

fun searchFilterRECYCLER(
    text: String,
    list: List<AllWorkersResponse.DataItem>
): List<AllWorkersResponse.DataItem> {
    val yList = ArrayList<AllWorkersResponse.DataItem>()
    for (a in list) {
        if (a.firstName!!.toLowerCase().contains(text.toLowerCase()) || a.lastName!!.toLowerCase()
                .contains(text.toLowerCase())
        ) {
            yList.add(a)
        }
    }
    return yList
}

fun searchFilterRECYCLERShort(
    text: String,
    list: List<AllWorkersShort>
): List<AllWorkersShort> {
    val yList = ArrayList<AllWorkersShort>()
    for (a in list) {
        if (a.fullName!!.toLowerCase().contains(text.toLowerCase())) {
            yList.add(a)
        }
    }
    return yList
}

fun searchFilterRecycler(
    text: String,
    list: List<StaffBelow>
): List<StaffBelow> {
    val yList = ArrayList<StaffBelow>()
    for (a in list) {
        if (a.firstName.lowercase(Locale.getDefault())
                .contains(text.lowercase(Locale.getDefault())) || a.lastName.lowercase(Locale.getDefault())
                .contains(text.lowercase(Locale.getDefault()))
        ) {
            yList.add(a)
        }
    }
    return yList
}




