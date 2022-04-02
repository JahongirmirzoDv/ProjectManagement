package uz.perfectalgorithm.projects.tezkor.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *Created by farrukh_kh on 9/2/21 4:42 PM
 *uz.rdo.projects.projectmanagement.utils
 **/
class SpaceItemDecoration(
    private val spaceSize: Int = 0,
    private val orientation: Int = RecyclerView.VERTICAL
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        with(outRect) {
            if (orientation == RecyclerView.VERTICAL) {
                if (position == 0) {
                    top = spaceSize
                }
                bottom = spaceSize
            }

            if (orientation == RecyclerView.HORIZONTAL) {
                if (position == 0) {
                    left = spaceSize
                }
                right = spaceSize
            }
        }
    }
}