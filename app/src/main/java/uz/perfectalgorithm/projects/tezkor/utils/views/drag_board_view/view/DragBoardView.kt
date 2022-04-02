package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.HorizontalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import kotlin.math.abs

/**
 *Created by farrukh_kh on 8/3/21 9:49 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.view
 **/
@SuppressLint("ClickableViewAccessibility")
class DragBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : DragLayout(context, attrs) {

    val mRecyclerView by lazy { findViewById<View>(R.id.rv_lists) as PagerRecyclerView }
    private val mLayoutMain by lazy { findViewById<View>(R.id.layout_main) as DragLayout }
    private lateinit var mAdapter: HorizontalAdapter<*>
    override lateinit var mDragHelper: DragHelper

//    private val mOnPageChangedListener = object : PagerRecyclerView.OnPageChangedListener {
//        override fun onPageChanged(oldPosition: Int, newPosition: Int) {
//
//        }
//    }

//    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
//
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            val childCount = mRecyclerView.childCount
//            val width = mRecyclerView.getChildAt(0).width
//            val padding = (mRecyclerView.width - width) / 2
//            for (j in 0 until childCount) {
//                val v = recyclerView.getChildAt(j)
//                //From padding to -(v.getWidth()-padding) to the left, from large to small
//                var rate = 0f
//                if (v.left <= padding) {
//                    rate = if (v.left >= padding - v.width) {
//                        (padding - v.left) * 1f / v.width
//                    } else {
//                        1f
//                    }
//                    v.scaleX = 1 - rate * 0.1f
//                } else {
//                    //To the right, from padding to recyclerView.getWidth()-padding, from large to small
//                    if (v.left <= recyclerView.width - padding) {
//                        rate = (recyclerView.width - padding - v.left) * 1f / v.width
//                    }
//                    v.scaleX = 0.9f + rate * 0.1f
//                }
//            }
//        }
//    }

    init {
        inflate(context, R.layout.view_drag_board, this)

        mRecyclerView.setHasFixedSize(true)
//        mRecyclerView.addOnScrollListener(mOnScrollListener)
//        mRecyclerView.addOnPageChangedListener(mOnPageChangedListener)
        mRecyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {

            var lastX = 0.0f
            var lastY = 0.0f

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        rv.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (mDragHelper.isDraggingItem) {
                            rv.parent.requestDisallowInterceptTouchEvent(false)
                        } else {
                            if (abs(lastX - e.x) > abs(lastY - e.y)) {
                                rv.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        rv.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }

                lastX = e.x
                lastY = e.y
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
        mDragHelper = DragHelper(context, mRecyclerView)
        mLayoutMain.setDragHelper(mDragHelper)
    }

    fun setHorizontalAdapter(horizontalAdapter: HorizontalAdapter<*>) {
        mAdapter = horizontalAdapter
        mAdapter.dragHelper = mDragHelper
        mRecyclerView.adapter = mAdapter
    }
}