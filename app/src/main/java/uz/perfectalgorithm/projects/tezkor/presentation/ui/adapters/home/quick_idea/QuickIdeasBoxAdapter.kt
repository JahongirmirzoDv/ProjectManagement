package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.IdeasBoxData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemBoxIdeasBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 20.07.2021 16:10
 **/
class QuickIdeasBoxAdapter :
    ListAdapter<IdeasBoxData, QuickIdeasBoxAdapter.VH>(ITEM_IDEA_BOX_CALLBACK) {

    private var itemClickListener: SingleBlock<IdeasBoxData>? = null
    private var itemMoreButtonClick: SingleBlock<IdeasBoxData>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemBoxIdeasBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    fun setOnItemClickListener(block: SingleBlock<IdeasBoxData>) {
        itemClickListener = block
    }

    fun setOnItemMoreButtonClickListener(block: SingleBlock<IdeasBoxData>) {
        itemMoreButtonClick = block
    }


    companion object {
        var ITEM_IDEA_BOX_CALLBACK = object : DiffUtil.ItemCallback<IdeasBoxData>() {
            override fun areItemsTheSame(oldItem: IdeasBoxData, newItem: IdeasBoxData): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: IdeasBoxData, newItem: IdeasBoxData): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.title == oldItem.title &&
                        newItem.countIdeas == oldItem.countIdeas
            }
        }
    }

    inner class VH(private val binding: ItemBoxIdeasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                tvTitle.text = data.title
                tvCount.text =
                    resources.getString(R.string._1_s_fikr_lar, data.countIdeas.toString())
                itemIdeaBox.setOnClickListener {
                    itemClickListener?.invoke(data)
                }
                ideaBoxItemMoreButton.setOnClickListener {
                    itemMoreButtonClick?.invoke(data)
                }
            }
        }
    }


}