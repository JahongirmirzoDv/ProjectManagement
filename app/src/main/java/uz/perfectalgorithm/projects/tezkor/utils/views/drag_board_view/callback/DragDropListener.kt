package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback

import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem

/**
 *Created by farrukh_kh on 8/3/21 9:42 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.callback
 **/
interface DragDropListener {

    fun onDeleteFromOldList()

    fun onItemDropped(item: DragItem, newPageIndex: Int)
}