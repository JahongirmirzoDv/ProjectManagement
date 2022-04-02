package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback

import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem

/**
 *Created by farrukh_kh on 8/3/21 9:43 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.callback
 **/
interface DragVerticalAdapter {
    /**
     * call if on dragItem item
     * @param position item position in Item List (which in VerticalRecyclerView)
     * 0 <= position <= size()-1
     */
    fun onDrag(position: Int)

    /**
     * call if on dropItem item
     * @param page item position in Entry List (which in HorizontalRecyclerView)
     * @param position dropItem item to position in entry.getItemList()
     * 0 <= position <= size()-1
     * @param tag convert it to your Item object
     */
    fun onDrop(page: Int, oldPage: Int, position: Int, oldPosition: Int, item: DragItem)

    fun deleteItemFromOldList(position: Int)
}