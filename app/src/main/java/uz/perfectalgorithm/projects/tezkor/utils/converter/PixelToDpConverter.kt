package uz.perfectalgorithm.projects.tezkor.utils.converter

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics


fun Float.pxToDp(): Float {
    return (this / Resources.getSystem().displayMetrics.density)
}

fun Float.dpToPx(): Float {
    return (this * Resources.getSystem().displayMetrics.density)
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}