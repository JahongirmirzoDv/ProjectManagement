package uz.perfectalgorithm.projects.tezkor.utils.dashboard

import android.graphics.*
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 *Created by farrukh_kh on 9/24/21 11:06 AM
 *uz.perfectalgorithm.projects.tezkor.utils.dashboard
 **/
class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val mRadius: Float
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val mBarShadowRectBuffer = RectF()

    override fun drawDataSet(c: Canvas?, dataSet: IBarDataSet?, index: Int) {

        if (c == null || dataSet == null) return

        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor

            val barData = mChart.barData

            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float

            var i = 0
            val count = Math.min(
                Math.ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt(),
                dataSet.entryCount
            )
            while (i < count) {

                val e = dataSet.getEntryForIndex(i)

                x = e.x

                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf

                trans.rectValueToPixel(mBarShadowRectBuffer)

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                c.drawRoundRect(mBarRect, mRadius, mRadius, mShadowPaint)
                i++
            }
        }

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break

            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    gradientColor.startColor,
                    gradientColor.endColor,
                    Shader.TileMode.MIRROR
                )
            }

            if (dataSet.gradientColors != null) {
                mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    dataSet.getGradientColor(j / 4).startColor,
                    dataSet.getGradientColor(j / 4).endColor,
                    Shader.TileMode.MIRROR
                )
            }


//            c.drawRoundRect(
//                RectF(
//                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
//                    buffer.buffer[j + 3]
//                ), mRadius, mRadius, mRenderPaint
//            )
            val path = roundRect(
                RectF(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]
                ), mRadius, mRadius
            )
            c.drawPath(path, mRenderPaint)

            if (drawBorder) {
//                c.drawRoundRect(
//                    RectF(
//                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
//                        buffer.buffer[j + 3]
//                    ), mRadius, mRadius, mBarBorderPaint
//                )
                val borderPath = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), mRadius, mRadius
                )
                c.drawPath(borderPath, mBarBorderPaint)
            }
            j += 4
        }
    }

    private fun roundRect(
        rect: RectF,
        rx: Float,
        ry: Float,
        tl: Boolean = true,
        tr: Boolean = true,
        br: Boolean = false,
        bl: Boolean = false
    ): Path {
        var mRx = rx
        var mRy = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (mRx < 0) mRx = 0f
        if (mRy < 0) mRy = 0f
        val width = right - left
        val height = bottom - top
        if (mRx > width / 2) mRx = width / 2
        if (mRy > height / 2) mRy = height / 2
        val widthMinusCorners = width - 2 * mRx
        val heightMinusCorners = height - 2 * mRy
        path.moveTo(right, top + mRy)
        if (tr) path.rQuadTo(0f, -mRy, -mRx, -mRy)
        else {
            path.rLineTo(0f, -mRy)
            path.rLineTo(-mRx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) path.rQuadTo(-mRx, 0f, -mRx, mRy)
        else {
            path.rLineTo(-mRx, 0f)
            path.rLineTo(0f, mRy)
        }
        path.rLineTo(0f, heightMinusCorners)
        if (bl) path.rQuadTo(0f, mRy, mRx, mRy)
        else {
            path.rLineTo(0f, mRy)
            path.rLineTo(mRx, 0f)
        }
        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(mRx, 0f, mRx, -mRy)
        else {
            path.rLineTo(mRx, 0f)
            path.rLineTo(0f, -mRy)
        }
        path.rLineTo(0f, -heightMinusCorners)
        path.close()
        return path
    }
}