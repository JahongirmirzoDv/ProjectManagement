package uz.perfectalgorithm.projects.tezkor.utils.views.calendarViews

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

class MyScrollView : ScrollView {
    var isScrollable = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )



    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        if (isScrollable){
            super.onScrollChanged(x, y, oldx, oldy)
        }
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return if (isScrollable) {
//            super.onTouchEvent(event)
//        } else {
//            true
//        }
//    }

//    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
//        timberLog(isScrollable.toString(), "KKKScrollable1")
//        return if (isScrollable) {
//            super.onInterceptTouchEvent(event)
//        } else {
//            false
//        }
//    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: MyScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
    }
}
