package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.tactic_plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanStatus
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanYear
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTacticPlanYearBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock


class YearAdapter(
    private val onTacticPlanClick: SingleBlock<Int>,
    private val onAddTacticPlanClick: (Int, Int, Status) -> Unit,
    private val changeItemStatus: (TacticPlan, TacticPlanStatus) -> Unit
) : ListAdapter<TacticPlanYear, YearAdapter.VH>(ITEM_CALLBACK) {

    private var openedYear: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemTacticPlanYearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.rlMainLayout.setOnClickListener {
            holder.bind(getItem(position), false)
        }
        holder.binding.imgDropDown.setOnClickListener {
            holder.bind(getItem(position), false)
        }
        holder.bind(getItem(position), true)
    }

    inner class VH(val binding: ItemTacticPlanYearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val monthsAdapter by lazy {
            MonthAdapter(
                onTacticPlanClick,
                onAddTacticPlanClick,
                changeItemStatus
            )
        }

        init {
            with(binding.rvMonths) {
                adapter = monthsAdapter
            }
        }

        fun bind(year: TacticPlanYear, isFirstLaunch: Boolean) = with(binding) {
            tvTitle.text = year.title
            monthsAdapter.yearId = year.id

            when {
                isFirstLaunch && openedYear == year.id -> {
                    rvMonths.isVisible = true
                    imgDropDown.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.ic_chevron_down
                        )
                    )
                    monthsAdapter.submitList(year.months)
                }
                isFirstLaunch && openedYear != year.id -> {
                    rvMonths.isVisible = false
                    imgDropDown.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.ic_chevron_up
                        )
                    )
                }
                !isFirstLaunch && !rvMonths.isVisible -> {
                    rvMonths.isVisible = true
                    openedYear = year.id
                    imgDropDown.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.ic_chevron_down
                        )
                    )
                    monthsAdapter.submitList(year.months)
                }
                else -> {
                    rvMonths.isVisible = false
                    openedYear = null
                    imgDropDown.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.ic_chevron_up
                        )
                    )
                }
            }
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<TacticPlanYear>() {
            override fun areItemsTheSame(
                oldItem: TacticPlanYear,
                newItem: TacticPlanYear
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TacticPlanYear,
                newItem: TacticPlanYear
            ) = oldItem.months.toTypedArray().contentDeepEquals(newItem.months.toTypedArray())

            override fun getChangePayload(
                oldItem: TacticPlanYear,
                newItem: TacticPlanYear
            ) = false
        }
    }
}