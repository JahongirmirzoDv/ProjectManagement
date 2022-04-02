package uz.perfectalgorithm.projects.tezkor.utils.extensions

import androidx.fragment.app.Fragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity

/**
 * Created by Jasurbek Kurganbaev on 19.06.2021 16:54
 **/

fun Fragment.setNavigationIcon() {
    try {
        (activity as HomeActivity).setNavigationIcon()
    } catch (e: Exception) {

    }
}

fun Fragment.showBottomMenu() {
    try {
        (activity as HomeActivity).showBottomMenu()
    } catch (e: Exception) {

    }
}

fun Fragment.hideBottomMenu() {
    try {
        (activity as HomeActivity).hideBottomMenu()
    } catch (e: Exception) {

    }
}

fun Fragment.showAppBar() {
    try {
        (activity as HomeActivity).showAppBar()
    } catch (e: Exception) {

    }
}

fun Fragment.hideAppBar() {
    try {
        (activity as HomeActivity).hideAppBar()
    } catch (e: Exception) {

    }
}

fun Fragment.hideQuickButton() {
    try {
        (activity as HomeActivity).hideQuickButton()
    } catch (e: Exception) {

    }
}

fun Fragment.showQuickButton() {
    try {
        (activity as HomeActivity).showQuickButton()
    } catch (e: Exception) {

    }
}