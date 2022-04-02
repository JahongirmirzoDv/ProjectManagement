package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard

import com.github.mikephil.charting.data.PieEntry

/**
 *Created by farrukh_kh on 7/17/21 10:03 AM
 *uz.rdo.projects.projectmanagement.data.sources.local_models.dashboard
 **/
data class ChartData(
    val id: Int,
    val title: String,
    val data: List<PieEntry>,
)