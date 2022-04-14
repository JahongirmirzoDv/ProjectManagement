package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback

import androidx.recyclerview.widget.RecyclerView


interface DragHorizontalViewHolder {
    /**
     * each Horizontal ViewHolder is supposed to have a RecycleView
     * @return RecycleView in ViewHolder which contains vertical items
     */
    fun getRecyclerView(): RecyclerView
}