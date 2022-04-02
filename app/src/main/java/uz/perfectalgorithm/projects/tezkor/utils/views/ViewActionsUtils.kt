package uz.perfectalgorithm.projects.tezkor.utils.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R

/**
 * Created by Jasurbek Kurganbaev on 01.07.2021 15:06
 **/

fun setActions(context: Context, recyclerView: RecyclerView, imageView: ImageView) {
    if (recyclerView.visibility == View.GONE) {
        recyclerView.visibility = View.VISIBLE
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_chevron_down
            )
        )
    } else {
        recyclerView.visibility = View.GONE
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_chevron_up
            )
        )
    }
}

fun ViewGroup.setDropDownClick(
    context: Context,
    recyclerView: RecyclerView?,
    imageView: ImageView?
) {
    if (recyclerView != null || imageView != null) {
        setOnClickListener {
            if (recyclerView?.visibility == View.GONE) {
                recyclerView.visibility = View.VISIBLE
                imageView?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_chevron_down
                    )
                )
            } else {
                recyclerView?.visibility = View.GONE
                imageView?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_chevron_up
                    )
                )
            }
        }
    }
}
