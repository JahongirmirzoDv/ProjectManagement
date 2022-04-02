package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStatusBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

/**
 *Created by farrukh_kh on 8/12/21 2:10 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.adding
 **/
class StatusAdapter(
    private val items: List<StatusData>,
    private val onItemClick: SingleBlock<StatusData>
) : RecyclerView.Adapter<StatusAdapter.VH>() {

    inner class VH(private val binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StatusData) = with(binding) {
            tvTitle.text = item.title
            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}