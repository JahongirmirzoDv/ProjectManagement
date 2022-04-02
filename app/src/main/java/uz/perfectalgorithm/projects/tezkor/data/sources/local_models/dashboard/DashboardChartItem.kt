package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.DashboardChartType

/**
 *Created by farrukh_kh on 9/23/21 11:42 PM
 *uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard
 **/

interface DashboardChartItem {
    val id: Int
    val title: String
    val data: List<Any>
    val type: DashboardChartType
}

data class PieChartItem(
    override val id: Int,
    override val title: String,
    override val data: List<PieEntry>,
) : DashboardChartItem {
    override val type = DashboardChartType.PIE_CHART
}

data class BarChartItem(
    override val id: Int,
    override val title: String,
    val titleList: List<String>,
    override val data: List<BarEntry>,
) : DashboardChartItem {
    override val type = DashboardChartType.BAR_CHART

    fun getTitleLinesCount(): Int {
        var linesCount = 0
        val linesList =
            titleList.map { item ->
                val count = item.trim().count { it == ' ' || it.toString() == "\\s+" }
                if (count > 3) 3 else count
            }

        linesList.forEach {
            if (it >= linesCount) {
                linesCount = it
            }
        }

        return ++linesCount
    }
}

data class GoalChartItem(
    override val id: Int,
    override val title: String,
    override val data: List<Float>,
) : DashboardChartItem {
    override val type = DashboardChartType.GOAL_CHART
}