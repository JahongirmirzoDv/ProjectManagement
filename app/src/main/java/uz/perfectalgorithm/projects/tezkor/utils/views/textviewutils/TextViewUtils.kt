package uz.perfectalgorithm.projects.tezkor.utils.views.textviewutils

import android.graphics.Color
import android.widget.TextView

fun TextView.setStrikeFlag() {
    val strokeFlag = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
    this.paintFlags = strokeFlag
    this.setTextColor(Color.LTGRAY)

}

fun TextView.setDefaultFlag() {
    val defaultFlag = android.graphics.Paint.CURSOR_BEFORE
    this.paintFlags = defaultFlag
    this.setTextColor(Color.BLACK)
}
