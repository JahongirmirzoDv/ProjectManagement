package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.offers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.FilesItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemDetailOfferForFileBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemDetailOfferForImageBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadPictureUrl
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


class FilesAdapter(private val context: Context, private val listenerFile: FileItemClickListener) :
    ListAdapter<FilesItem, RecyclerView.ViewHolder>(
        diffUtil
    ) {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }


    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ONE) {
            VH1(ItemDetailOfferForImageBinding.inflate(inflater, parent, false))
        } else {
            VH2(ItemDetailOfferForFileBinding.inflate(inflater, parent, false))
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = currentList[position]
        if (itemData.viewType == VIEW_TYPE_ONE) {
            (holder as VH1).itemImageBinding.ivOffer.apply {
                loadPictureUrl(itemData.path)
                setOnClickListener {
                    listenerFile.itemClickImage(itemData.path)
                }
            }

        } else {
            (holder as VH2).itemFileBinding.apply {
                tvFileName.text = itemData.uriFile.lastPathSegment.toString()
                val fileName = itemData.path.substring(itemData.path.lastIndexOf('.') + 1)
                tvFileSize.text = getReadableFileSize(itemData.size) + " " + fileName

                ivDownload.isVisible = !itemData.file.exists() && !itemData.isDownloading
                progressBar.isVisible = itemData.isDownloading

                mainContainer.setOnClickListener {
                    listenerFile.itemClickFile(itemData)
                }

            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with((holder as VH2).itemFileBinding) {
                (payloads.first() as Bundle).getInt("progress").also {
                    progressBar.progress = it
                }
            }
        }
    }

    class VH1(val itemImageBinding: ItemDetailOfferForImageBinding) :
        RecyclerView.ViewHolder(itemImageBinding.root)

    class VH2(val itemFileBinding: ItemDetailOfferForFileBinding) :
        RecyclerView.ViewHolder(itemFileBinding.root)

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
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


    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}

interface FileItemClickListener {
    fun itemClickFile(itemData: FilesItem)
    fun itemClickImage(imageUrl: String)
}

private val diffUtil = object : DiffUtil.ItemCallback<FilesItem>() {
    override fun areItemsTheSame(
        oldItem: FilesItem,
        newItem: FilesItem,
    ): Boolean = oldItem.file == newItem.file

    override fun areContentsTheSame(
        oldItem: FilesItem,
        newItem: FilesItem,
    ): Boolean = oldItem == newItem

}

