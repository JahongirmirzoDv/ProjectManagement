package uz.perfectalgorithm.projects.tezkor.utils.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment


fun Fragment.phoneCall(view: View, phoneNumber: String) {
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:+$phoneNumber")
    startActivity(dialIntent)
}

fun Activity.phoneCall(view: View, phoneNumber: String) {
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:+$phoneNumber")
    startActivity(dialIntent)
}