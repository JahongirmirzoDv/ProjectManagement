package uz.perfectalgorithm.projects.tezkor.utils.views

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Created by Farhod Tohirov on 13-Feb-21
 **/

fun Fragment.hideKeyboard(view: View?) {
    if (view == null) {
        return
    }
    try {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (!imm.isActive) {
            return
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {

    }
}

fun Fragment.showKeyboard(view: View?): Boolean {
    if (view == null) {
        return false
    }
    try {
        view.requestFocus()
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    } catch (e: java.lang.Exception) {

    }
    return false
}