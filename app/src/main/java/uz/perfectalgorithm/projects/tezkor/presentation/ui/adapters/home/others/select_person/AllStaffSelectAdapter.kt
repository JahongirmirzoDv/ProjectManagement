package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemListSelectedBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone

/**
 *Created by farrukh_kh on 8/25/21 10:59 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.others.adding_helpers
 **/
class AllStaffSelectAdapter(private val onWorkerClick: SingleBlock<AllWorkersShort>) :
    ListAdapter<AllWorkersShort, AllStaffSelectAdapter.VH>(ITEM_CALLBACK) {

    private var lastCapLetter = '|'
    private val storage by lazy { LocalStorage.instance }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemListSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemListSelectedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onWorkerClick(getItem(absoluteAdapterPosition))
            }
        }

        fun bind(worker: AllWorkersShort) = with(binding) {

            var a = ';'

            if (!worker.fullName.isNullOrBlank()) {
                a = worker.fullName.trim()[0].toLowerCase()
            }

            if (a != lastCapLetter.toLowerCase()) {
                lastCapLetter = a
                tvTitle.text = a.toString().toUpperCase()
                tvTitle.show()
            } else {
                tvTitle.gone()
            }

            tvPersonName.text = worker.fullName
            if (worker.image != null) {
                imgAvatar.loadImageUrl(worker.image)
            } else {
                imgAvatar.setImageResource(R.drawable.ic_user)
            }
            imgChecked.setImageResource(
                if (storage.persons.contains(worker.id.toString()))
                    R.drawable.custom_checkbox_checked else R.drawable.custom_checkbox_unchecked
            )
        }
    }

    companion object {
        var ITEM_CALLBACK = object : DiffUtil.ItemCallback<AllWorkersShort>() {
            override fun areItemsTheSame(
                oldItem: AllWorkersShort,
                newItem: AllWorkersShort
            ) = newItem.id == oldItem.id

            override fun areContentsTheSame(
                oldItem: AllWorkersShort,
                newItem: AllWorkersShort
            ) = oldItem == newItem
        }
    }
}