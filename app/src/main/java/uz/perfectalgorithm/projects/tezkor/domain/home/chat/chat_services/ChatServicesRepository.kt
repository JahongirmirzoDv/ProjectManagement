package uz.perfectalgorithm.projects.tezkor.domain.home.chat.chat_services

import androidx.lifecycle.LiveData



interface ChatServicesRepository {
    fun downloadFile(imageUrl: String): LiveData<String>
}