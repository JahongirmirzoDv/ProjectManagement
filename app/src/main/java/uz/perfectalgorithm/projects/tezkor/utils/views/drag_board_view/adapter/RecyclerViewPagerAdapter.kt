package uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewPagerAdapter<VH : RecyclerView.ViewHolder>(
    private val viewPager: RecyclerView,
    val adapter: RecyclerView.Adapter<VH>
) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return adapter.onCreateViewHolder(parent, viewType)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        adapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        adapter.unregisterAdapterDataObserver(observer)
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        adapter.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        return adapter.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        adapter.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        adapter.onViewDetachedFromWindow(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        adapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        adapter.onDetachedFromRecyclerView(recyclerView)
    }

    // TODO: 8/3/21 must be adapted to dynamic height/width
    override fun onBindViewHolder(holder: VH, position: Int) {
        adapter.onBindViewHolder(holder, position)
        val itemView = holder.itemView
        val lp: ViewGroup.LayoutParams
        if (itemView.layoutParams == null) {
            lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } else {
            lp = itemView.layoutParams
            if (viewPager.layoutManager!!.canScrollHorizontally()) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        itemView.layoutParams = lp
    }

    override fun getItemCount() = adapter.itemCount

    override fun getItemViewType(position: Int) = adapter.getItemViewType(position)

    override fun getItemId(position: Int) = adapter.getItemId(position)
}