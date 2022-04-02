package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemSelectedPersonBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 *Created by farrukh_kh on 10/22/21 11:44 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update
 **/
class SelectedPersonAdapter : ListAdapter<PersonData, SelectedPersonAdapter.VH>(ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemSelectedPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemSelectedPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: PersonData) = with(binding) {
            tvName.text = person.fullName
            if (person.image != null) {
                ivAvatar.loadImageUrl(person.image)
            } else {
                ivAvatar.setImageResource(R.drawable.ic_user)
            }
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<PersonData>() {
            override fun areItemsTheSame(oldItem: PersonData, newItem: PersonData) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PersonData, newItem: PersonData) =
                oldItem == newItem
        }
    }
}