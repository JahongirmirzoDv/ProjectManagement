package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItemData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemPackageListBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 8/27/2021 6:25 PM
 **/
class PackageAdapter : ListAdapter<PackageItemData, PackageAdapter.VH>(PACKAGE_ITEM_DATA_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPackageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    companion object {
        var PACKAGE_ITEM_DATA_CALLBACK = object : DiffUtil.ItemCallback<PackageItemData>() {
            override fun areItemsTheSame(
                oldItem: PackageItemData,
                newItem: PackageItemData
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: PackageItemData,
                newItem: PackageItemData
            ): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.startDate == oldItem.startDate &&
                        newItem.expireDate == oldItem.expireDate &&
                        newItem.isDemo == oldItem.isDemo &&
                        newItem.price == oldItem.price &&
                        newItem.next == oldItem.next &&
                        newItem.staffLimit == oldItem.staffLimit
            }

        }
    }

    inner class VH(private val binding: ItemPackageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)


            binding.apply {

                val startDate =
                    data?.startDate?.replace("-", ".")?.split("T")?.get(0)

                val expireDate =
                    data?.expireDate?.replace("-", ".")?.split("T")?.get(0)

                unlimProDeadline.text = resources.getString(
                    R.string.unlim_pro_package_deadline,
                    startDate,
                    expireDate
                )
                unlimProStaffLimit.text =
                    resources.getString(R.string.limit_staffs, data.staffLimit.toString())
            }
        }
    }

}
