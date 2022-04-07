package uz.perfectalgorithm.projects.tezkor.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.NotificationChatModel
import uz.perfectalgorithm.projects.tezkor.utils.notification.pushNotifyEvent
import uz.perfectalgorithm.projects.tezkor.utils.notification.pushNotifyEventChat
import javax.inject.Inject


/***
 * Bu firebasedan keladigan baza uchun service
 */
@AndroidEntryPoint
class FMService : FirebaseMessagingService() {

    @Inject
    lateinit var storage: LocalStorage

    override fun onNewToken(p0: String) {
        storage.firebaseToken = p0
        Timber.tag("fcm").d(p0)
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.tag("fcm").d("keldi="+remoteMessage.data.toString())
        val type = remoteMessage.data["type"].toString() //chat task
        val action = remoteMessage.data["action"].toString() //create
        val title = remoteMessage.data["title"].toString() //
        val id = remoteMessage.data["id"]!!.toInt()
        val chatData = remoteMessage.data["additional_data"]

        if (chatData.isNullOrEmpty()) {
            pushNotifyEvent(type, id, title, action)
        } else {
            val gson = Gson()
            val chatModel: NotificationChatModel =
                gson.fromJson(chatData, NotificationChatModel::class.java)
//            storage.chatId = chatModel.chatId
            pushNotifyEventChat(title, chatModel)
        }
    }
}