package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Davronbek Raximjanov on 22.06.2021
 **/

class StructureSectionAdapter :
    RecyclerView.Adapter<StructureSectionAdapter.VH>() {

    private var myList: ArrayList<StructureResponse.DataItem> = ArrayList()

    private var listenerCallback: DoubleBlock<StructureResponse.DataItem, Boolean>? = null
    private var deleteCallback: SingleBlock<Int>? = null
    private var updateCallback: DoubleBlock<String, Int>? = null
    private var listenerMenuItem: DoubleBlock<AllWorkersResponse.DataItem, Int>? = null

    inner class VH(val binding: ItemStructureBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var workerAdapter: StructureWorkerAdapter
        private lateinit var leaderAdapter: StructureLeaderAdapter
        private lateinit var sectionAdapter: StructureSectionAdapter

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
                                leaderAdapter = StructureLeaderAdapter(a)
                                leaderAdapter.submitList(a.staffs)
                                rvLeaders.adapter = leaderAdapter
                                leaderAdapter.setOnClickMenuListener { dataItem, position ->
                                    listenerMenuItem?.invoke(dataItem, position)
                                }

                            } else {
                                workerAdapter = StructureWorkerAdapter(a)
                                workerAdapter.submitList(a.staffs)
                                rvWorkers.adapter = workerAdapter
                                workerAdapter.setOnClickMenuListener { dataItem, position ->
                                    listenerMenuItem?.invoke(dataItem, position)
                                }
                            }
                        }
                    }
                }
                sectionAdapter = StructureSectionAdapter()
                if (!data.children.isNullOrEmpty()) {
                    sectionAdapter.submitList(data.children)
                    rvSection.adapter = sectionAdapter
                    sectionAdapter.setOnClickMenuListener { dataItem, position ->
                        listenerMenuItem?.invoke(dataItem, position)
                    }
                    sectionAdapter.deleteClickListener { id ->
                        deleteCallback?.invoke(id)
                    }
                    sectionAdapter.updateClickListener { title, id ->
                        updateCallback?.invoke(title, id)
                    }
                }

                txtSectionTitle.text = data.title

                rootLayout.setOnClickListener {
                    setVisibility()
                }
                rootLayout.setOnLongClickListener {
                    setChangeVisibility()
                    return@setOnLongClickListener true
                }

                ivDelete.setOnClickListener {
                    data.id?.let { it1 -> deleteCallback?.invoke(it1) }
                }


                ivEdit.setOnClickListener {
                    data.id?.let { it1 -> updateCallback?.invoke(data.title ?: "", it1) }
                }

                imgDropDown.setOnClickListener {
                    setVisibility()
                }

            }
        }

        private fun setChangeVisibility() {

            val boolVisibility = binding.ivDelete.isVisible
            timberLog(boolVisibility.toString())
            if (boolVisibility) {
                binding.ivDelete.gone()
                binding.ivEdit.gone()
            } else {
                binding.ivDelete.visible()
                binding.ivEdit.visible()
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
        val binding = ItemStructureBinding.inflate(
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

    fun setOnClickListener(f: DoubleBlock<StructureResponse.DataItem, Boolean>) {
        listenerCallback = f
    }

    fun deleteClickListener(f: SingleBlock<Int>) {
        deleteCallback = f
    }

    fun updateClickListener(f: DoubleBlock<String, Int>) {
        updateCallback = f
    }

    fun setOnClickMenuListener(f: DoubleBlock<AllWorkersResponse.DataItem, Int>) {
        listenerMenuItem = f
    }

}