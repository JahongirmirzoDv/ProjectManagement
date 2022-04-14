package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.own_task



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.OwnTaskItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemFunctionalAllListBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemOwnTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

class OwnTasksAdapter(
) : ListAdapter<OwnTaskItem, OwnTasksAdapter.VH>(
    ITEM_ALL_FUNCTIONAL_TASK_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemOwnTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: ItemOwnTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: OwnTaskItem) = bindItem {
            binding.apply {
                val idText = resources.getString(R.string.task) + " #${data.id}"
                tvId.text = idText
                tvTitle.text = data.title

                val startDate = data.startTime?.replace("-", ".")
                val resultStartDate = startDate?.split("T")?.get(0)

                val endDate = data.endTime?.replace("-", ".")
                val resultEndDate = endDate?.split("T")?.get(0)

                tvTime.text = "$resultStartDate | $resultEndDate"
                tvMasterName.text = data.leaderFirstName + " " + data.leaderLastName
                tvPerformerName.text = data.performerFirstName + " " + data.performerLastName
            }
        }
    }

    companion object {

        var ITEM_ALL_FUNCTIONAL_TASK_CALLBACK = object : DiffUtil.ItemCallback<OwnTaskItem>() {
            override fun areItemsTheSame(oldItem: OwnTaskItem, newItem: OwnTaskItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: OwnTaskItem, newItem: OwnTaskItem): Boolean {

                return oldItem.id == newItem.id && oldItem.leaderLastName == newItem.leaderLastName &&
                        oldItem.leaderFirstName == newItem.leaderFirstName &&
                        oldItem.performerFirstName == newItem.performerFirstName &&
                        oldItem.performerLastName == newItem.performerLastName &&
                        oldItem.startTime == newItem.startTime &&
                        oldItem.endTime == newItem.endTime &&
                        oldItem.title == newItem.title &&
                        oldItem.hashCode() == newItem.hashCode()
            }
        }

    }
}