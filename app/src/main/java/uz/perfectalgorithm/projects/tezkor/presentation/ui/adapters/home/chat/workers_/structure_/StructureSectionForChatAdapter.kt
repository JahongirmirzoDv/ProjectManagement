package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_.structure_

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureForChatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Davronbek Raximjanov on 20.08.2021
 **/

class StructureSectionForChatAdapter :
    RecyclerView.Adapter<StructureSectionForChatAdapter.VH>() {

    private var myList: ArrayList<StructureResponse.DataItem> = ArrayList()

    private var listenClick: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerMenuItem: DoubleBlock<AllWorkersResponse.DataItem, Int>? = null

    inner class VH(val binding: ItemStructureForChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var workerAdapter: StructureWorkerForChatAdapter
        private lateinit var leaderAdapter: StructureLeaderForChatAdapter
        private lateinit var sectionAdapter: StructureSectionForChatAdapter

        fun bind() {
            binding.apply {
                val data = myList[adapterPosition]

                rvLeaders.layoutManager = LinearLayoutManager(App.instance)
                rvWorkers.layoutManager = LinearLayoutManager(App.instance)
                rvSection.layoutManager = LinearLayoutManager(App.instance)

                if (data.positions != null) {
                    for (a in data.positions) {
                        if (a != null) {
                            if (a.hierarchy == HierarchyPositionsEnum.LEADER.text) {
                                leaderAdapter = StructureLeaderForChatAdapter(a)
                                leaderAdapter.submitList(a.staffs)
                                rvLeaders.adapter = leaderAdapter
                                leaderAdapter.setOnClickListener { leaderData ->
                                    listenClick?.invoke(leaderData)
                                }

                            } else {
                                workerAdapter = StructureWorkerForChatAdapter(a)
                                workerAdapter.submitList(a.staffs)
                                rvWorkers.adapter = workerAdapter
                                workerAdapter.setOnClickListener { leaderData ->
                                    listenClick?.invoke(leaderData)
                                }
                            }
                        }
                    }
                }

                sectionAdapter = StructureSectionForChatAdapter()
                if (!data.children.isNullOrEmpty()) {
                    sectionAdapter?.submitList(data.children!!)
                    rvSection.adapter = sectionAdapter
                    sectionAdapter.setOnClickListener { workerData ->
                        listenClick?.invoke(workerData)
                    }
                }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemStructureForChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    override fun getItemCount(): Int = myList.size

    fun submitList(list: List<StructureResponse.DataItem>?) {
        if (!list.isNullOrEmpty()) {
            myList.clear()
            myList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun setOnClickListener(f: SingleBlock<AllWorkersResponse.DataItem>) {
        listenClick = f
    }
}