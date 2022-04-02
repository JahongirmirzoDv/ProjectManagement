package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.offers.addOffers

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemAddComplaintUserBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder


class ComplaintUserAdapter(
    private val context: Context,
    private val listener: ComplaintItemClickListener
) :
    ListAdapter<PersonData, ComplaintUserAdapter.VH>(
        diffUtil
    ) {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemAddComplaintUserBinding.inflate(inflater, parent, false))


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = currentList[position]
        holder.itemUserBinding.tvUsername.text = itemData.fullName

        holder.itemUserBinding.btnRemove.setOnClickListener {
            listener.itemRemoveClick(itemData)
        }
        holder.itemUserBinding.ivUser.loadImageUrlWithPlaceholder(
            "${itemData.image}",
            R.drawable.ic_baseline_account_circle_24
        )
        if (itemData.position != null && itemData.position.isNotEmpty())
            holder.itemUserBinding.tvPosition.text = itemData.position[0]?.title

    }

    fun addItem(dataItem: PersonData) {
        val newList = ArrayList<PersonData>()
        newList.addAll(currentList)
        newList.add(dataItem)
        submitList(newList)
    }

    fun removeItem(dataItem: PersonData) {
        val newList = ArrayList<PersonData>()
        newList.addAll(currentList)
        newList.remove(dataItem)
        submitList(newList)
    }

    class VH(val itemUserBinding: ItemAddComplaintUserBinding) :
        RecyclerView.ViewHolder(itemUserBinding.root)
}

interface ComplaintItemClickListener {
    fun itemRemoveClick(dataItem: PersonData)
}

private val diffUtil = object : DiffUtil.ItemCallback<PersonData>() {
    override fun areItemsTheSame(
        oldItem: PersonData,
        newItem: PersonData,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: PersonData,
        newItem: PersonData,
    ): Boolean = newItem == oldItem

}