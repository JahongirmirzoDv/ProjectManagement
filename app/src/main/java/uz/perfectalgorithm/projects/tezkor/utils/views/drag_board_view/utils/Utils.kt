package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

/**
 *Created by farrukh_kh on 8/3/21 9:46 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.utils
 **/
fun View.getBitmapFromView(): Bitmap? {
    val bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}