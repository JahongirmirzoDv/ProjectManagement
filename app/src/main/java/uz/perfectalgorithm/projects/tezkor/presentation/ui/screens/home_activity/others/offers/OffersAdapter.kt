package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.offers

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOfferResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemOffersVsComplaintsBinding
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

class OffersAdapter(
    private val context: Context,
    private val listenerOffer: OfferItemClickListener
) :
    androidx.recyclerview.widget.ListAdapter<BaseOfferResponse.DataResult, OffersAdapter.VH>(
        diffUtil
    ) {

    val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemOffersVsComplaintsBinding.inflate(inflater, parent, false))


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = currentList[position]
        if (itemData.type == App.instance.getString(R.string.offer)) {
            holder.itemOffersBinding.tvId.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue
                )
            )
        } else {
            holder.itemOffersBinding.tvId.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.count_text_color_red
                )
            )
        }
        when (itemData.status) {
            "new" -> {
                holder.itemOffersBinding.ivSatisfied.setImageResource(R.drawable.ic_new)
            }
            "in_progress" -> {
                holder.itemOffersBinding.ivSatisfied.setImageResource(R.drawable.ic_play_circle)
            }
            "finished" -> {
                holder.itemOffersBinding.ivSatisfied.setImageResource(R.drawable.ic_satisfied)
            }
        }
        holder.itemOffersBinding.tvId.text = "${itemData.type} #${itemData.id}"
        holder.itemOffersBinding.tvDescription.text = itemData.description
        holder.itemOffersBinding.tvDate.text = DateUtil.convertDate(itemData.createdAt)
        holder.itemOffersBinding.tvCountFile.text = itemData.getFiles.size.toString()
        holder.itemOffersBinding.tvUser.text =
            "${itemData.creator.lastName} ${itemData.creator.firstName}"

        holder.itemOffersBinding.mainContainer.setOnClickListener {
            listenerOffer.itemClick(itemData.id, itemData.type)
        }
    }

    class VH(val itemOffersBinding: ItemOffersVsComplaintsBinding) :
        RecyclerView.ViewHolder(itemOffersBinding.root)
}

interface OfferItemClickListener {
    fun itemClick(offerId: Int, type: String)
}

private val diffUtil = object : DiffUtil.ItemCallback<BaseOfferResponse.DataResult>() {
    override fun areItemsTheSame(
        oldItem: BaseOfferResponse.DataResult,
        newItem: BaseOfferResponse.DataResult,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: BaseOfferResponse.DataResult,
        newItem: BaseOfferResponse.DataResult,
    ): Boolean = oldItem == newItem

}