package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.contact_structure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemContactStructureBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Kurganbaev Jasurbek on 22.06.2021
 **/

class ContactStructureSectionAdapter(/*private val onWorkerClick: SingleBlock<AllWorkersResponse.DataItem>*/) :
    ListAdapter<StructureResponse.DataItem, ContactStructureSectionAdapter.VH>(DIFF_CHANNEL_CALLBACK) {

    private var listenerCallback: DoubleBlock<StructureResponse.DataItem, Boolean>? = null


    companion object {
        var DIFF_CHANNEL_CALLBACK =
            object : DiffUtil.ItemCallback<StructureResponse.DataItem>() {
                override fun areItemsTheSame(
                    oldItem: StructureResponse.DataItem,
                    newItem: StructureResponse.DataItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: StructureResponse.DataItem,
                    newItem: StructureResponse.DataItem
                ): Boolean {
                    return newItem.title == oldItem.title
                }
            }
    }

    inner class VH(val binding: ItemContactStructureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var workerAdapter: ContactStructureWorkerAdapter
        private lateinit var leaderAdapter: ContactStructureLeaderAdapter
        private lateinit var sectionAdapter: ContactStructureSectionAdapter


        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)

                rvLeaders.layoutManager = LinearLayoutManager(App.instance)
                rvWorkers.layoutManager = LinearLayoutManager(App.instance)
                rvSection.layoutManager = LinearLayoutManager(App.instance)

                if (data.positions != null) {
                    for (a in data.positions) {
                        if (a != null) {
                            if (a.hierarchy == HierarchyPositionsEnum.LEADER.text) {
                                leaderAdapter = ContactStructureLeaderAdapter(a)
                                leaderAdapter.submitList(a.staffs)
                                rvLeaders.adapter = leaderAdapter

                            } else {
                                workerAdapter = ContactStructureWorkerAdapter(a/*, onWorkerClick*/)
                                workerAdapter.submitList(a.staffs)
                                rvWorkers.adapter = workerAdapter
                            }
                        }
                    }
                }

                sectionAdapter = ContactStructureSectionAdapter(/*onWorkerClick*/)
                sectionAdapter.submitList(data.children!!)
                rvSection.adapter = sectionAdapter


                txtSectionTitle.text = data.title

                rootLayout.setOnClickListener {
                    setVisibility()
                }
                imgDropDown.setOnClickListener {
                    setVisibility()
                }

            }
        }

        private fun setVisibility() {
            val boolVisibility =
                binding.rvSection.visibility == View.VISIBLE

            if (boolVisibility) {
                binding.rvSection.gone()
                binding.rvLeaders.gone()
                binding.rvWorkers.gone()
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_up
                    )
                )
            } else {
                binding.rvSection.visible()
                binding.rvLeaders.visible()
                binding.rvWorkers.visible()
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_down
                    )
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemContactStructureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(f: DoubleBlock<StructureResponse.DataItem, Boolean>) {
        listenerCallback = f
    }


}