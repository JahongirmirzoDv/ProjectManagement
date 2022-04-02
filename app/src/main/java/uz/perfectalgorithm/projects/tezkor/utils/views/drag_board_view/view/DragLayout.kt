package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper

/**
 *Created by farrukh_kh on 8/3/21 9:49 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.view
 **/
open class DragLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    open lateinit var mDragHelper: DragHelper

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (mDragHelper.isDraggingItem) {
            requestDisallowInterceptTouchEvent(true)
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mDragHelper.isDraggingItem) {
                    mDragHelper.updateDraggingPosition(event.rawX, event.rawY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mDragHelper.isDraggingItem) {
                    mDragHelper.dropItem()
                }
                requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onTouchEvent(event)
    }

    fun setDragHelper(dragHelper: DragHelper) {
        mDragHelper = dragHelper
    }
}