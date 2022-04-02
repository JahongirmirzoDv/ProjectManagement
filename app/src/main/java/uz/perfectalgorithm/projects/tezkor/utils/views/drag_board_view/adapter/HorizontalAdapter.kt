package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragHorizontalViewHolder
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn

/**
 *Created by farrukh_kh on 8/3/21 9:47 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.adapter
 **/
abstract class HorizontalAdapter<VH : HorizontalAdapter<VH>.ViewHolder>(
    private val context: Context
) : ListAdapter<DragColumn, VH>(ITEM_CALLBACK) {

    var dragHelper: DragHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        if (needFooter() && viewType == DragHelper.TYPE_FOOTER) {
            val convertView = LayoutInflater.from(context)
                .inflate(getFooterLayoutRes(), parent, false)
            return onCreateViewHolder(convertView, DragHelper.TYPE_FOOTER)
        }
        val convertView = LayoutInflater.from(context)
            .inflate(getContentLayoutRes(), parent, false)
        return onCreateViewHolder(convertView, DragHelper.TYPE_CONTENT)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        when (getItemViewType(position)) {
            DragHelper.TYPE_CONTENT -> {
                val dragColumn = getItem(position)
                holder.itemView.tag = dragColumn
                onBindContentViewHolder(holder, dragColumn, position)
            }
            DragHelper.TYPE_FOOTER -> onBindFooterViewHolder(holder, position)
            else -> {
                throw IllegalArgumentException("There are only 2 view types")
            }
        }
    }

    override fun getItemCount() = if (needFooter()) currentList.size + 1 else currentList.size

    override fun getItemViewType(position: Int) = when {
        !needFooter() -> DragHelper.TYPE_CONTENT
        position == itemCount - 1 -> DragHelper.TYPE_FOOTER
        else -> DragHelper.TYPE_CONTENT
    }

    fun appendNewColumn(dragColumn: DragColumn) {
        val newList = currentList.toMutableList()
        newList.add(dragColumn)
        submitList(newList)
    }

    abstract fun getContentLayoutRes(): Int
    abstract fun getFooterLayoutRes(): Int
    abstract fun onCreateViewHolder(parent: View?, viewType: Int): VH
    abstract fun onBindContentViewHolder(holder: VH, dragColumn: DragColumn?, position: Int)
    abstract fun onBindFooterViewHolder(holder: VH, position: Int)
    abstract fun needFooter(): Boolean

    abstract inner class ViewHolder(convertView: View) :
        RecyclerView.ViewHolder(convertView), DragHorizontalViewHolder {
        abstract override fun getRecyclerView(): RecyclerView
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DragColumn>() {
            override fun areItemsTheSame(oldItem: DragColumn, newItem: DragColumn) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DragColumn, newItem: DragColumn) =
                oldItem == newItem

            override fun getChangePayload(oldItem: DragColumn, newItem: DragColumn) = false
        }
    }
}