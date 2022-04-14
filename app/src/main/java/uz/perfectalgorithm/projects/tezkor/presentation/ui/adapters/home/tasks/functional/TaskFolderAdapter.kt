package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TasksByStatus
import uz.perfectalgorithm.projects.tezkor.databinding.ItemFunctionalBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.ThreeBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

class TaskFolderAdapter(
    private val onItemClick: SingleBlock<Int>,
    private val onAddNewTask: ThreeBlock<String, Int, Int>,
    private val onAddColumnClick: EmptyBlock,
    private val changeItemStatus: DoubleBlock<FolderItem, TasksByStatus>,
    private val onTaskClick: SingleBlock<Int>,
    private val onMenuClick: DoubleBlock<Status, View>,
    private val onLongClick: DoubleBlock<TaskFolderListItem, View>
) : ListAdapter<TaskFolderListItem, TaskFolderAdapter.VH>(ITEM_CALLBACK) {

    private val openedFolders = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemFunctionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.binding.relativeLayout.setOnClickListener {
//            onItemClick(position)
//            holder.bind(getItem(position), false)
//        }
//        holder.binding.imgDropDownCompany.setOnClickListener {
//            onItemClick(position)
//            holder.bind(getItem(position), false)
//        }
//        holder.binding.relativeLayout.setOnLongClickListener {
//            onLongClick(getItem(position), it)
//            true
//        }
        holder.bind(getItem(position), true)
    }

    inner class VH(val binding: ItemFunctionalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val statusAdapter by lazy {
            TaskStatusAdapter(
                binding.root.context,
                onAddNewTask,
                onAddColumnClick,
                changeItemStatus,
                onTaskClick,
                onMenuClick
            )
        }

        init {
            binding.dragBoardView.setHorizontalAdapter(statusAdapter)

            binding.relativeLayout.setOnClickListener {
                onItemClick(bindingAdapterPosition)
                bind(getItem(bindingAdapterPosition), false)
            }
            binding.imgDropDownCompany.setOnClickListener {
                onItemClick(bindingAdapterPosition)
                bind(getItem(bindingAdapterPosition), false)
            }
            binding.relativeLayout.setOnLongClickListener {
                onLongClick(getItem(bindingAdapterPosition), it)
                true
            }
        }

        fun bind(data: TaskFolderListItem, isFirstLaunch: Boolean) = bindItem {
            with(binding) {
                tvTitle.text = data.title
                statusAdapter.folderId = data.id

                when {
                    isFirstLaunch && openedFolders.contains(data.id) -> {
                        dragBoardView.isVisible = true
                        setMatchParent()
                        imgDropDownCompany.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.ic_chevron_down
                            )
                        )
                        statusAdapter.submitList(data.tasks)
                    }
                    isFirstLaunch && !openedFolders.contains(data.id) -> {
                        dragBoardView.isVisible = false
                        setWrapContent()
                        imgDropDownCompany.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.ic_chevron_up
                            )
                        )
                    }
                    !isFirstLaunch && !dragBoardView.isVisible -> {
                        dragBoardView.isVisible = true
                        setMatchParent()
                        openedFolders.add(data.id)
                        imgDropDownCompany.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.ic_chevron_down
                            )
                        )
                        statusAdapter.submitList(data.tasks)
                    }
                    else -> {
                        dragBoardView.isVisible = false
                        setWrapContent()
                        openedFolders.remove(data.id)
                        imgDropDownCompany.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.ic_chevron_up
                            )
                        )
                    }
                }
            }
        }

        private fun setMatchParent() {
            binding.cons1.layoutParams = binding.cons1.layoutParams.apply {
                height = ConstraintLayout.LayoutParams.MATCH_PARENT
            }
        }

        private fun setWrapContent() {
            binding.cons1.layoutParams = binding.cons1.layoutParams.apply {
                height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            }
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<TaskFolderListItem>() {
            override fun areItemsTheSame(
                oldItem: TaskFolderListItem,
                newItem: TaskFolderListItem
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TaskFolderListItem,
                newItem: TaskFolderListItem
            ) = oldItem.tasks?.toTypedArray().contentDeepEquals(newItem.tasks?.toTypedArray()) &&
                    oldItem.title == newItem.title

            override fun getChangePayload(
                oldItem: TaskFolderListItem,
                newItem: TaskFolderListItem
            ) = false
        }
    }
}