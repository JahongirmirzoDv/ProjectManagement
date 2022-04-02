package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.view

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.RecyclerViewPagerAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.utils.getCenterXChildPosition
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.utils.getCenterYChildPosition

/**
 *Created by farrukh_kh on 8/3/21 9:49 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.view
 **/
class PagerRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var mViewPagerAdapter: RecyclerViewPagerAdapter<*>? = null
    private var mSmoothScrollTargetPosition = -1

    init {
        PagerSnapHelper().attachToRecyclerView(this)
        isNestedScrollingEnabled = false
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        try {
            val fLayoutState = state.javaClass.getDeclaredField("mLayoutState")
            fLayoutState.isAccessible = true
            val layoutState = fLayoutState[state]
            val fAnchorOffset = layoutState.javaClass.getDeclaredField("mAnchorOffset")
            val fAnchorPosition = layoutState.javaClass.getDeclaredField("mAnchorPosition")
            fAnchorPosition.isAccessible = true
            fAnchorOffset.isAccessible = true
            if (fAnchorOffset.getInt(layoutState) > 0) {
                fAnchorPosition[layoutState] = fAnchorPosition.getInt(layoutState) - 1
            } else if (fAnchorOffset.getInt(layoutState) < 0) {
                fAnchorPosition[layoutState] = fAnchorPosition.getInt(layoutState) + 1
            }
            fAnchorOffset.setInt(layoutState, 0)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        super.onRestoreInstanceState(state)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        mViewPagerAdapter = ensureRecyclerViewPagerAdapter(adapter)
        super.setAdapter(mViewPagerAdapter)
    }

    override fun swapAdapter(
        adapter: Adapter<ViewHolder>?,
        removeAndRecycleExistingViews: Boolean
    ) {
        mViewPagerAdapter = ensureRecyclerViewPagerAdapter(adapter)
        super.swapAdapter(adapter, removeAndRecycleExistingViews)
    }

    override fun getAdapter() = mViewPagerAdapter?.adapter

    override fun smoothScrollToPosition(position: Int) {
        mSmoothScrollTargetPosition = position
//        if (layoutManager != null && layoutManager is LinearLayoutManager) {
//            val linearSmoothScroller = object : LinearSmoothScroller(context) {
//                override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
//                    return if (layoutManager == null) {
//                        null
//                    } else (layoutManager as LinearLayoutManager?)!!.computeScrollVectorForPosition(
//                        targetPosition
//                    )
//                }
//
//                override fun onTargetFound(targetView: View, state: State, action: Action) {
//                    if (layoutManager == null) {
//                        return
//                    }
//                    var dx = calculateDxToMakeVisible(targetView, horizontalSnapPreference)
//                    var dy = calculateDyToMakeVisible(targetView, verticalSnapPreference)
//                    dx = if (dx > 0) {
//                        dx - layoutManager!!.getLeftDecorationWidth(targetView)
//                    } else {
//                        dx + layoutManager!!.getRightDecorationWidth(targetView)
//                    }
//                    dy = if (dy > 0) {
//                        dy - layoutManager!!.getTopDecorationHeight(targetView)
//                    } else {
//                        dy + layoutManager!!.getBottomDecorationHeight(targetView)
//                    }
//                    val distance = sqrt((dx * dx + dy * dy).toDouble()).toInt()
//                    val time = calculateTimeForDeceleration(distance)
//                    if (time > 0) {
//                        action.update(-dx, -dy, time, mDecelerateInterpolator)
//                    }
//                }
//            }
//            linearSmoothScroller.targetPosition = position
//            if (position == NO_POSITION) {
//                return
//            }
//            layoutManager!!.startSmoothScroll(linearSmoothScroller)
//        } else {
//            super.smoothScrollToPosition(position)
//        }
        super.smoothScrollToPosition(position)
    }

    override fun scrollToPosition(position: Int) {
        mSmoothScrollTargetPosition = position
        super.scrollToPosition(position)
    }

    private fun ensureRecyclerViewPagerAdapter(adapter: Adapter<*>?) =
        if (adapter is RecyclerViewPagerAdapter<*>) adapter else RecyclerViewPagerAdapter(
            this,
            adapter as Adapter<ViewHolder>
        )

    fun backToCurrentPage() {
        smoothScrollToPosition(getCurrentPosition())
    }

    fun getCurrentPosition(): Int {
        var curPosition = if (layoutManager!!.canScrollHorizontally())
            getCenterXChildPosition() else getCenterYChildPosition()

        if (curPosition < 0) {
            curPosition = mSmoothScrollTargetPosition
        }
        return curPosition
    }
}