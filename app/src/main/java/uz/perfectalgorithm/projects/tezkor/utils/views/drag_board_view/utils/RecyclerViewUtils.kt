package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.utils

import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

/**
 *Created by farrukh_kh on 8/3/21 9:45 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.utils
 **/
/**
 * Get center child in X Axes
 */
fun RecyclerView.getCenterXChild(): View? {
    children.filter { isChildInCenterX(it) }.toList().let {
        return if (it.isNullOrEmpty()) null else it.first()
    }
}

/**
 * Get position of center child in X Axes
 */
fun RecyclerView.getCenterXChildPosition(): Int {
    children.filter { isChildInCenterX(it) }.toList().let {
        return if (it.isNullOrEmpty()) childCount else getChildAdapterPosition(it.first())
    }
}

/**
 * Get center child in Y Axes
 */
fun RecyclerView.getCenterYChild(): View? {
    children.filter { isChildInCenterY(it) }.toList().let {
        return if (it.isNullOrEmpty()) null else it.first()
    }
}

/**
 * Get position of center child in Y Axes
 */
fun RecyclerView.getCenterYChildPosition(): Int {
    children.filter { isChildInCenterY(it) }.toList().let {
        return if (it.isNullOrEmpty()) childCount else getChildAdapterPosition(it.first())
    }
}

fun RecyclerView.isChildInCenterX(view: View): Boolean {
    val lvLocationOnScreen = IntArray(2)
    val vLocationOnScreen = IntArray(2)
    getLocationOnScreen(lvLocationOnScreen)
    val middleX = lvLocationOnScreen[0] + width / 2
    if (childCount > 0) {
        view.getLocationOnScreen(vLocationOnScreen)
        if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.width >= middleX) {
            return true
        }
    }
    return false
}

fun RecyclerView.isChildInCenterY(view: View): Boolean {
    val lvLocationOnScreen = IntArray(2)
    val vLocationOnScreen = IntArray(2)
    getLocationOnScreen(lvLocationOnScreen)
    val middleY = lvLocationOnScreen[1] + height / 2
    if (childCount > 0) {
        view.getLocationOnScreen(vLocationOnScreen)
        if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.height >= middleY) {
            return true
        }
    }
    return false
}