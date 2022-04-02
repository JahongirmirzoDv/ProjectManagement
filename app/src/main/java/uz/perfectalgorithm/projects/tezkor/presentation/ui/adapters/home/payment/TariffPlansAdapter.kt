package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TariffListItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTariffPlanBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 8/27/2021 2:54 PM
 **/
class TariffPlansAdapter :
    ListAdapter<TariffListItem, TariffPlansAdapter.VH>(ITEM_TARIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemTariffPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()


    companion object {
        var ITEM_TARIFF_CALLBACK = object : DiffUtil.ItemCallback<TariffListItem>() {
            override fun areItemsTheSame(
                oldItem: TariffListItem,
                newItem: TariffListItem
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: TariffListItem,
                newItem: TariffListItem
            ): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.isPinned == oldItem.isPinned &&
                        newItem.companyPricePerMonth == oldItem.companyPricePerMonth &&
                        newItem.minStaffs == oldItem.minStaffs &&
                        newItem.userPricePerMonth == oldItem.userPricePerMonth
            }

        }
    }


    inner class VH(private val binding: ItemTariffPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                if (data.isChanged) {
                    cvName.strokeColor = resources.getColor(R.color.project_done)
                    perMoney.setTextColor(resources.getColor(R.color.project_done))
                    monthTitle.setTextColor(resources.getColor(R.color.project_done))
                } else {
                    cvName.strokeColor = resources.getColor(R.color.white)
                    perMoney.setTextColor(resources.getColor(R.color.colorDarkBlue))
                    monthTitle.setTextColor(resources.getColor(R.color.colorDarkBlue))

                }
                tariffDefinition.text =
                    resources.getString(R.string.tariff_definition, data.minStaffs)
                perMoney.text =
                    resources.getString(R.string.per_cost, data.userPricePerMonth!! * 3)
            }
        }
    }

    fun checkedToFalse(position: Int) {
        if (currentList.isNotEmpty()) {
            currentList.forEach {
                it.isChanged = false
            }
            currentList[position].isChanged = true
        }
    }


}