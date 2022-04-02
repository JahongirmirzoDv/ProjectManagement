package uz.perfectalgorithm.projects.tezkor.domain.home.chat.chat_services

import androidx.lifecycle.LiveData

/**
 * Created by Davronbek Raximjanov on 18.08.2021
 **/

interface ChatServicesRepository {
    fun downloadFile(imageUrl: String): LiveData<String>
}