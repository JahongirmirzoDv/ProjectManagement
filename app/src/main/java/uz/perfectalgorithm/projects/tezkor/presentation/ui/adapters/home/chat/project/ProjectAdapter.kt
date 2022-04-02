package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemProjectBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 11:56
 **/
class ProjectAdapter(private val projectDifference: Int) :
    ListAdapter<ProjectData, ProjectAdapter.VH>(DIFF_PROJECT_CALLBACK) {

    private var listenerCallback: SingleBlock<ProjectData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )


    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.bind()
    }

    companion object {
        var DIFF_PROJECT_CALLBACK = object : DiffUtil.ItemCallback<ProjectData>() {
            override fun areItemsTheSame(oldItem: ProjectData, newItem: ProjectData): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: ProjectData, newItem: ProjectData): Boolean {
                return newItem.id == oldItem.id && newItem.performer == oldItem.performer
                        && newItem.title == oldItem.title && newItem.endTime == oldItem.endTime
            }
        }
    }

    fun setOnClickListener(block: SingleBlock<ProjectData>) {
        listenerCallback = block
    }


    inner class VH(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    listenerCallback?.invoke(getItem(absoluteAdapterPosition))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                tvProjectName.text = data.title
                tvProjectId.text = "#${data.id}"
                val time = data.endTime?.replace("-", ".")
                val resultDate = time?.split("T")?.get(0)

                tvProjectEndingTime.text = resultDate
                tvProjectResponsible.text =
                    data.performer?.firstName + " " + data.performer?.lastName
                tvUnReadMessage
                when (data.importance) {
                    "green" -> cardImportance.setBackgroundResource(R.color.green)
                    "yellow" -> cardImportance.setBackgroundResource(R.color.yellow)
                    else -> cardImportance.setBackgroundResource(R.color.project_status_red_color)
                }
            }
        }
    }


}