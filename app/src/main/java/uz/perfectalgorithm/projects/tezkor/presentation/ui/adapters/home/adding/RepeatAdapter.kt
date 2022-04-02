package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemRepeatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

/**
 *Created by farrukh_kh on 8/10/21 3:02 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.adding
 **/
class RepeatAdapter(
    private val items: List<RepetitionData>,
    private val onItemClick: SingleBlock<RepetitionData>
) : RecyclerView.Adapter<RepeatAdapter.VH>() {

    inner class VH(private val binding: ItemRepeatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RepetitionData) = with(binding) {
            tvTitle.text = item.title
            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRepeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}