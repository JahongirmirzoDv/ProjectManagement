package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.manage_users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user.ManageUsers
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTeamWorkersBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder

class ManageUsersAdapter :
    ListAdapter<ManageUsers, ManageUsersAdapter.VH>(DIFF_TEAM_CALLBACK) {

    private var listenerClickItem: SingleBlock<ManageUsers>? = null
    private var listenerClickDetail: SingleBlock<ManageUsers>? = null
    private var listenerPhoneCall: SingleBlock<ManageUsers>? = null
    private var listenerCalendar: SingleBlock<ManageUsers>? = null

    companion object {
        var DIFF_TEAM_CALLBACK = object : DiffUtil.ItemCallback<ManageUsers>() {
            override fun areItemsTheSame(
                oldItem: ManageUsers,
                newItem: ManageUsers
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: ManageUsers,
                newItem: ManageUsers
            ): Boolean {
                return newItem == oldItem
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

                data.image.let { imgPerson.loadImageUrlWithPlaceholder(it, R.drawable.ic_user) }

                imgAddFavourites.setImageResource(R.drawable.ic_saved_contact)

                binding.btnPhone.setOnClickListener {
                    listenerPhoneCall?.invoke(data)
                }

                binding.btnPersonal.setOnClickListener {
                    listenerClickDetail?.invoke(data)
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

    fun setOnClickDetailListener(block: SingleBlock<ManageUsers>) {
        listenerClickDetail = block
    }

    fun setOnPhoneClickListener(block: SingleBlock<ManageUsers>) {
        listenerPhoneCall = block
    }

    fun setOnCalendarClickListener(block: SingleBlock<ManageUsers>) {
        listenerCalendar = block
    }

}