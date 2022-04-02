package uz.perfectalgorithm.projects.tezkor.utils.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.NotificationChatModel
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.CoroutinesScope
import uz.perfectalgorithm.projects.tezkor.utils.showToast


fun Context.pushNotifyEventChat(title: String, chatModel: NotificationChatModel) {
    val pendingIntent = getPendingIntentChat(applicationContext, chatModel)

    CoroutinesScope.io {
        val channelId =
            "notifcation_chat"
        val notification = getNotificationChat(pendingIntent, title, channelId, chatModel)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                chatModel.senderName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        try {
            notificationManager.notify(chatModel.chatId, notification)
        } catch (e: Exception) {
            showToast("Error")
        }
    }
}

private fun getPendingIntentChat(
    context: Context,
    chatModel: NotificationChatModel
): PendingIntent {

    val bundle = bundleOf(
        "receiverFullName" to chatModel.senderName,
        "receiverImage" to chatModel.senderImage
    )

    return NavDeepLinkBuilder(context)
        .setComponentName(HomeActivity::class.java)
        .setGraph(R.navigation.home_navigation)
        .setDestination(R.id.personalConversationFragment)
        .setArguments(bundle)
        .createPendingIntent()

}


private fun Context.getNotificationChat(
    pendingIntent: PendingIntent,
    title: String,
    channelId: String, chatModel: NotificationChatModel
): Notification {


    val builder = NotificationCompat.Builder(this, channelId)
        .setContentTitle(chatModel.senderName)
        .setContentText(if (chatModel.isMedia) "File" else title)
        .setSmallIcon(R.drawable.apk_icon)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(Notification.CATEGORY_EVENT)
        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
        .setAutoCancel(true)

    return builder.build()
}