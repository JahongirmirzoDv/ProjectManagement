package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.FinishedIdeaItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemIdeaCompletedBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 * Created by Jasurbek Kurganbaev on 28.07.2021 14:18
 **/
class FinishedIdeasAdapter :
    ListAdapter<FinishedIdeaItem, FinishedIdeasAdapter.VH>(ITEM_FINISHED_IDEA_CALLBACK) {

    private var itemClickListener: SingleBlock<FinishedIdeaItem>? = null


    companion object {
        var ITEM_FINISHED_IDEA_CALLBACK = object : DiffUtil.ItemCallback<FinishedIdeaItem>() {
            override fun areItemsTheSame(
                oldItem: FinishedIdeaItem,
                newItem: FinishedIdeaItem
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: FinishedIdeaItem,
                newItem: FinishedIdeaItem
            ): Boolean {
                return newItem.averageRating == oldItem.averageRating &&
                        newItem.ideaPrice == oldItem.ideaPrice &&
                        newItem.ratingsCount == oldItem.ratingsCount &&
                        newItem.commentsCount == oldItem.commentsCount &&
                        newItem.createdAt == oldItem.createdAt &&
                        newItem.endTime == oldItem.endTime &&
                        newItem.id == oldItem.id
            }

        }
    }

    fun setOnClickListener(block: SingleBlock<FinishedIdeaItem>) {
        itemClickListener = block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemIdeaCompletedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    inner class VH(private val binding: ItemIdeaCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                Log.d("ZSA", "$data")
                ideaTitle.text = data.title

                val timeStart = data.createdAt?.replace("-", ".")
                val resultStartDate = timeStart?.split("T")?.get(0)
                ideaStartDate.text = resultStartDate
                ideaDate.text = resultStartDate

                val timeEnd = data.endTime?.replace("-", ".")
                val resultEndDate = timeEnd?.split("T")?.get(0)
                ideaCompletedDate.text = resultEndDate


                ideaScore.text = data.ratingsCount.toString()
                ideaStar.text = data.averageRating.toString()
                ideaCommentCount.text = data.commentsCount.toString()
                ideaValue.text = data.ideaPrice.toString()
                ideaCreator.loadImageUrl(data.creatorImage!!)

                itemIdeaCompleted.setOnClickListener {
                    itemClickListener?.invoke(data)
                }


            }
        }
    }


}
