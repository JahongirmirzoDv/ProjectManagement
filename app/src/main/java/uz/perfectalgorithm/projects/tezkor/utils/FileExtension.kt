package uz.perfectalgorithm.projects.tezkor.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.utils.download.getMimeType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*

/**
 * Created by Jasurbek Kurganbaev on 09.07.2021 16:56
 **/

fun prepareImageFilePart(partName: String, file: File): MultipartBody.Part {
    val requestFile = file.asRequestBody(file.toUri().getMimeType()?.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        partName, file.name, requestFile
    )
}

fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    } catch (e: IOException) {
        null
    }
}


fun Bitmap.saveToInternalStorage(context: Context): Uri? {
    // get the context wrapper instance
    val wrapper = ContextWrapper(context)

    // initializing a new file
    // bellow line return a directory in internal storage
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    // create a file to save the image
    file = File(file, "${UUID.randomUUID()}.jpg")

    return try {
        // get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // compress bitmap
        compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // flush the stream
        stream.flush()

        // close stream
        stream.close()

        // return the saved image uri
        Uri.parse(file.absolutePath)
    } catch (e: IOException) { // catch the exception
        e.printStackTrace()
        null
    }
}

fun App.saveImageToGallery(bitmap: Bitmap): Uri {

    // Save image to gallery
    val savedImageURL = MediaStore.Images.Media.insertImage(
        contentResolver,
        bitmap,
        "${UUID.randomUUID()}",
        "Image of ${UUID.randomUUID()}"
    )

    // Parse the gallery image url to uri
    return Uri.parse(savedImageURL)
}

