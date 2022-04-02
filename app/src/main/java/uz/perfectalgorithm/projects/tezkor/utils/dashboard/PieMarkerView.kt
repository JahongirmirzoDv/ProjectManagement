package uz.perfectalgorithm.projects.tezkor.utils.dashboard

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.converter.dpToPx

/**
 *Created by farrukh_kh on 10/15/21 4:02 PM
 *uz.perfectalgorithm.projects.tezkor.utils.dashboard
 **/
class PieMarkerView(
    context: Context,
    layoutResourceId: Int
) : MarkerView(context, layoutResourceId) {

    private val maxWidth = resources.displayMetrics.widthPixels - 20f.dpToPx()
    private val tvTitle = findViewById<TextView>(R.id.tv_title)

    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {

        val w = width
        var newPosX = posX
        if (maxWidth - posX - w < w) {
            newPosX -= w
        }

        canvas?.translate(newPosX, posY)
        draw(canvas)
        canvas?.translate(-newPosX, -posY)
        super.draw(canvas, newPosX, posY)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvTitle.text = if (e is PieEntry?) {
            if (e?.value == null) "" else ("${e?.label}\n${e?.value?.toInt()}")
        } else {
            if (e?.y == null) "" else e.y.toInt().toString()
        }

        super.refreshContent(e, highlight)
    }
}