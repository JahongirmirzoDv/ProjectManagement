package uz.perfectalgorithm.projects.tezkor.utils.download

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import uz.perfectalgorithm.projects.tezkor.BuildConfig
import uz.perfectalgorithm.projects.tezkor.utils.showToast
import java.io.File
import java.util.*

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import uz.perfectalgorithm.projects.tezkor.app.App


fun Activity.openFile(file: File) {
    Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        addCategory(Intent.CATEGORY_DEFAULT)
        val uri = FileProvider.getUriForFile(
            this@openFile,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        val mimeType = file.toUri().getMimeType()
        if (mimeType == null) {
            showToast("Nomalum fayl turi")
        } else {
            setDataAndType(uri, mimeType)
            try {
                startActivity(this)
            } catch (e: ActivityNotFoundException) {
                showToast("${file.extension.toLowerCase(Locale.getDefault())} turidagi faylni ochish uchun kerakli dastur o'rnatilmagan")
            }
        }
    }
}

fun getMimeType(file: File): String? {
    val extension = file.extension
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

fun Uri.getMimeType() = if (ContentResolver.SCHEME_CONTENT == scheme) {
    val cr = App.instance.applicationContext.contentResolver
    cr.getType(this)
} else {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
    MimeTypeMap.getSingleton().getMimeTypeFromExtension(
        fileExtension.toLowerCase(Locale.getDefault())
    )
}

fun Uri.isImageFile(context: Context) = getExtension(context)?.isImageFile() == true

fun String.isImageFile() = endsWith("jpeg") ||
        endsWith("jpg") ||
        endsWith("png") ||
        endsWith("svg") ||
        endsWith("bmp")

fun Uri.getExtension(context: Context) = if (scheme == ContentResolver.SCHEME_CONTENT) {
    val mime = MimeTypeMap.getSingleton()
    mime.getExtensionFromMimeType(context.contentResolver.getType(this))
} else {
    MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(path.toString())).toString())
}