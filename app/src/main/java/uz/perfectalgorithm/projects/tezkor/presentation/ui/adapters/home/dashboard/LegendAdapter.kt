package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieEntry
import uz.perfectalgorithm.projects.tezkor.databinding.ItemLegendBinding
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.getDrawable

/**
 *Created by farrukh_kh on 7/13/21 3:48 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.dashboard
 **/
class LegendAdapter(private val items: List<PieEntry>) :
    RecyclerView.Adapter<LegendAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemLegendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class VH(private val binding: ItemLegendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PieEntry) = with(binding) {
            txvTitle.text = item.label
            txvValue.text = "${item.value.toInt()}"
            imvIcon.setImageDrawable(
                getDrawable(
                    root.context,
                    absoluteAdapterPosition
                )
            )
        }
    }
}