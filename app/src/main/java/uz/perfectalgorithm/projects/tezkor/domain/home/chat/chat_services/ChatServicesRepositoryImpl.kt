package uz.perfectalgorithm.projects.tezkor.domain.home.chat.chat_services

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.utils.BASE_URL
import uz.perfectalgorithm.projects.tezkor.utils.saveImageToGallery
import uz.perfectalgorithm.projects.tezkor.utils.toBitmap
import java.net.URL
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 18.08.2021
 **/

class ChatServicesRepositoryImpl @Inject constructor(
    private val storage: LocalStorage,
) : ChatServicesRepository {

    private val app = App.instance

    override fun downloadFile(imageUrl: String): LiveData<String> {
        val resultLiveData = MutableLiveData<String>()
        val result: Deferred<Bitmap?> = GlobalScope.async {
            URL(BASE_URL + imageUrl).toBitmap()
        }

        GlobalScope.launch(Dispatchers.Main) {
            val bitmap: Bitmap? = result.await()
            bitmap?.apply {
                val savedUri: Uri? = app.saveImageToGallery(this)
                resultLiveData.value = savedUri.toString()
            }
        }
        return resultLiveData
    }

}