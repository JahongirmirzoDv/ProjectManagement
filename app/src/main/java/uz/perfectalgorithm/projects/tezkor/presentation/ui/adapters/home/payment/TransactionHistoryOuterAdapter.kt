package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TransactionListItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemPaymentBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 8/23/2021 4:35 PM
 **/

class TransactionHistoryOuterAdapter :
    ListAdapter<TransactionListItem, TransactionHistoryOuterAdapter.VH>(
        ITEM_TRANSACTION_HISTORY_CALLBACK
    ) {


    companion object {
        var ITEM_TRANSACTION_HISTORY_CALLBACK =
            object : DiffUtil.ItemCallback<TransactionListItem>() {
                override fun areItemsTheSame(
                    oldItem: TransactionListItem,
                    newItem: TransactionListItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: TransactionListItem,
                    newItem: TransactionListItem
                ): Boolean {
                    return newItem.id == oldItem.id &&
                            newItem.isPurchase == oldItem.isPurchase &&
                            newItem.status == oldItem.status &&
                            newItem.status == oldItem.status &&
                            newItem.amount == oldItem.amount &&
                            newItem.amount == oldItem.amount &&
                            newItem.createdAt == oldItem.createdAt
                }

            }
    }


    inner class VH(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                if (data.isPurchase!!) {
                    img.setBackgroundResource(R.drawable.ic_output)
                    tvSum.setTextColor(resources.getColor(R.color.project_status_red_color))
                    tvAbout.text = "Paket sotib olingan to'ldirilgan"
                } else {
                    img.setBackgroundResource(R.drawable.ic_input)
                    tvSum.setTextColor(resources.getColor(R.color.green))
                    tvAbout.text = "Balans to'ldirilgan"
                }

                tvSum.text = "$".plus("%.2f".format(data.amount))
                tvTransactionTime.text = data.createdAt
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()
}