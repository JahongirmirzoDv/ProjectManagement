package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person

/*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemSearchPersonSelectBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTeamWorkersBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

*/
/**
 * Created by Raximjanov Davronbek on 28.06.2021
 **//*


class SearchSelectWorkersAdapter :
    ListAdapter<AllWorkersResponse.DataItem, SearchSelectWorkersAdapter.VH>(DIFF_TEAM_CALLBACK) {

    private var lastCapLetter = ';'
    private val storage by lazy { LocalStorage.instance }


    companion object {
        var DIFF_TEAM_CALLBACK = object : DiffUtil.ItemCallback<AllWorkersResponse.DataItem>() {
            override fun areItemsTheSame(
                oldItem: AllWorkersResponse.DataItem,
                newItem: AllWorkersResponse.DataItem
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: AllWorkersResponse.DataItem,
                newItem: AllWorkersResponse.DataItem
            ): Boolean {
                return newItem.firstName == oldItem.firstName && newItem.birthDate == oldItem.birthDate
            }
        }
    }

    inner class VH(val binding: ItemSearchPersonSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtFullName.text = data.firstName + " " + data.lastName

                var a = ';'

                if (!data.firstName.isNullOrBlank()) {
                    a = data.firstName.trim()[0].toLowerCase()
                }

                if (a != lastCapLetter.toLowerCase()) {
                    lastCapLetter = a
                    tvTitle.text = a.toString().toUpperCase()
                    tvTitle.show()
                } else {
                    tvTitle.gone()
                }

                txtFullName.text = data.getFullName()

                data.image?.let { imgPerson.loadImageUrl(it) }

                imgChecked.setImageResource(
                    if (storage.persons.contains(data.id.toString()))
                        R.drawable.custom_checkbox_checked else R.drawable.custom_checkbox_unchecked
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemSearchPersonSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }


}*/
