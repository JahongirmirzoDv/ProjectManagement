package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.*
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.BarChartItem
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DashboardChartItem
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.GoalChartItem
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.PieChartItem
import uz.perfectalgorithm.projects.tezkor.databinding.ViewHolderBarChartBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ViewHolderGoalChartBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ViewHolderPieChartBinding
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.*


/**
 *Created by farrukh_kh on 9/23/21 9:29 AM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard
 **/
class DashboardChartAdapter :
    ListAdapter<DashboardChartItem, DashboardChartAdapter.BaseVH<out DashboardChartItem>>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> PieChartVH(
            ViewHolderPieChartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        1 -> BarChartVH(
            ViewHolderBarChartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        2 -> GoalChartVH(
            ViewHolderGoalChartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else -> throw IllegalArgumentException("There are only 3 types")
    }

    override fun onBindViewHolder(holder: BaseVH<out DashboardChartItem>, position: Int) {
        when (holder) {
            is PieChartVH -> holder.bind(getItem(position) as PieChartItem)
            is BarChartVH -> holder.bind(getItem(position) as BarChartItem)
            is GoalChartVH -> holder.bind(getItem(position) as GoalChartItem)
            else -> throw IllegalArgumentException("There are only 3 types")
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type.id

    abstract inner class BaseVH<T : DashboardChartItem>(root: View) :
        RecyclerView.ViewHolder(root) {
        abstract fun bind(item: T)
    }

    inner class PieChartVH(private val binding: ViewHolderPieChartBinding) :
        BaseVH<PieChartItem>(binding.root) {

        override fun bind(item: PieChartItem) = with(binding) {
            tvTitle.text = item.title
            with(chart) {
                dragDecelerationFrictionCoef = 0.95f
                holeRadius = 65f
                setHoleColor(Color.TRANSPARENT)
                setCenterTextSize(20f)
                setCenterTextTypeface(context.getTypeface())
                setDrawEntryLabels(false)
                description.isEnabled = false
                legend.isEnabled = false
                setNoDataText(null)
//                marker = PieMarkerView(context, R.layout.marker_view_department)

                val dataSet = PieDataSet(item.data as MutableList<PieEntry>?, null)
                dataSet.setDrawIcons(false)
                dataSet.setDrawValues(false)
                dataSet.selectionShift = 5f
                dataSet.sliceSpace = 3f
                dataSet.colors = getColors(root.context)

                var all = 0
                item.data.forEach {
                    all += it.value.toInt()
                }

                val pieData = PieData(dataSet).apply { setValueTypeface(context.getTypeface()) }

                rvLegend.adapter = LegendAdapter(item.data)
                rvLegend.scheduleLayoutAnimation()
                data = pieData
                centerText = "$all"
                highlightValues(null)
                animateXY(1000, 1000, Easing.EaseInOutQuad)
            }
        }
    }

    inner class BarChartVH(private val binding: ViewHolderBarChartBinding) :
        BaseVH<BarChartItem>(binding.root) {

        override fun bind(item: BarChartItem) = with(binding) {
            tvTitle.text = item.title
            with(chart) {
                isDoubleTapToZoomEnabled = false
                description.isEnabled = false
                legend.isEnabled = false
                renderer = RoundedBarChartRenderer(this, animator, viewPortHandler, 12f)
                setDrawGridBackground(false)
                setPinchZoom(true)
                marker = DepartmentMarkerView(
                    item.titleList,
                    root.context,
                    R.layout.marker_view_department
                )

                val yAxisFormatter = StaffCountAxisValueFormatter()
                val xAxisFormatter = DepartmentAxisValueFormatter(item.titleList)

                axisRight.isEnabled = false

                axisLeft.apply {
                    typeface = context.getTypeface()
                    enableGridDashedLine(12f, 8f, 0f)
                    setDrawAxisLine(false)
                    valueFormatter = yAxisFormatter
                    setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                    spaceTop = 15f
                    axisMinimum = 0f
                }

                xAxis.apply {
                    typeface = context.getTypeface()
                    setDrawGridLines(false)
                    valueFormatter = xAxisFormatter
                    position = XAxisPosition.BOTTOM
                    granularity = 1f
                    isGranularityEnabled = true
                    setXAxisRenderer(
                        DepartmentAxisRenderer(
                            viewPortHandler,
                            this,
                            getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )
                }

                val dataSet = BarDataSet(item.data as MutableList<BarEntry>?, null).apply {
                    setDrawIcons(false)
                    colors = getColors(root.context)
                }

                val barData = BarData(dataSet).apply {
                    setValueTypeface(context.getTypeface())
                    barWidth = 0.5f
                    setValueFormatter(StaffCountAxisValueFormatter())
                }

                data = barData
                setDrawGridBackground(false)
                animateY(1000, Easing.EaseInOutQuad)
                setVisibleXRangeMaximum(4f)
                extraBottomOffset = xAxis.textSize * (item.getTitleLinesCount())
//                invalidate()
            }
        }
    }

    inner class GoalChartVH(private val binding: ViewHolderGoalChartBinding) :
        BaseVH<GoalChartItem>(binding.root) {

        override fun bind(item: GoalChartItem) = with(binding) {
            tvTitle.text = item.title
            if (item.data.isNotEmpty()) {
                arcProgressView.setValue(item.data[0].toInt())
                tvValue.text = "${item.data[0].toInt()}%"
            }
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<DashboardChartItem>() {
            override fun areItemsTheSame(
                oldItem: DashboardChartItem,
                newItem: DashboardChartItem
            ) = oldItem.id == newItem.id && oldItem.type == newItem.type

            override fun areContentsTheSame(
                oldItem: DashboardChartItem,
                newItem: DashboardChartItem
            ) = oldItem.data == newItem.data && oldItem.data.toTypedArray()
                .contentDeepEquals(newItem.data.toTypedArray())
        }
    }
}