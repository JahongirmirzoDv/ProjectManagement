package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model

import androidx.annotation.IntRange

/**
 *Created by farrukh_kh on 8/3/21 9:44 PM
 *uz.rdo.projects.projectmanagement.utils.views.drag_board_view.model
 **/
interface DragItem {

    val id: Int
    val title: String

    @IntRange(from = 0)
    fun getColumnIndex(): Int

    @IntRange(from = 0)
    fun getItemIndex(): Int

    fun setColumnIndex(@IntRange(from = 0) columnIndex: Int)

    fun setItemIndex(@IntRange(from = 0) itemIndex: Int)

    override fun equals(other: Any?): Boolean
}