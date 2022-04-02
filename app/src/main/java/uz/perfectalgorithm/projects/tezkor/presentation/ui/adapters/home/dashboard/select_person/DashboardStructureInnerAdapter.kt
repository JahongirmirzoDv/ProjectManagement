package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureSelectWorkerBinding
import uz.perfectalgorithm.projects.tezkor.utils.adding.StructureSelectDashboardListener
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 *Created by farrukh_kh on 10/1/21 11:56 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person
 **/
class DashboardStructureInnerAdapter(
    private val positionType: HierarchyPositionsEnum,
    private val clickListener: StructureSelectDashboardListener
) : ListAdapter<AllWorkersResponse.DataItem, DashboardStructureInnerAdapter.VH>(ITEM_CALLBACK) {

    private val storage by lazy { LocalStorage.instance }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStructureSelectWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(val binding: ItemStructureSelectWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(worker: AllWorkersResponse.DataItem) = with(binding) {
            root.setOnClickListener {
                clickListener.onWorkerClick(worker)
            }
            tvFullName.text = worker.getFullName()
            tvPositions.text = worker.getPositionsTitle(positionType)
            if (worker.image != null) {
                ivProfile.loadImageUrl(worker.image)
            } else {
                ivProfile.setImageResource(R.drawable.ic_user)
            }
            ivChecked.setImageResource(
                if (storage.persons.contains(worker.id.toString()))
                    R.drawable.custom_checkbox_checked else R.drawable.custom_checkbox_unchecked
            )
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<AllWorkersResponse.DataItem>() {
            override fun areItemsTheSame(
                oldItem: AllWorkersResponse.DataItem,
                newItem: AllWorkersResponse.DataItem
            ) = newItem.id == oldItem.id

            override fun areContentsTheSame(
                oldItem: AllWorkersResponse.DataItem,
                newItem: AllWorkersResponse.DataItem
            ) = newItem.firstName == oldItem.firstName
        }
    }
}