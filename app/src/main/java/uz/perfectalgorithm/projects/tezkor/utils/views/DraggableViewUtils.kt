package uz.perfectalgorithm.projects.tezkor.utils.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import io.github.hyuwah.draggableviewlib.Draggable.DRAG_TOLERANCE
import io.github.hyuwah.draggableviewlib.Draggable.DURATION_MILLIS
import io.github.hyuwah.draggableviewlib.DraggableListener
import io.github.hyuwah.draggableviewlib.DraggableView
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 *Created by farrukh_kh on 10/8/21 3:31 AM
 *uz.perfectalgorithm.projects.tezkor.utils.views
 **/

internal fun View.marginStart(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart ?: 0).toFloat()
}

internal fun View.marginEnd(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd ?: 0).toFloat()
}

internal fun View.marginTop(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0).toFloat()
}

internal fun View.marginBottom(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0).toFloat()
}

@JvmOverloads
internal fun View.setupDraggableCustom(
//    minimizeBtnListener: DraggableView.Listener,
    stickyAxis: DraggableView.Mode = DraggableView.Mode.NON_STICKY,
    animated: Boolean = true,
    draggableListener: DraggableListener? = null,
    onDragStarted: EmptyBlock,
    onDragStopped: EmptyBlock
) {
    var widgetInitialX = 0f
    var widgetDX = 0f
    var widgetInitialY = 0f
    var widgetDY = 0f

    val marginStart = marginStart()
    val marginTop = marginTop()
    val marginEnd = marginEnd()
    val marginBottom = marginBottom()

    setOnTouchListener { v, event ->
        val viewParent = v.parent as View
        val parentHeight = viewParent.height
        val parentWidth = viewParent.width
        val xMax = parentWidth - v.width - marginEnd
        val xMiddle = parentWidth / 2
        val yMax = parentHeight - v.height - marginBottom
        val yMiddle = parentHeight / 2

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                widgetDX = v.x - event.rawX
                widgetDY = v.y - event.rawY
                widgetInitialX = v.x
                widgetInitialY = v.y
                onDragStarted()
            }
            MotionEvent.ACTION_MOVE -> {
                var newX = event.rawX + widgetDX
                newX = max(marginStart, newX)
                newX = min(xMax, newX)
                v.x = newX

                var newY = event.rawY + widgetDY
                newY = max(marginTop, newY)
                newY = min(yMax, newY)
                v.y = newY

                draggableListener?.onPositionChanged(v)
//                minimizeBtnListener.onPositionChanged(v, StickyRestSide.HIDE)
            }
            MotionEvent.ACTION_UP -> {
                when (stickyAxis) {
                    DraggableView.Mode.STICKY_X -> {
                        if (event.rawX >= xMiddle) {
                            if (animated)
                                v.animate().x(xMax)
                                    .setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
//                                        minimizeBtnListener.onPositionChanged(v, StickyRestSide.RIGHT)
                                    }
                                    .setListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            super.onAnimationEnd(animation)
                                        }
                                    })
                                    .start()
                            else {
                                v.x = xMax
//                                minimizeBtnListener.onPositionChanged(v, StickyRestSide.RIGHT)
                            }
                        } else {
                            if (animated)
                                v.animate().x(marginStart).setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
//                                        minimizeBtnListener.onPositionChanged(v, StickyRestSide.LEFT)
                                    }
                                    .start()
                            else {
                                v.x = marginStart
//                                minimizeBtnListener.onPositionChanged(v, StickyRestSide.LEFT)
                            }
                        }
                    }
                    DraggableView.Mode.STICKY_Y -> {
                        if (event.rawY >= yMiddle) {
                            if (animated)
                                v.animate().y(yMax)
                                    .setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
//                                        minimizeBtnListener.onPositionChanged(v, StickyRestSide.BOTTOM)
                                    }
                                    .start()
                            else
                                v.y = yMax
                        } else {
                            if (animated)
                                v.animate().y(marginTop)
                                    .setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.y = marginTop
                        }
                    }
                    DraggableView.Mode.STICKY_XY -> {
                        if (event.rawX >= xMiddle) {
                            if (animated)
                                v.animate().x(xMax)
                                    .setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.x = xMax
                        } else {
                            if (animated)
                                v.animate().x(marginStart).setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            v.x = marginStart
                        }

                        if (event.rawY >= yMiddle) {
                            if (animated)
                                v.animate().y(yMax)
                                    .setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.y = yMax
                        } else {
                            if (animated)
                                v.animate().y(marginTop).setDuration(DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.y = marginTop
                        }
                    }
                    else -> {
                    }
                }

                if (abs(v.x - widgetInitialX) <= DRAG_TOLERANCE && abs(v.y - widgetInitialY) <= DRAG_TOLERANCE) {
                    performClick()
                }
                onDragStopped()
            }
            else -> return@setOnTouchListener false
        }
        true
    }
}