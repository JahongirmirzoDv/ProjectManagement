package uz.perfectalgorithm.projects.tezkor.utils.views

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Button

fun View.showView(state: Boolean) {
    this.visibility = if (state) View.VISIBLE else View.INVISIBLE
}

fun View.goneView() {
    this.visibility = View.GONE
}

fun Button.isVisibleState(state: Boolean) {
    this.visibility = if (state) View.VISIBLE else View.GONE
}

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

fun View.onGlobalLayoutHeight(callback: (height: Int, width: Int) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback(height, width)
        }
    })
}

fun View.onGlobalLayoutWidth(callback: (width: Int) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback(width)
        }
    })
}

val Context.usableScreenSize: Point
    get() {
        val size = Point()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getSize(size)
        return size
    }

