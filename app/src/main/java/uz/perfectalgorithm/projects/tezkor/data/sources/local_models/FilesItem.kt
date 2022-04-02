package uz.perfectalgorithm.projects.tezkor.data.sources.local_models

import android.net.Uri
import android.os.Environment
import uz.perfectalgorithm.projects.tezkor.app.App
import java.io.File

data class FilesItem(
    val id: Int,
    val title: String,
    val path: String,
    val type: String,
    val viewType: Int,
    val size: Long,
    var progress: Int = 0,
    var isDownloading: Boolean = false
) {
    val uriFile: Uri
        get() = Uri.parse(path)
    val file: File
        get() = File(
            App.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            uriFile.lastPathSegment
        )
}