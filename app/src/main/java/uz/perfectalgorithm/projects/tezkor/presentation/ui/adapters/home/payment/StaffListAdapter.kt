package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.beppi.tristatetogglebutton_library.TriStateToggleButton
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.StaffItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStaffBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.ThreeBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone

/**
 * Created by Jasurbek Kurganbaev on 8/22/2021 11:40 PM
 **/
class StaffListAdapter : ListAdapter<StaffItem, StaffListAdapter.VH>(ITEM_STAFF_CALLBACK) {

    private var userClickActiveListener: ThreeBlock<StaffItem, Boolean, TriStateToggleButton>? =
        null


    companion object {
        var ITEM_STAFF_CALLBACK = object : DiffUtil.ItemCallback<StaffItem>() {
            override fun areItemsTheSame(oldItem: StaffItem, newItem: StaffItem): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: StaffItem, newItem: StaffItem): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.isActive == oldItem.isActive &&
                        newItem.firstName == oldItem.firstName &&
                        newItem.lastName == oldItem.lastName

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    fun setItemToggleListener(block: ThreeBlock<StaffItem, Boolean, TriStateToggleButton>?) {
        userClickActiveListener = block
    }

    inner class VH(private val binding: ItemStaffBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                if (data.role == "owner") {
                    tstb1.gone()
                    ownerTitle.show()
                }
                if (data.image != null) {
                    staffAvatar.loadImageUrl(data.image)
                }
                if (data.isActive!!) {
                    tstb1.setToggleOn()
                } else {
                    tstb1.setToggleOff()
                }

                tstb1.setOnToggleChanged { toggleStatus, booleanToggleStatus, toggleIntValue ->
                    Log.d("TOGGLE", "TOGGLE STATUS $booleanToggleStatus")
                    userClickActiveListener?.invoke(data, booleanToggleStatus, tstb1)
                }
                fullName.text = "${data.firstName} ${data.lastName}"
            }
        }

    }


}