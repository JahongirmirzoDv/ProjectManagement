package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.ItemSelectedFileBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.utils.download.getExtension
import uz.perfectalgorithm.projects.tezkor.utils.inVisible
import uz.perfectalgorithm.projects.tezkor.utils.visible


class SelectedFileAdapter(
    private val itemClickListener: SelectedFileAdapterListener,
    private var isEditorMode: Boolean = false
) : ListAdapter<FilesItem, SelectedFileAdapter.VH>(ITEM_CALLBACK) {

    @SuppressLint("NotifyDataSetChanged")
    fun changeMode(isEditorMode: Boolean) {
        this.isEditorMode = isEditorMode
        notifyDataSetChanged()
    }

/*
    override fun getItemViewType(position: Int) = getItem(position).viewType
*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedFileAdapter.VH {
        return VH(
            ItemSelectedFileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

/*        val binding = ItemSelectedFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                Log.e("onCreateVH", "viewType=VIEW_TYPE_IMAGE")
                ImageVH(binding)
            }
            VIEW_TYPE_IMAGE_NEW -> {
                Log.e("onCreateVH", "viewType=VIEW_TYPE_IMAGE_NEW")
                ImageVH(binding)
            }
            VIEW_TYPE_OTHER -> {
                Log.e("onCreateVH", "viewType=VIEW_TYPE_OTHER")
                OtherVH(binding)
            }
            VIEW_TYPE_OTHER_NEW -> {
                Log.e("onCreateVH", "viewType=VIEW_TYPE_OTHER_NEW")
                OtherVH(binding)
            }
            else -> throw Exception("There are only 4 types of item")
        }*/
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
/*
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = getItem(position)

        Log.e("onBindVH", "item=$itemData")
        when (itemData.viewType) {
            VIEW_TYPE_IMAGE -> (holder as ImageVH).bind(itemData)
            VIEW_TYPE_IMAGE_NEW -> (holder as ImageVH).bind(itemData)
            VIEW_TYPE_OTHER -> (holder as OtherVH).bind(itemData)
            VIEW_TYPE_OTHER_NEW -> (holder as OtherVH).bind(itemData)
            else -> throw Exception("There are only 4 types of item")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null && holder is OtherVH) {
            with(holder.itemFileBinding) {
                (payloads.first() as Bundle).getInt("progress").also {
                    pbDownloading.progress = it
                }
            }
        }
    }*/

    inner class VH(private val binding: ItemSelectedFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilesItem) = with(binding) {
            tvFileName.text = item.uriFile.lastPathSegment.toString()
            tvFileType.text = item.uriFile.getExtension(root.context)

            if (!isEditorMode) {
                ivAction.apply {
                    rotation = 0f
                    setImageResource(R.drawable.ic_download_file)
                    if (item.viewType == VIEW_TYPE_IMAGE || item.viewType == VIEW_TYPE_IMAGE_NEW) {
                        inVisible()
                    } else if (!item.file.exists() && !item.isDownloading) {
                        visible()
                    } else {
                        inVisible()
                    }
                }
            } else {
                ivAction.apply {
                    rotation = 45f
                    setImageResource(R.drawable.ic_add_plus)
                    visible()
                }
            }

            pbDownloading.isVisible = item.isDownloading

            if (isEditorMode) {
                ivAction.setOnClickListener {
                    when (item.viewType) {
                        VIEW_TYPE_IMAGE -> itemClickListener.onRemoteImageDelete(item)
                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileDelete(item)
                        VIEW_TYPE_IMAGE_NEW -> itemClickListener.onLocalImageDelete(item)
                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileDelete(item)
                    }
                }
            }

            root.setOnClickListener {
                if (!isEditorMode) {
                    when (item.viewType) {
                        VIEW_TYPE_IMAGE -> itemClickListener.onRemoteImageClick(item)
                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileClick(item)
                        VIEW_TYPE_IMAGE_NEW -> itemClickListener.onLocalImageClick(item)
                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileClick(item)
                    }
                }
/*            ivDelete.apply {
                isVisible = isEditorMode
                setOnClickListener {
                    when (item.viewType) {
                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileDelete(item)
                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileDelete(item)
                    }
                }
            }*/
            }
        }
/*
    inner class ImageVH(private val itemImageBinding: ItemSelectedFileBinding) :
        RecyclerView.ViewHolder(itemImageBinding.root) {

        fun bind(item: FilesItem) = with(itemImageBinding) {
//            ivImage.apply {
//                when (item.viewType) {
//                    VIEW_TYPE_IMAGE -> loadPictureUrl(item.path)
//                    VIEW_TYPE_IMAGE_NEW -> setImageURI(item.uriFile)
//                }
//                setOnClickListener {
//                    if (!isEditorMode) {
//                        when (item.viewType) {
//                            VIEW_TYPE_IMAGE -> itemClickListener.onRemoteImageClick(item)
//                            VIEW_TYPE_IMAGE_NEW -> itemClickListener.onLocalImageClick(item)
//                        }
//                    }
//                }
//            }
//            ivDelete.apply {
//                isVisible = isEditorMode
//                setOnClickListener {
//                    when (item.viewType) {
//                        VIEW_TYPE_IMAGE -> itemClickListener.onRemoteImageDelete(item)
//                        VIEW_TYPE_IMAGE_NEW -> itemClickListener.onLocalImageDelete(item)
//                    }
//                }
//            }
        }
    }

    inner class OtherVH(val itemFileBinding: ItemSelectedFileBinding) :
        RecyclerView.ViewHolder(itemFileBinding.root) {

        fun bind(item: FilesItem) = with(itemFileBinding) {
            Log.e("OtherVH", "item=$item")

//            tvFileName.text = item.uriFile.lastPathSegment.toString()
//            val fileName = item.uriFile.getExtension(root.context)
//            tvFileSize.text = getReadableFileSize(item.size) + " " + fileName
//
//            ivDownload.isVisible = !item.file.exists() && !item.isDownloading
//            progressBar.isVisible = item.isDownloading
//
//            clRoot.setOnClickListener {
//                if (!isEditorMode) {
//                    when (item.viewType) {
//                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileClick(item)
//                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileClick(item)
//                    }
//                }
//            }
//            ivDelete.apply {
//                isVisible = isEditorMode
//                setOnClickListener {
//                    when (item.viewType) {
//                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileDelete(item)
//                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileDelete(item)
//                    }
//                }
//            }
        }
    }*/

/*    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }*/
    }

    fun setDownloading(filesItem: FilesItem, isDownloading: Boolean) {
        getItem(filesItem)?.isDownloading = isDownloading
        notifyItemChanged(currentList.indexOf(filesItem))
    }

    fun setProgress(filesItem: FilesItem, progress: Int) {
        getItem(filesItem)?.progress = progress
        notifyItemChanged(
            currentList.indexOf(filesItem),
            Bundle().apply { putInt("progress", progress) })
    }

    private fun getItem(filesItem: FilesItem) = currentList.find { filesItem.id == it.id }

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_OTHER = 2
        const val VIEW_TYPE_IMAGE_NEW = 3
        const val VIEW_TYPE_OTHER_NEW = 4

        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<FilesItem>() {
            override fun areItemsTheSame(
                oldItem: FilesItem,
                newItem: FilesItem,
            ) = oldItem.title == newItem.title && oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: FilesItem,
                newItem: FilesItem,
            ) = oldItem.title == newItem.title &&
                    oldItem.viewType == newItem.viewType &&
                    oldItem.size == newItem.size

            override fun getChangePayload(oldItem: FilesItem, newItem: FilesItem) = false
        }
    }
}

interface SelectedFileAdapterListener {
    fun onLocalFileClick(item: FilesItem)
    fun onRemoteFileClick(item: FilesItem)
    fun onLocalImageClick(item: FilesItem)
    fun onRemoteImageClick(item: FilesItem)
    fun onLocalFileDelete(item: FilesItem)
    fun onRemoteFileDelete(item: FilesItem)
    fun onLocalImageDelete(item: FilesItem)
    fun onRemoteImageDelete(item: FilesItem)
}