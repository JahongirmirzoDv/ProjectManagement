package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureSelectWorkerBinding
import uz.perfectalgorithm.projects.tezkor.utils.adding.StructureSelectListener
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 *Created by farrukh_kh on 8/24/21 11:57 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.others.adding_helpers
 **/
class WorkerSelectAdapter(
    private val positionType: HierarchyPositionsEnum,
    private val clickListener: StructureSelectListener
) : ListAdapter<AllWorkersShort, WorkerSelectAdapter.VH>(ITEM_CALLBACK) {

    private val storage by lazy { LocalStorage.instance }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStructureSelectWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(val binding: ItemStructureSelectWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(worker: AllWorkersShort) = with(binding) {
            root.setOnClickListener {
                clickListener.onWorkerClick(worker)
            }
            tvFullName.text = worker.fullName
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
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<AllWorkersShort>() {
            override fun areItemsTheSame(
                oldItem: AllWorkersShort,
                newItem: AllWorkersShort
            ) = newItem.id == oldItem.id

            override fun areContentsTheSame(
                oldItem: AllWorkersShort,
                newItem: AllWorkersShort
            ) = newItem == oldItem
        }
    }
}