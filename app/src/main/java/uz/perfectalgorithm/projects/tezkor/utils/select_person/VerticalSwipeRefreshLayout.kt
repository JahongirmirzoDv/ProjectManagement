package uz.perfectalgorithm.projects.tezkor.utils.select_person

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import android.view.ViewConfiguration

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs


/**
 *Created by farrukh_kh on 10/12/21 11:52 AM
 *uz.perfectalgorithm.projects.tezkor.utils.select_person
 **/
class VerticalSwipeRefreshLayout(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var prevX = 0f
    private var declined = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                prevX = event.x
                declined = false
            }
            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = abs(eventX - prevX)
                if (declined || xDiff > touchSlop) {
                    declined = true
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}
