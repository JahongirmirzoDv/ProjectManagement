package uz.perfectalgorithm.projects.tezkor.utils.dashboard

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 *Created by farrukh_kh on 10/11/21 9:23 AM
 *uz.perfectalgorithm.projects.tezkor.utils.dashboard
 **/
class DepartmentAxisValueFormatter(
    private val titles: List<String?>
) : ValueFormatter() {

    override fun getBarLabel(barEntry: BarEntry?) = if (barEntry?.y?.toInt() == null)
        "" else titles.getOrNull(barEntry.y.toInt())?.trim()?.replace("\\s+", "\n")
        ?.replace(" ", "\n") ?: ""

    override fun getFormattedValue(value: Float) =
        titles.getOrNull(value.toInt())?.trim()?.replace("\\s+", "\n")?.replace(" ", "\n") ?: ""
}