package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragHorizontalViewHolder
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragVerticalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.utils.getBitmapFromView
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.view.PagerRecyclerView
import java.util.*
import kotlin.math.abs

/**
 *Created by farrukh_kh on 8/3/21 9:44 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.helper
 **/
@SuppressLint("ClickableViewAccessibility")
class DragHelper(activity: Context, private val mHorizontalRecyclerView: PagerRecyclerView) {

    private lateinit var mCurrentVerticalRecycleView: RecyclerView

    private lateinit var dragItem: DragItem
    internal var isDraggingItem = false

    //The coordinates of the view when grabbed
    private var mBornLocationX = 0f
    private var mBornLocationY = 0f

    //The distance between the view coordinate and the click point when grabbed
    private var offsetX = 0
    private var offsetY = 0

    //Is the offset determined
    private var confirmOffset = false

    private val mHorizontalScrollTimer = Timer()
    private val mVerticalScrollTimer = Timer()
    private var mHorizontalScrollTimerTask: TimerTask? = null
    private var mVerticalScrollTimerTask: TimerTask? = null

    //When dragging, the boundary that starts to scroll to some direction
    private var leftScrollBounce = 0
    private var rightScrollBounce = 0
    private var upScrollBounce = 0
    private var downScrollBounce = 0

    //The position of the dragged View on the vertical recyclerView
    private var mPosition = -1
    private var originalPosition = -1

    //The position of the dragged View on the horizontal recyclerView
    private var mPagerPosition = -1
    private var originalPagePosition = -1

    private var originalAdapter: DragVerticalAdapter? = null

    private val mWindowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mWindowParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        alpha = 1.0f
        format = PixelFormat.TRANSLUCENT
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.TOP or Gravity.START
        x = 0
        y = 0
    }
    private val mDragImageView = ImageView(activity).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(10, 10, 10, 10)
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE || event.action == MotionEvent.ACTION_DOWN) {
                dropItem()
            }
            false
        }
    }

    private val screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = mWindowManager.currentWindowMetrics
        windowMetrics.bounds.width()
    } else {
        val dm = DisplayMetrics()
        @Suppress("DEPRECATION")
        mWindowManager.defaultDisplay.getMetrics(dm)
        dm.widthPixels
    }

    //region drag item

    /**
     * Drag item
     *
     * @param draggedView  Dragged View
     * @param position Position of the dragged view in the vertical RecyclerView
     */
    fun dragItem(draggedView: View, position: Int) {
        val bitmap = draggedView.getBitmapFromView()
        if (bitmap != null && !bitmap.isRecycled) {
            mDragImageView.setImageBitmap(bitmap)
            mDragImageView.rotation = 1.5f
            mDragImageView.alpha = 0.8f
            isDraggingItem = true
            if (draggedView.tag is DragItem) {
                dragItem = draggedView.tag as DragItem
            }
            val dragPage = mHorizontalRecyclerView.getCurrentPosition()
            val holder = mHorizontalRecyclerView.findViewHolderForAdapterPosition(dragPage)
            if (holder?.itemView != null && holder.itemViewType == TYPE_CONTENT) {
                mCurrentVerticalRecycleView = (holder as DragHorizontalViewHolder).getRecyclerView()
                mPagerPosition = dragPage
                originalPagePosition = dragPage
                originalPosition = position
                originalAdapter = getCurrentVerticalAdapter()
            }
            getTargetHorizontalRecyclerViewScrollBoundaries()
            getTargetVerticalRecyclerViewScrollBoundaries()
            val location = IntArray(2)
            draggedView.getLocationOnScreen(location)
            mWindowParams.x = location[0]
            mWindowParams.y = location[1]
            mBornLocationX = location[0].toFloat()
            mBornLocationY = location[1].toFloat()
            confirmOffset = false
            mPosition = position
            mWindowManager.addView(mDragImageView, mWindowParams)
            getCurrentVerticalAdapter().onDrag(position)
        }
    }

    /**
     * Drop item
     */
    fun dropItem() {
        if (isDraggingItem) {
            mWindowManager.removeView(mDragImageView)
            isDraggingItem = false

            mVerticalScrollTimerTask?.cancel()
            mHorizontalScrollTimerTask?.cancel()
            mHorizontalRecyclerView.backToCurrentPage()

            getCurrentVerticalAdapter().onDrop(
                mPagerPosition,
                originalPagePosition,
                mPosition,
                originalPosition,
                dragItem
            )
            if (mPagerPosition != originalPagePosition) {
                originalAdapter?.deleteItemFromOldList(originalPosition)
            }
            originalAdapter = null
        }
    }

    /**
     * Update the RecyclerView under the current drag point
     */
    private fun updateSlidingVerticalRecyclerView(x: Float, y: Float) {
        //The incoming is relative to the screen x, y
        val newPage = getHorizontalCurrentPosition(x, y)
        if (mPagerPosition != newPage) {
            val holder = mHorizontalRecyclerView.findViewHolderForAdapterPosition(newPage)
            if (holder?.itemView != null && holder.itemViewType == TYPE_CONTENT) {
                mCurrentVerticalRecycleView = (holder as DragHorizontalViewHolder).getRecyclerView()
                mPagerPosition = newPage
            }
        }
    }

    /**
     * Get the position of the currently dragged item
     *
     * @param rowX The abscissa of the drag point relative to the screen
     * @param rowY The ordinate of the drag point relative to the screen
     */
    private fun findViewPositionInCurVerticalRV(rowX: Float, rowY: Float) {
        val location = IntArray(2)
        mCurrentVerticalRecycleView.getLocationOnScreen(location)
        val x = rowX - location[0]
        val y = rowY - location[1]
        //The value passed in this method is relative to recyclerView
        val child = mCurrentVerticalRecycleView.findChildViewUnder(x, y)
        child?.let {
            val newPosition = mCurrentVerticalRecycleView.getChildAdapterPosition(it)
            if (newPosition != RecyclerView.NO_POSITION && mPosition != newPosition) {
                mPosition = newPosition
            }
        }
    }

    /**
     * Get the Adapter of the current vertical RecyclerView
     *
     * @return Adapter
     */
    private fun getCurrentVerticalAdapter() =
        mCurrentVerticalRecycleView.adapter as DragVerticalAdapter

    //endregion

    //region compute

    /**
     * Update the coordinates of the drag point
     *
     * @param rowX The abscissa relative to the screen
     * @param rowY The ordinate relative to the screen
     */
    fun updateDraggingPosition(rowX: Float, rowY: Float) {
        if (!confirmOffset) {
            calculateOffset(rowX, rowY)
        }
        if (isDraggingItem) {
            mWindowParams.x = (rowX - offsetX).toInt()
            mWindowParams.y = (rowY - offsetY).toInt()
            mWindowManager.updateViewLayout(mDragImageView, mWindowParams)
            updateSlidingVerticalRecyclerView(rowX, rowY)
            findViewPositionInCurVerticalRV(rowX, rowY)
            recyclerViewScrollHorizontal(rowX.toInt(), rowY.toInt())
            recyclerViewScrollVertical(rowX.toInt(), rowY.toInt())
        }
    }

    /**
     * Calculate the offset between the drag point and the generated ImageView coordinates
     *
     * @param x Abscissa
     * @param y Ordinate
     */
    private fun calculateOffset(x: Float, y: Float) {
        offsetX = abs(x - mBornLocationX).toInt()
        offsetY = abs(y - mBornLocationY).toInt()
        confirmOffset = true
    }

    /**
     * Get the vertical sliding boundary
     */
    private fun getTargetVerticalRecyclerViewScrollBoundaries() {
        val location = IntArray(2)
        mCurrentVerticalRecycleView.getLocationOnScreen(location)
        upScrollBounce = location[1] + 150
        downScrollBounce = location[1] + mCurrentVerticalRecycleView.height - 150
    }

    /**
     * Get the horizontal sliding boundary
     */
    private fun getTargetHorizontalRecyclerViewScrollBoundaries() {
        leftScrollBounce = 200
        rightScrollBounce = screenWidth - 200
    }

    /**
     * Horizontal RecyclerView sliding
     *
     * @param x Drag point abscissa
     * @param y Drag point ordinate
     */
    private fun recyclerViewScrollHorizontal(x: Int, y: Int) {
        mHorizontalScrollTimerTask?.cancel()
        if (x > rightScrollBounce) {
            mHorizontalScrollTimerTask = object : TimerTask() {
                override fun run() {
                    mHorizontalRecyclerView.post {
                        mHorizontalRecyclerView.scrollBy(HORIZONTAL_STEP, 0)
                        findViewPositionInCurVerticalRV(x.toFloat(), y.toFloat())
                    }
                }
            }
            mHorizontalScrollTimer.schedule(
                mHorizontalScrollTimerTask,
                0,
                HORIZONTAL_SCROLL_PERIOD.toLong()
            )
        } else if (x < leftScrollBounce) {
            mHorizontalScrollTimerTask = object : TimerTask() {
                override fun run() {
                    mHorizontalRecyclerView.post {
                        mHorizontalRecyclerView.scrollBy(-HORIZONTAL_STEP, 0)
                        findViewPositionInCurVerticalRV(x.toFloat(), y.toFloat())
                    }
                }
            }
            mHorizontalScrollTimer.schedule(
                mHorizontalScrollTimerTask,
                0,
                HORIZONTAL_SCROLL_PERIOD.toLong()
            )
        }
    }

    /**
     * Vertical RecyclerView sliding
     *
     * @param x Drag point abscissa
     * @param y Drag point ordinate
     */
    private fun recyclerViewScrollVertical(x: Int, y: Int) {
        mVerticalScrollTimerTask?.cancel()
        if (y > downScrollBounce) {
            mVerticalScrollTimerTask = object : TimerTask() {
                override fun run() {
                    mCurrentVerticalRecycleView.post {
                        mCurrentVerticalRecycleView.scrollBy(
                            0,
                            VERTICAL_STEP
                        )
                        findViewPositionInCurVerticalRV(x.toFloat(), y.toFloat())
                    }
                }
            }
            mVerticalScrollTimer.schedule(
                mVerticalScrollTimerTask,
                0,
                VERTICAL_SCROLL_PERIOD.toLong()
            )
        } else if (y < upScrollBounce) {
            mVerticalScrollTimerTask = object : TimerTask() {
                override fun run() {
                    mCurrentVerticalRecycleView.post {
                        mCurrentVerticalRecycleView.scrollBy(
                            0,
                            -VERTICAL_STEP
                        )
                        findViewPositionInCurVerticalRV(x.toFloat(), y.toFloat())
                    }
                }
            }
            mVerticalScrollTimer.schedule(
                mVerticalScrollTimerTask,
                0,
                VERTICAL_SCROLL_PERIOD.toLong()
            )
        }
    }

    private fun getHorizontalCurrentPosition(rowX: Float, rowY: Float): Int {
        val location = IntArray(2)
        mHorizontalRecyclerView.getLocationOnScreen(location)
        val x = rowX - location[0]
        val y = rowY - location[1]
        val child = mHorizontalRecyclerView.findChildViewUnder(x, y)
        return if (child != null) {
            mHorizontalRecyclerView.getChildAdapterPosition(child)
        } else mPagerPosition
    }

    //endregion

    companion object {
        const val TYPE_CONTENT = 1
        const val TYPE_FOOTER = 2
        private const val HORIZONTAL_STEP = 15 // Horizontal sliding pace
        private const val HORIZONTAL_SCROLL_PERIOD = 20 // Horizontal sliding interval
        private const val VERTICAL_STEP = 10 // Vertical sliding pace
        private const val VERTICAL_SCROLL_PERIOD = 10 // Vertical sliding interval
    }
}