package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.team_workers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTeamWorkers1Binding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder


/**
 * Created by Raximjanov Davronbek on 21.06.2021
 **/

class TeamWorkersAdapter :
    ListAdapter<AllWorkersShort, TeamWorkersAdapter.VH>(DIFF_TEAM_CALLBACK) {

    private var listenerClickItem: SingleBlock<AllWorkersShort>? = null
    private var listenerClickDetail: SingleBlock<AllWorkersShort>? = null
    private var listenerClickEditContact: SingleBlock<AllWorkersShort>? = null
    private var listenerPhoneCall: SingleBlock<AllWorkersShort>? = null
    private var listenerCalendar: SingleBlock<AllWorkersShort>? = null

    companion object {
        var DIFF_TEAM_CALLBACK = object : DiffUtil.ItemCallback<AllWorkersShort>() {
            override fun areItemsTheSame(
                oldItem: AllWorkersShort,
                newItem: AllWorkersShort
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: AllWorkersShort,
                newItem: AllWorkersShort
            ): Boolean {
                return newItem == oldItem
            }
        }
    }

    inner class VH(val binding: ItemTeamWorkers1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtFullName.text = data.fullName

                binding.cons1.setOnClickListener {
                    dropDown()
                }
                binding.imgDropDown.setOnClickListener {
                    dropDown()
                }

                data.image?.let { imgPerson.loadImageUrlWithPlaceholder(it, R.drawable.ic_user) }

                binding.btnPhone.setOnClickListener {
                    listenerPhoneCall?.invoke(data)
                }

                binding.btnPersonal.setOnClickListener {
                    listenerClickDetail?.invoke(data)
                }

                binding.btnEditPersonal.setOnClickListener {
                    listenerClickEditContact?.invoke(data)
                }

                btnGoCalendar.setOnClickListener {
//                    listenerCalendar?.invoke(data)
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
        ItemTeamWorkers1Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickDetailListener(block: SingleBlock<AllWorkersShort>) {
        listenerClickDetail = block
    }

    fun setOnClickEditContactListener(block: SingleBlock<AllWorkersShort>) {
        listenerClickEditContact = block
    }

    fun setOnPhoneClickListener(block: SingleBlock<AllWorkersShort>) {
        listenerPhoneCall = block
    }

    fun setOnCalendarClickListener(block: SingleBlock<AllWorkersShort>) {
        listenerCalendar = block
    }

}