package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.RatingIdeaData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemIdeaReytingBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 * Created by Jasurbek Kurganbaev on 27.07.2021 19:03
 **/
class RatingIdeasAdapter :
    ListAdapter<RatingIdeaData, RatingIdeasAdapter.VH>(ITEM_RATING_IDEA_CALLBACK) {

    private var itemClickListener: SingleBlock<RatingIdeaData>? = null


    companion object {
        var ITEM_RATING_IDEA_CALLBACK = object : DiffUtil.ItemCallback<RatingIdeaData>() {
            override fun areItemsTheSame(
                oldItem: RatingIdeaData,
                newItem: RatingIdeaData
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: RatingIdeaData,
                newItem: RatingIdeaData
            ): Boolean {
                return newItem.averageRating == oldItem.averageRating &&
                        newItem.ideaPrice == oldItem.ideaPrice &&
                        newItem.ratingsCount == oldItem.ratingsCount &&
                        newItem.commentsCount == oldItem.commentsCount &&
                        newItem.id == oldItem.id
            }

        }
    }

    fun setOnItemClickListener(block: SingleBlock<RatingIdeaData>) {
        itemClickListener = block
    }


    inner class VH(private val binding: ItemIdeaReytingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                Log.d("ZSA", "$data")
                ideaTitle.text = data.title
                val time = data.createdAt?.replace("-", ".")
                val resultDate = time?.split("T")?.get(0)
                ideaDate.text = resultDate
                ideaScore.text =
                    "${data.ratingsCount.toString()} (${data.averageRating.toString()})"
                ideaCommentCount.text = data.commentsCount.toString()
//                ideaValue.text = data.ideaPrice.toString()
                ideaOwnerAvatar.loadImageUrl(data.creatorImage!!)

                itemIdeaReyting.setOnClickListener {
                    itemClickListener?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemIdeaReytingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

}