package uz.perfectalgorithm.projects.tezkor.utils.views

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

fun Activity.rotateAnimation(): Animation {
    return AnimationUtils.loadAnimation(this, R.anim.rotate_center)
}


fun Fragment.rotateAnimation(emptyBlock: EmptyBlock, view: View) {
    val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_center)
}