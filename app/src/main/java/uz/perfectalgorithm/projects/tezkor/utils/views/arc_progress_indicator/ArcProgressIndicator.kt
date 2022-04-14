package uz.perfectalgorithm.projects.tezkor.utils.views.arc_progress_indicator

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import uz.perfectalgorithm.projects.tezkor.R



class ArcProgressIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private var mValue = 20
    private lateinit var circleProgressView: CircleProgressView
    private lateinit var ivIndicator: ImageView

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.arc_progress_view, this, true)

        ivIndicator = customView.findViewById(R.id.iv_indicator)
        circleProgressView = customView.findViewById(R.id.circle_progress_view)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressIndicator)
            setMinValue(ta.getInteger(R.styleable.ArcProgressIndicator_min_value, 0))
            setMaxValue(ta.getInteger(R.styleable.ArcProgressIndicator_max_value, 100))
            setValue(ta.getInteger(R.styleable.ArcProgressIndicator_value, 20))
//            setSectionsCount(ta.getInteger(R.styleable.ArcProgressIndicator_sections_count, 4))
            circleProgressView.apply {
                mIsPercentValues =
                    ta.getBoolean(R.styleable.ArcProgressIndicator_is_percent_values, true)
                mIsSectionsDivided =
                    ta.getBoolean(R.styleable.ArcProgressIndicator_is_sections_divided, true)
                mIsHighlightCurrentSectionEnabled = ta.getBoolean(
                    R.styleable.ArcProgressIndicator_is_highlight_current_section_enabled,
                    true
                )
            }
            ta.recycle()
        }
    }

    private fun getStep(): Float {
        val min = circleProgressView.getMinValue()
        val max = circleProgressView.getMaxValue()
        var s = (max - min).toFloat()
        s = 180 / s
        return s
    }

    fun setValue(value: Int) {
        circleProgressView.setValue(value)
        if (value in circleProgressView.getRange()) {
            mValue = value
            var angle = value * getStep()

            if (angle > 180) {
                angle = 180f
            }
            val rotate = RotateAnimation(
                0f,
                angle,
                Animation.RELATIVE_TO_SELF,
                1f,
                Animation.RELATIVE_TO_SELF,
                1f
            )

            rotate.duration = 800
            rotate.repeatCount = 0
            rotate.fillAfter = true
            ivIndicator.startAnimation(rotate)
            invalidate()
        }
    }

    fun setMinValue(minValue: Int) {
        circleProgressView.setMinValue(minValue)
    }

    fun setMaxValue(maxValue: Int) {
        circleProgressView.setMaxValue(maxValue)
    }

    fun setRange(minValue: Int, maxValue: Int) {
        circleProgressView.setRange(minValue, maxValue)
    }

    fun setSectionsCount(sectionsCount: Int) {
        circleProgressView.setSectionsCount(sectionsCount)
    }
}