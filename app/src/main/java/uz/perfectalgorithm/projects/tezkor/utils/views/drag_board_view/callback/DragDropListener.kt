package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback

import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem


interface DragDropListener {

    fun onDeleteFromOldList()

    fun onItemDropped(item: DragItem, newPageIndex: Int)
}