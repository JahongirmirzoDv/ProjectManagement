package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.databinding.ItemEditFileImageBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemEditFileOtherBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.utils.download.getExtension
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadPictureUrl
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

/**
 *Created by farrukh_kh on 8/13/21 11:09 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.others.edit_files
 **/
class EditFileAdapter(private val itemClickListener: EditFileAdapterListener) :
    ListAdapter<FilesItem, RecyclerView.ViewHolder>(ITEM_CALLBACK) {

    var isEditorMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_IMAGE -> {
            ImageVH(
                ItemEditFileImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        VIEW_TYPE_IMAGE_NEW -> {
            ImageVH(
                ItemEditFileImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        VIEW_TYPE_OTHER -> {
            OtherVH(
                ItemEditFileOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        VIEW_TYPE_OTHER_NEW -> {
            OtherVH(
                ItemEditFileOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        else -> throw Exception("There are only 4 types of item")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = getItem(position)

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
                    progressBar.progress = it
                }
            }
        }
    }

    inner class ImageVH(private val itemImageBinding: ItemEditFileImageBinding) :
        RecyclerView.ViewHolder(itemImageBinding.root) {

        fun bind(item: FilesItem) = with(itemImageBinding) {
            ivImage.apply {
                when (item.viewType) {
                    VIEW_TYPE_IMAGE -> loadPictureUrl(item.path)
                    VIEW_TYPE_IMAGE_NEW -> setImageURI(item.uriFile)
                }
                setOnClickListener {
                    if (!isEditorMode) {
                        when (item.viewType) {
                            VIEW_TYPE_IMAGE -> itemClickListener.onRemoteImageClick(item)
                            VIEW_TYPE_IMAGE_NEW -> itemClickListener.onLocalImageClick(item)
                        }
                    }
                }
            }
            ivDelete.apply {
                isVisible = isEditorMode
                setOnClickListener {
                    when (item.viewType) {
                        VIEW_TYPE_IMAGE -> itemClickListener.onRemoteImageDelete(item)
                        VIEW_TYPE_IMAGE_NEW -> itemClickListener.onLocalImageDelete(item)
                    }
                }
            }
        }
    }

    inner class OtherVH(val itemFileBinding: ItemEditFileOtherBinding) :
        RecyclerView.ViewHolder(itemFileBinding.root) {

        fun bind(item: FilesItem) = with(itemFileBinding) {

            tvFileName.text = item.uriFile.lastPathSegment.toString()
            val fileName = item.uriFile.getExtension(root.context)
            tvFileSize.text = getReadableFileSize(item.size) + " " + fileName

            ivDownload.isVisible = !item.file.exists() && !item.isDownloading
            progressBar.isVisible = item.isDownloading

            clRoot.setOnClickListener {
                if (!isEditorMode) {
                    when (item.viewType) {
                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileClick(item)
                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileClick(item)
                    }
                }
            }
            ivDelete.apply {
                isVisible = isEditorMode
                setOnClickListener {
                    when (item.viewType) {
                        VIEW_TYPE_OTHER -> itemClickListener.onRemoteFileDelete(item)
                        VIEW_TYPE_OTHER_NEW -> itemClickListener.onLocalFileDelete(item)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).viewType

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

    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_OTHER = 2
        const val VIEW_TYPE_IMAGE_NEW = 3
        const val VIEW_TYPE_OTHER_NEW = 4

        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<FilesItem>() {
            override fun areItemsTheSame(
                oldItem: FilesItem,
                newItem: FilesItem,
            ) = oldItem.title == newItem.title

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

interface EditFileAdapterListener {
    fun onLocalFileClick(item: FilesItem)
    fun onRemoteFileClick(item: FilesItem)
    fun onLocalImageClick(item: FilesItem)
    fun onRemoteImageClick(item: FilesItem)
    fun onLocalFileDelete(item: FilesItem)
    fun onRemoteFileDelete(item: FilesItem)
    fun onLocalImageDelete(item: FilesItem)
    fun onRemoteImageDelete(item: FilesItem)
}