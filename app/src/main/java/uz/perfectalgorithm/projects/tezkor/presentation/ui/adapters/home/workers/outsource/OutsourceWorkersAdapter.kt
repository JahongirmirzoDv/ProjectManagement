package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.outsource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTeamWorkersBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder


/**
 * Created by Raximjanov Davronbek on 21.06.2021
 **/

class OutsourceWorkersAdapter :
    ListAdapter<AllWorkersResponse.DataItem, OutsourceWorkersAdapter.VH>(DIFF_TEAM_CALLBACK) {

    private var listenerCallback: SingleBlock<ChannelData>? = null

    private var listenerClickDetail: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerPhoneCall: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerAddToFavourite: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerCalendar: SingleBlock<AllWorkersResponse.DataItem>? = null

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

    inner class VH(val binding: ItemTeamWorkersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
//                channelImage.loadImageUrl(data.image as String)
                txtFullName.text = data.firstName + " " + data.lastName

                binding.cons1.setOnClickListener {
                    dropDown()
                }
                binding.imgDropDown.setOnClickListener {
                    dropDown()
                }

                data.image?.let { imgPerson.loadImageUrlWithPlaceholder(it, R.drawable.ic_user) }

                if (data.isFavourite!!) {
                    imgAddFavourites.setImageResource(R.drawable.ic_saved_contact)
                } else {
                    imgAddFavourites.setImageResource(R.drawable.ic_star)
                }

                binding.btnPhone.setOnClickListener {
                    listenerPhoneCall?.invoke(data)
                }

                binding.btnPersonal.setOnClickListener {
                    listenerClickDetail?.invoke(data)
                }

                btnAddFavourites.setOnClickListener {
                    listenerAddToFavourite?.invoke(data)
                }

                btnGoCalendar.setOnClickListener {
                    listenerCalendar?.invoke(data)
                }

            }
        }

        private fun dropDown() {
            if (binding.ll1.visibility == View.GONE) {
                binding.ll1.visibility = View.VISIBLE
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_down
                    )
                )
            } else {
                binding.ll1.visibility = View.GONE
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_up
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemTeamWorkersBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: SingleBlock<ChannelData>) {
        listenerCallback = block
    }

    fun setOnClickDetailListener(block: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerClickDetail = block
    }

    fun setOnPhoneClickListener(block: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerPhoneCall = block
    }

    fun setOnAddToFavouriteClickListener(block: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerAddToFavourite = block
    }

    fun setOnCalendarClickListener(block: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerCalendar = block
    }

}