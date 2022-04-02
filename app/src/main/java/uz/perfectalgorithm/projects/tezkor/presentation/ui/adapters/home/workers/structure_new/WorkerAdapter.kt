package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure_new

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemWorkerBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 *Created by farrukh_kh on 11/1/21 8:32 AM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure_new
 **/
class WorkerAdapter(
    private val positionType: HierarchyPositionsEnum,
    private val onMenuClick: DoubleBlock<AllWorkersShort, Int>,
    private val onClick: DoubleBlock<AllWorkersShort, Int>
) : RecyclerView.Adapter<WorkerAdapter.VH>() {

    private var list = ArrayList<AllWorkersShort>()
    var icsPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position])
    }

    inner class VH(val binding: ItemWorkerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(worker: AllWorkersShort) = with(binding) {
            txtFullName.text = worker.fullName
            txtPosition.text = worker.getPositionsTitle(positionType)
            if (worker.image != null) {
                imgProfile.loadImageUrl(worker.image)
            } else {
                imgProfile.setImageResource(R.drawable.ic_user)
            }

            val item = list[absoluteAdapterPosition]

            if (item.isChecked) {
                ll1.visibility = View.VISIBLE
            } else {
                ll1.visibility = View.GONE
            }

            bigCons.setOnClickListener{
                if (item.isChecked) {
                    ll1.visibility = View.GONE
                    item.isChecked = false
                } else {
                    ll1.visibility = View.VISIBLE
                    item.isChecked = true
                }
            }

            if (item.isFavourite == true) {
                imgAddFavourites.setImageResource(R.drawable.ic_saved_contact)
            } else {
                imgAddFavourites.setImageResource(R.drawable.ic_star_grey_sd)
            }

            btnAddFavourites.setOnClickListener {
                icsPosition = absoluteAdapterPosition
                if (item.isFavourite == true) {
                    imgAddFavourites.setImageResource(R.drawable.ic_star_grey_sd)
                    item.isFavourite = false
                } else {
                    imgAddFavourites.setImageResource(R.drawable.ic_saved_contact)
                    item.isFavourite = true
                }
                onClick.invoke(item, 0)
            }

            btnPersonal.setOnClickListener {
                onClick.invoke(item, 1)
            }

            btnEditPersonal.setOnClickListener {
                onClick.invoke(item, 2)
            }

            btnGoCalendar.setOnClickListener {
                onClick.invoke(item, 3)
            }

            btnPhone.setOnClickListener {
                onClick.invoke(item, 4)
            }

            btnMore.setOnClickListener { it ->
                val menu = PopupMenu(root.context, it)
                menu.inflate(R.menu.worker_more_options_menu)
                menu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.detail_op_staff -> onMenuClick.invoke(
                            item, 0
                        )
                        R.id.edit_op_staff -> onMenuClick.invoke(
                            item, 1
                        )
                        R.id.delete_op_staff -> onMenuClick.invoke(
                            item, 2
                        )
                    }
                    true
                }
                menu.show()
            }
        }
    }

    fun submitList(listW: ArrayList<AllWorkersShort>) {
        if (!listW.isNullOrEmpty()) {
            list.clear()
            list.addAll(listW)
            notifyDataSetChanged()
        }
    }

    fun setUIAddFavourite(workerData: AllWorkersShort) {
            Log.d("DepartmentAllWorker", "changeUI: Change1")
        if (icsPosition > -1) {
            Log.d("DepartmentAllWorker", "changeUI: Change2")
            list[icsPosition].isFavourite = workerData.isFavourite
            notifyItemChanged(icsPosition)
            icsPosition = -1
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

    override fun getItemCount(): Int = list.size
}