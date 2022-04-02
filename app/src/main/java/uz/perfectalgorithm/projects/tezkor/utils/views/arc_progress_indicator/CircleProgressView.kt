package uz.perfectalgorithm.projects.tezkor.utils.views.arc_progress_indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.getTypeface


/**
 *Created by farrukh_kh on 9/25/21 2:30 PM
 *uz.perfectalgorithm.projects.tezkor.utils.views.goal_percent_view
 **/
class CircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private lateinit var defaultPaint: Paint
    private lateinit var highlightPaint: Paint
    private lateinit var textPaint: Paint
    private val textTypeface by lazy { context.getTypeface() }

    private var mHeight = 0
    private var mWidth = 0

    private var mValue = 0
    private var mMinValue = 0
    private var mMaxValue = 100
    private var mSectionsCount = 4
    private var mSectionSliceLength = 0f
    private var mDividerLength = 0f
    private var mFullArcSliceLength = 0f

    var mIsSectionsDivided = true
    var mIsPercentValues = true
    var mIsHighlightCurrentSectionEnabled = true

    init {
        init()
    }

    private fun init() {
        defaultPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#E0E0E0")
            style = Paint.Style.STROKE
            strokeWidth = 50f
        }

        highlightPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#86C9F8")
            style = Paint.Style.STROKE
            strokeWidth = 50f
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            textSize = 20f
            typeface = textTypeface
        }

        mFullArcSliceLength = -180f / mSectionsCount
        mDividerLength = -1f
        mSectionSliceLength = mFullArcSliceLength - 2 * mDividerLength
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mHeight = height
        mWidth = width
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(mWidth, mWidth / 2)
//    }

    private fun getStep(): Float {
        var s = (mMaxValue - mMinValue).toFloat()
        s = 180 / s
        return s
    }

    fun setValue(value: Int) {
        if (value in mMinValue..mMaxValue) {
            mValue = value
            invalidate()
        } else {
            throw IllegalArgumentException("Value must be in $mMinValue..$mMaxValue range")
        }
    }

    fun setMinValue(minValue: Int) {
        if (minValue < mMaxValue) {
            mMinValue = minValue
        } else {
            throw IllegalArgumentException("minValue must be smaller than maxValue")
        }
    }

    fun setMaxValue(maxValue: Int) {
        if (maxValue > mMinValue) {
            mMaxValue = maxValue
        } else {
            throw IllegalArgumentException("maxValue must be bigger than minValue")
        }
    }

    fun setRange(minValue: Int, maxValue: Int) {
        if (minValue < maxValue && maxValue > minValue) {
            mMinValue = minValue
            mMaxValue = maxValue
        } else {
            throw IllegalArgumentException("minValue must be smaller than maxValue")
        }
    }

    fun getMinValue() = mMinValue

    fun getMaxValue() = mMaxValue

    fun getRange() = mMinValue..mMaxValue

    fun setSectionsCount(sectionsCount: Int) {
        if (sectionsCount > 0) {
            mSectionsCount = sectionsCount
            mFullArcSliceLength = -180f / mSectionsCount
            mDividerLength = -1f
            mSectionSliceLength = mFullArcSliceLength - 2 * mDividerLength
            invalidate()
        } else {
            throw IllegalArgumentException("sectionsCount must be bigger than 0")
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let { c ->
            if (mIsSectionsDivided) {
                drawDividedSections(c)
            } else {
                drawSingleSection(c)
            }
        }
    }

    private fun drawSingleSection(canvas: Canvas) {
        canvas.drawArc(
            100f,
            100f,
            width - 100f,
            height * 2 - 100f,
            0f,
            -180f,
            false,
            defaultPaint
        )
    }

    private fun drawDividedSections(canvas: Canvas) {
        for (i in 0 until mSectionsCount) {
            if ((mValue * getStep() - 180) <= i * mFullArcSliceLength && (mValue * getStep() - 180) > (i + 1) * mFullArcSliceLength) {
                canvas.drawArc(
                    100f,
                    100f,
                    width - 100f,
                    height * 2 - 100f,
                    i * mFullArcSliceLength + mDividerLength,
                    mSectionSliceLength,
                    false,
                    highlightPaint
                )
            } else {
                canvas.drawArc(
                    100f,
                    100f,
                    width - 100f,
                    height * 2 - 100f,
                    i * mFullArcSliceLength + mDividerLength,
                    mSectionSliceLength,
                    false,
                    defaultPaint
                )
            }
            //dynamic
            //todo add i == mSectionsCount
            when {
                i == 0 -> {
                    //todo draw to right
                }
                i == mSectionsCount -> {
                    //todo draw to left
                }
                i == mSectionsCount / 2 -> {
                    //todo draw to top
                }
                i < mSectionsCount / 2 -> {
                    //todo draw to top and right
                }
                i > mSectionsCount / 2 -> {
                    //todo draw to top and left
                }
            }
        }

        //static (temp)
//        canvas.drawText(
//            "50%",
//            width / 2f,
//            100f - 10.dpToPixel(context),
//            textPaint
//        )
//        canvas.drawText(
//            "0%",
//            100f - 10.dpToPixel(context),
//            height.toFloat(),
//            textPaint
//        )
//        canvas.drawText(
//            "100%",
//            width - 100f,
//            height.toFloat(),
//            textPaint
//        )
//        canvas.drawText(
//            "75%",
//            width / 2f + width / 2f / sqrt(2f),
//            height - width / 2f / sqrt(2f),
//            textPaint
//        )

//        canvas.drawPoint(width / 2f, height.toFloat(), highlightPaint)
    }

    private fun Int.dpToPixel(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}