package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback

import androidx.recyclerview.widget.RecyclerView

/**
 *Created by farrukh_kh on 8/3/21 9:42 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.callback
 **/
interface DragHorizontalViewHolder {
    /**
     * each Horizontal ViewHolder is supposed to have a RecycleView
     * @return RecycleView in ViewHolder which contains vertical items
     */
    fun getRecyclerView(): RecyclerView
}