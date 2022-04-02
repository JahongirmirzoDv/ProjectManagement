package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureWorkerBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 * Created by Davronbek Raximjanov on 29.06.2021
 **/

class StructureLeaderAdapter(private val positionsItem: StructureResponse.PositionsItem) :
    ListAdapter<AllWorkersResponse.DataItem, StructureLeaderAdapter.VH>(DIFF_CHANNEL_CALLBACK) {

    private var listenerCallback: SingleBlock<AllWorkersResponse.DataItem>? = null

    private var listenerMenuItem: DoubleBlock<AllWorkersResponse.DataItem, Int>? = null

    companion object {
        var DIFF_CHANNEL_CALLBACK =
            object : DiffUtil.ItemCallback<AllWorkersResponse.DataItem>() {
                override fun areItemsTheSame(
                    oldItem: AllWorkersResponse.DataItem,
                    newItem: AllWorkersResponse.DataItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: AllWorkersResponse.DataItem,
                    newItem: AllWorkersResponse.DataItem
                ): Boolean {
                    return newItem.firstName == oldItem.firstName
                }
            }
    }

    inner class VH(val binding: ItemStructureWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            listenerCallback?.invoke(getItem(absoluteAdapterPosition))
        }

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtFullName.text = data.contact?.firstName + " " + data.contact?.lastName

                txtPosition.text = positionsItem.title
                data.image?.let { imgProfile.loadImageUrl(it) }

                itemView.apply {
                    btnMore.setOnClickListener { it ->
                        val menu = PopupMenu(context, it)
                        menu.inflate(R.menu.worker_more_options_menu)
                        menu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {

                                R.id.detail_op_staff -> listenerMenuItem?.invoke(
                                    getItem(
                                        absoluteAdapterPosition
                                    ), 0
                                )
                                R.id.edit_op_staff -> listenerMenuItem?.invoke(
                                    getItem(
                                        absoluteAdapterPosition
                                    ), 1
                                )
                                R.id.delete_op_staff -> listenerMenuItem?.invoke(
                                    getItem(
                                        absoluteAdapterPosition
                                    ), 2
                                )
                            }
                            true
                        }
                        menu.show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStructureWorkerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }


    fun setOnClickListener(block: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerCallback = block
    }

    fun setOnClickMenuListener(f: DoubleBlock<AllWorkersResponse.DataItem, Int>) {
        listenerMenuItem = f
    }


}