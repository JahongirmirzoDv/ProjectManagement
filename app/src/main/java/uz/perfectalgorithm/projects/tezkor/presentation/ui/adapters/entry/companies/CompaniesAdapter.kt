package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.entry.companies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.CompaniesResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemCompanyBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithBaseUrl

/**
 * Created by Raximjanov Davronbek on 26.06.2021
 **/

class CompaniesAdapter :
    ListAdapter<CompaniesResponse.CompanyData, CompaniesAdapter.VH>(DIFF_TEAM_CALLBACK) {

    private var listenerClickItem: DoubleBlock<CompaniesResponse.CompanyData, Int>? = null

    var selected = -1
    private var listBackgrounds: ArrayList<View> = arrayListOf()

    companion object {
        var DIFF_TEAM_CALLBACK = object : DiffUtil.ItemCallback<CompaniesResponse.CompanyData>() {
            override fun areItemsTheSame(
                oldItem: CompaniesResponse.CompanyData,
                newItem: CompaniesResponse.CompanyData
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: CompaniesResponse.CompanyData,
                newItem: CompaniesResponse.CompanyData
            ): Boolean {
                return newItem.title == oldItem.title && newItem.mfo == oldItem.mfo
            }
        }
    }

    inner class VH(val binding: ItemCompanyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                listBackgrounds.add(imgCompanyLogo)
                mainContainer.setOnClickListener {
                    getItem(absoluteAdapterPosition)?.let {
//                        val last = selected
//                        selected = absoluteAdapterPosition
//                        changeBg(selected)
                        mainContainer.setBackgroundResource(
                            R.drawable.back_img_company_card
                        )
                        listenerClickItem?.invoke(it, absoluteAdapterPosition)
                    }
                }
            }
        }

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            data.let {
                binding.apply {
                    tvCompanyName.text = data.title
                    if (data.image.isNullOrEmpty()) {
//                        imgCompanyLogo.hide()
                    } else {
                        imgCompanyLogo.loadImageUrlWithBaseUrl(data.image)
                    }
                }
            }
        }

//        private fun changeBg(id: Int) {
//            if (id > -1) {
//                listBackgrounds[id].setBackgroundColor(
//                    ContextCompat.getColor(
//                        binding.root.context,
//                        R.color.white
//                    )
//                )
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemCompanyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: DoubleBlock<CompaniesResponse.CompanyData, Int>) {
        listenerClickItem = block
    }

}