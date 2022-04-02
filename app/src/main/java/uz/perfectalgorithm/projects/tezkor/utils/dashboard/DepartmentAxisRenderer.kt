package uz.perfectalgorithm.projects.tezkor.utils.dashboard

import android.graphics.Canvas
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler


/**
 *Created by farrukh_kh on 10/11/21 10:16 AM
 *uz.perfectalgorithm.projects.tezkor.utils.dashboard
 **/
class DepartmentAxisRenderer(
    viewPortHandler: ViewPortHandler?,
    xAxis: XAxis?,
    trans: Transformer?
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val lines = formattedLabel.split("\n".toRegex()).toTypedArray()
        lines.forEachIndexed { index, s ->
            when {
                index < 2 -> Utils.drawXAxisValue(
                    c,
                    s,
                    x,
                    y + mAxisLabelPaint.textSize * index,
                    mAxisLabelPaint,
                    anchor,
                    angleDegrees
                )
                index == 2 -> Utils.drawXAxisValue(
                    c,
                    if (lines.size > 3) "$s." else s,
                    x,
                    y + mAxisLabelPaint.textSize * index,
                    mAxisLabelPaint,
                    anchor,
                    angleDegrees
                )
            }
        }
    }
}