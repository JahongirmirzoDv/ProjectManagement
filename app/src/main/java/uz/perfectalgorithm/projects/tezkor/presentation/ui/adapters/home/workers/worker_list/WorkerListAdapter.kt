package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.worker_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemListWorkersBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

class WorkerListAdapter : RecyclerView.Adapter<WorkerListAdapter.VH>() {

    private var myList: ArrayList<AllWorkersShort> = ArrayList()

    private var listenerClickDetail: SingleBlock<AllWorkersShort>? = null
    private var listenerPhoneCall: SingleBlock<AllWorkersShort>? = null
    private var listenerAddToFavourite: SingleBlock<AllWorkersShort>? = null
    private var listenerCalendar: SingleBlock<AllWorkersShort>? = null
    private var listenerClickEditContact: SingleBlock<AllWorkersShort>? = null

    private var lastCapLetter = '|'

    var icsPosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<AllWorkersShort>?) {
        if (!list.isNullOrEmpty()) {
            myList.clear()
            myList.addAll(list)
            notifyDataSetChanged()
        }
    }


    inner class VH(val binding: ItemListWorkersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            binding.apply {

                val data = myList[absoluteAdapterPosition]
                var a = ';'
                if (!data.fullName.isNullOrBlank()) {
                    a = data.fullName.trim()[0].toLowerCase()
                }

                if (a != lastCapLetter.toLowerCase()) {
                    lastCapLetter = a
                    tvTitle.text = a.toString().toUpperCase()
                    tvTitle.visible()
                } else {
                    tvTitle.gone()
                }

                txtFullName.text = data.fullName

                data.image?.let { imgPerson.loadImageUrlWithPlaceholder(it, R.drawable.ic_user) }

                if (data.isFavourite!!) {
                    imgAddFavourites.setImageResource(R.drawable.ic_saved_contact)
                } else {
                    imgAddFavourites.setImageResource(R.drawable.ic_star_grey_sd)
                }

                if (data.isChecked) {
                    ll1.visible()
                } else {
                    ll1.gone()
                }

                binding.cons1.setOnClickListener {
                    dropDown()
                }

                binding.imgDropDown.setOnClickListener {
                    dropDown()
                }

                binding.btnPhone.setOnClickListener {
                    listenerPhoneCall?.invoke(data)
                }

                binding.btnPersonal.setOnClickListener {
                    listenerClickDetail?.invoke(data)
                }

                btnAddFavourites.setOnClickListener {
                    icsPosition = absoluteAdapterPosition
                    listenerAddToFavourite?.invoke(data)
                }

                btnGoCalendar.setOnClickListener {
//                    listenerCalendar?.invoke(data)
                }

                binding.btnEditPersonal.setOnClickListener {
                    listenerClickEditContact?.invoke(data)
                }
                binding.imgPhone.setOnClickListener {
                    listenerPhoneCall?.invoke(data)
                }

            }
        }

        private fun dropDown() {
            myList[absoluteAdapterPosition].isChecked = true
            if (binding.ll1.visibility == View.GONE) {
                binding.ll1.visibility = View.VISIBLE
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_down
                    )
                )
            } else {
                myList[absoluteAdapterPosition].isChecked = false
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemListWorkersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    override fun getItemCount() = myList.size

    fun setOnClickDetailListener(block: SingleBlock<AllWorkersShort>) {
        listenerClickDetail = block
    }

    fun setOnPhoneClickListener(block: SingleBlock<AllWorkersShort>) {
        listenerPhoneCall = block
    }

    fun setOnAddToFavouriteClickListener(block: SingleBlock<AllWorkersShort>) {
        listenerAddToFavourite = block
    }

    fun setOnCalendarClickListener(block: SingleBlock<AllWorkersShort>) {
        listenerCalendar = block
    }

    fun setUIAddFavourite(workerData: AllWorkersShort) {
        if (icsPosition > -1) {
            myList[icsPosition] = workerData
            notifyDataSetChanged()
            icsPosition = -1
        }
    }

    fun setOnClickEditContactListener(block: SingleBlock<AllWorkersShort>) {
        listenerClickEditContact = block
    }


}