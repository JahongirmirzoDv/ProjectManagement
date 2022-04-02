package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DashboardCountItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemDashboardCountBinding

/**
 *Created by farrukh_kh on 9/22/21 9:29 AM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard
 **/
class DashboardCountAdapter : ListAdapter<DashboardCountItem, DashboardCountAdapter.VH>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemDashboardCountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemDashboardCountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashboardCountItem) = with(binding) {
            tvTitle.text = item.title
            tvValue.text = item.count.toString()
            tvTitle.isSelected = true
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<DashboardCountItem>() {
            override fun areItemsTheSame(
                oldItem: DashboardCountItem,
                newItem: DashboardCountItem
            ) = oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: DashboardCountItem,
                newItem: DashboardCountItem
            ) = oldItem.count == newItem.count
        }
    }
}

