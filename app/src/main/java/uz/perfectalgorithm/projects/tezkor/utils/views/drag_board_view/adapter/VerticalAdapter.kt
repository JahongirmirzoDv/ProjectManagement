package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragVerticalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem

/**
 *Created by farrukh_kh on 8/3/21 9:47 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.adapter
 **/
abstract class VerticalAdapter<VH : RecyclerView.ViewHolder?>(
    private val context: Context,
    private var dragHelper: DragHelper,
    private val dragDropListener: DragDropListener
) : ListAdapter<DragItem, VH>(ITEM_CALLBACK), DragVerticalAdapter {

    //The position of the View being dragged
    private var mDragPosition = 0

    //Whether to hide the dragging position
    private var mHideDragItem = false

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position == mDragPosition && mHideDragItem) {
            holder!!.itemView.visibility = View.INVISIBLE
        } else {
            holder!!.itemView.visibility = View.VISIBLE
        }
        val item = getItem(position)
        holder.itemView.tag = item
        onBindViewHolder(context, holder, item, holder.absoluteAdapterPosition)
    }

    override fun onDrag(position: Int) {
        mDragPosition = position
        mHideDragItem = true
        notifyItemChanged(position)
    }

    override fun onDrop(page: Int, oldPage: Int, position: Int, oldPosition: Int, item: DragItem) {
        mHideDragItem = false
        notifyItemChanged(mDragPosition)
        if (oldPage != page) {
            val newList = currentList.toMutableList()
            newList.add(item)
            submitList(newList)
            dragDropListener.onItemDropped(item, page)
        }
    }

    override fun deleteItemFromOldList(position: Int) {
        if (mDragPosition >= 0 && mDragPosition < currentList.size) {
            val newList = currentList.toMutableList()
            newList.removeAt(position)
            submitList(newList)
            mDragPosition = -1
            dragDropListener.onDeleteFromOldList()
        }
    }

    fun dragItem(holder: VH) {
        dragHelper.dragItem(holder!!.itemView, holder.adapterPosition)
    }

    fun setDragHelper(dragHelper: DragHelper) {
        this.dragHelper = dragHelper
    }

    abstract fun onBindViewHolder(context: Context?, holder: VH, item: DragItem, position: Int)

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DragItem>() {
            override fun areItemsTheSame(oldItem: DragItem, newItem: DragItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DragItem, newItem: DragItem) =
                oldItem == newItem

            override fun getChangePayload(oldItem: DragItem, newItem: DragItem) = false
        }
    }
}