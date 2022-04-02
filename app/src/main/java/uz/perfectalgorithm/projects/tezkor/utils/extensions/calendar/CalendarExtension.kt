package uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar

import android.content.Context
import android.graphics.Color
import android.media.ExifInterface
import android.util.Range
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import java.text.DecimalFormat
import java.util.*

fun Int.getContrastColor(): Int {
    return Color.WHITE
}


fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor)
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}


fun Int.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()
    return "${
        DecimalFormat("#,##0.#").format(
            this / Math.pow(
                1024.0,
                digitGroups.toDouble()
            )
        )
    } ${units[digitGroups]}"
}

fun Int.addBitIf(add: Boolean, bit: Int) =
    if (add) {
        addBit(bit)
    } else {
        removeBit(bit)
    }

fun Int.removeBit(bit: Int) = addBit(bit) - bit

fun Int.addBit(bit: Int) = this or bit


fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start


fun Int.orientationFromDegrees() = when (this) {
    270 -> ExifInterface.ORIENTATION_ROTATE_270
    180 -> ExifInterface.ORIENTATION_ROTATE_180
    90 -> ExifInterface.ORIENTATION_ROTATE_90
    else -> ExifInterface.ORIENTATION_NORMAL
}.toString()


fun Range<Int>.touch(other: Range<Int>) =
    (upper > other.lower && lower < other.upper) || (other.upper > lower && other.lower < upper)

fun Context.hexColor(type: String): String {
    val typeNumber = when (type) {
        "task" -> 0
        "note" -> 1
        "meeting" -> 2
        "dating" -> 3
        else -> 4
    }
    return resources.getStringArray(R.array.colors_events)
        .toMutableList()[typeNumber]
}


fun Any.toInt() = Integer.parseInt(toString())

