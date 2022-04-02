package uz.perfectalgorithm.projects.tezkor.utils.dashboard

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 *Created by farrukh_kh on 9/24/21 9:13 AM
 *uz.perfectalgorithm.projects.tezkor.utils.dashboard
 **/
class StaffCountAxisValueFormatter : ValueFormatter() {

    override fun getBarLabel(barEntry: BarEntry?) = barEntry?.y?.toInt()?.toString()

    override fun getFormattedValue(value: Float) = value.toInt().toString()
}