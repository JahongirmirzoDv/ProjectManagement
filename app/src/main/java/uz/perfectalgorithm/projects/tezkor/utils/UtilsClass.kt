package uz.perfectalgorithm.projects.tezkor.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import java.io.File


fun Context.showToast(st: String) {
    Toast.makeText(this, st, Toast.LENGTH_SHORT).show()
}


fun ViewPager.pageChangeListener(f: (Int) -> Unit) =
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            f.invoke(position)
        }
    })


fun timberLog(st: String, tag: String = "TTT") {
    Timber.tag(tag).d(st)
}


/*fun Activity.checkPermissions(permission: Array<String>, granted: () -> Unit) {
    Permissions.check(
        this,
        permission,
        null, null,
        permissionHandler(granted, { goToSettings() }, { goToSettings() }, { goToSettings() })
    )
}*/

const val REQUEST_APP_SETTINGS = 11001
fun Activity.goToSettings() {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivityForResult(intent, REQUEST_APP_SETTINGS)
}

/*
private fun permissionHandler(
    granted: () -> Unit,
    denied: () -> Unit,
    justBlocked: () -> Unit,
    blocked: () -> Unit
) = object : PermissionHandler() {
    override fun onGranted() {
        granted()
    }

    override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
        super.onDenied(context, deniedPermissions)
        denied()
    }

    override fun onJustBlocked(
        context: Context?,
        justBlockedList: ArrayList<String>?,
        deniedPermissions: ArrayList<String>?
    ) {
        super.onJustBlocked(context, justBlockedList, deniedPermissions)
        justBlocked()
    }

    override fun onBlocked(context: Context?, blockedList: ArrayList<String>?): Boolean {
        blocked()
        return super.onBlocked(context, blockedList)
    }
}
*/


fun SearchView.onQueryListener(
    mHandler: Handler,
    time: Long,
    listener: (newText: String?) -> Unit
) =
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            listener.invoke(query)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                listener.invoke(newText)
            }, time)
            return true
        }
    })

fun File.toRequestData(fieldName: String): MultipartBody.Part {
    val fileReqBody = this.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(fieldName, this.name, fileReqBody)
}

fun String.translateStatusTask(str: String): String {
    return when (this) {
        "new" -> {
            return when (str) {
                "en" -> "New"
                "ru" -> "Новый"
                else -> "Yangi"
            }
        }
        "began" -> {
            return when (str) {
                "en" -> "Began"
                "ru" -> "Началось"
                else -> "Boshlangan"
            }
        }
        "confirmed" -> {
            return when (str) {
                "en" -> "Confirmed"
                "ru" -> "Подтвержденный"
                else -> "Tasdiqlangan"
            }
        }
        "done" -> {
            return when (str) {
                "en" -> "Done"
                "ru" -> ""
                else -> "Bajarilgan"
            }
        }
        "rejected" -> {
            return when (str) {
                "en" -> "Rejected"
                "ru" -> "Сделанный"
                else -> "Bekor qilingan"
            }
        }
        "checked_by_owner" -> {
            return when (str) {
                "en" -> "Checked by owner"
                "ru" -> "Проверено владельцем"
                else -> "Boshliq tekshirgan"
            }
        }
        "checked_by_leader" -> {
            return when (str) {
                "en" -> "Checked by leader"
                "ru" -> "Проверено лидером"
                else -> "Bo'lim boshlig'i tekshirgan"
            }
        }
        else -> {
            return when (str) {
                "en" -> "Confirm rejection"
                "ru" -> "Подтвердить отказ"
                else -> "Tasdiqlangan bekor"
            }
        }
    }
}

fun String.translateStatusMeeting(str: String): String {
    return when (this) {
        "started" -> {
            return when (str) {
                "en" -> "Started"
                "ru" -> "Начал"
                else -> "Boshlangan"
            }
        }
        "finished" -> {
            return when (str) {
                "en" -> "Finished"
                "ru" -> "Законченный"
                else -> "Bo'lim boshlig'i tekshirgan"
            }
        }
        else -> {
            return when (str) {
                "en" -> "Not started"
                "ru" -> "Не начато"
                else -> "Boshlanmagan"
            }
        }
    }
}

fun Boolean.translateStatusNote(str: String): String {
    return if (this) {
        when (str) {
            "en" -> "Active"
            "ru" -> "Активный"
            else -> "Aktiv"
        }
    } else {
        when (str) {
            "en" -> "Non active"
            "ru" -> "Не активен"
            else -> "Non aktiv"
        }
    }
}

fun String.translateWeeks(context: Context): String {
    return context.resources.getString(
        when (this) {
            "Dushanba" -> R.string.monday
            "Seshanba" -> R.string.tuesday
            "Chorshanba" -> R.string.wednesday
            "Payshanba" -> R.string.thursday
            "Juma" -> R.string.friday
            "Shanba" -> R.string.saturday
            else -> R.string.sunday
        }
    )
}

fun String.translateMonth(context: Context): String {
    return context.resources.getString(
        when (this) {
            "Yanvar" -> R.string.january
            "Fevral" -> R.string.february
            "Mart" -> R.string.march
            "Aprel" -> R.string.april
            "May" -> R.string.may
            "Iyun" -> R.string.june
            "Iyul" -> R.string.july
            "Avgust" -> R.string.august
            "Sentyabr" -> R.string.september
            "Oktyabr" -> R.string.october
            "Noyabr" -> R.string.november
            else -> R.string.december
        }
    )
}

fun String.translateTimes(context: Context): String {
    return context.resources.getString(
        when (this) {
            "Kunlik" -> R.string.day
            "Haftalik" -> R.string.week
            "Oylik" -> R.string.monthly
            "Yillik" -> R.string.yearly
            else -> R.string.all
        }
    )
}


