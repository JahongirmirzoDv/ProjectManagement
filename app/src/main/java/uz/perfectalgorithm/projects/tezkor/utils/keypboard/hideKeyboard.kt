package uz.perfectalgorithm.projects.tezkor.utils.keypboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock


fun Fragment.hideKeyboard(view: View) {

    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    if (view != null && imm != null) {
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS);

    }
}


fun Activity.hideKeyboard() {

    /*   val inputManager = this.getSystemService(
           Context.INPUT_METHOD_SERVICE
       ) as InputMethodManager
       val focusedView = this.currentFocus
       *//*
     * If no view is focused, an NPE will be thrown
     *
     * Maxim Dmitriev
     *//*
    *//*
     * If no view is focused, an NPE will be thrown
     *
     * Maxim Dmitriev
     *//*if (focusedView != null) {
        inputManager.hideSoftInputFromWindow(
            *//*focusedView.windowToken*//*this.window.getDecorView().getRootView().getWindowToken(),
            *//*InputMethodManager.HIDE_NOT_ALWAYS*//*0
        )
    }*/


    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (this.currentFocus != null) {
        imm.hideSoftInputFromWindow(
            this.currentFocus?.windowToken, 0
        );
    }
}


fun Fragment.showAlertDialog(text: String, dismissListener: EmptyBlock? = null) {
    val dialog = AlertDialog
        .Builder(requireContext())
        .setTitle(R.string.alert)
        .setMessage(text)
        .setPositiveButton("Ha") { dialog: DialogInterface?, which: Int ->
            dismissListener?.invoke()
        }
        .setNegativeButton("Yo'q") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        .create()

    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
    }
    dialog.show()
}

fun Fragment.showAlertDialogStuff(
    text: String,
    doListener: EmptyBlock? = null,
    undoListener: EmptyBlock? = null
) {
    val dialog = AlertDialog
        .Builder(requireContext())
        .setTitle(R.string.alert)
        .setMessage(text)
        .setPositiveButton("Ha") { dialog: DialogInterface?, which: Int ->
            doListener?.invoke()
        }
        .setNegativeButton("Yo'q") { dialog: DialogInterface, which: Int -> /*dialog.cancel()*/
            undoListener?.invoke()
            dialog.cancel()

//            doListener?.invoke()

        }
        .create()

    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
    }
    dialog.show()
}

fun Context.showAlertDialog(text: String, dismissListener: EmptyBlock? = null) {
    val dialog = AlertDialog
        .Builder(this)
        .setTitle(R.string.alert)
        .setMessage(text)
        .setPositiveButton("Ha") { dialog: DialogInterface?, _: Int ->
            dismissListener?.invoke()
        }
        .setNegativeButton("Yo'q") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        .create()

    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.new_green))
    }
    dialog.show()
}
