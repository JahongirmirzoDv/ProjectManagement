package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.RatingIdeaData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemIdeaAllBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show

/**
 * Created by Jasurbek Kurganbaev on 28.07.2021 15:01
 **/
class UndoneIdeaAdapter(private var id: Int) :
    ListAdapter<RatingIdeaData, UndoneIdeaAdapter.VH>(ITEM_UNDONE_IDEA_CALLBACK) {

    private var itemClickListener: SingleBlock<RatingIdeaData>? = null
    private var itemMoreClickListener: SingleBlock<RatingIdeaData>? = null

    companion object {
        var ITEM_UNDONE_IDEA_CALLBACK = object : DiffUtil.ItemCallback<RatingIdeaData>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemIdeaAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
        when (id) {
            1 -> holder.bindWithEvents()
        }
    }

    fun setOnClickListener(block: SingleBlock<RatingIdeaData>) {
        itemClickListener = block
    }

    fun setOnItemMoreClickListener(block: SingleBlock<RatingIdeaData>) {
        itemMoreClickListener = block
    }

    inner class VH(private val binding: ItemIdeaAllBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                ideaCreator.loadImageUrl(data.creatorImage!!)
                ideaTitle.text = data.title
                val time = data.createdAt?.replace("-", ".")
                val resultDate = time?.split("T")?.get(0)
                ideaDate.text = resultDate
                ideaDescription.text = data.description

                itemUndoneIdea.setOnClickListener {
                    itemClickListener?.invoke(data)
                }

            }
        }

        fun bindWithEvents() = bindItem {
            binding.apply {
                btnMore.show()
                val data = getItem(absoluteAdapterPosition)

                btnMore.setOnClickListener {
                    itemMoreClickListener?.invoke(data)
                }

            }
        }


    }


}