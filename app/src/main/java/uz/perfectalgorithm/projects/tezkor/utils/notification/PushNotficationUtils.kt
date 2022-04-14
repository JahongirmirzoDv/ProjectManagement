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
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.CoroutinesScope
import uz.perfectalgorithm.projects.tezkor.utils.showToast


fun Context.pushNotifyEvent(type: String, id: Int, title: String, action: String) {
    val pendingIntent = getPendingIntent(applicationContext, type, id)

    CoroutinesScope.io {
        val channelId =
            "notifcation_${type}"
        val notification = getNotification(pendingIntent, type, title, channelId, action)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "NOTIFICATION_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        try {
            notificationManager.notify(id, notification)
        } catch (e: Exception) {
            showToast("Error")
        }
    }
}

private fun getPendingIntent(context: Context, type: String, id: Int): PendingIntent {

    when (type) {
        "dating" -> {
            val args = bundleOf("datingId" to id)

            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.datingDetailsFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        "meeting" -> {
            val args = bundleOf("meetingId" to id)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.meetingDetailsFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        "task" -> {
            val args = bundleOf("taskId" to id)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.taskDetailUpdateFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        "project" -> {
            val args = bundleOf("projectId" to id)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.projectDetailUpdateFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        "goal" -> {
            val args = bundleOf("goalId" to id)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.goalDetailsFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        else -> {
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.navigation_chat)
                .createPendingIntent()
        }
    }
}

private fun Context.getNotification(
    pendingIntent: PendingIntent,
    type: String, title: String,
    channelId: String, action: String
): Notification {


    var contentType = when (type) {
        "task" -> "vazifa"
        "dating" -> "uchrashuv"
        "meeting" -> "majlis"
        "goal" -> "goal"
        "project" -> "proyekt"
        else -> ""
    }

    contentType += when (action) {
        "create" -> " yaratildi"
        "update" -> " yangilandi"
        "delete" -> " o'chirildi"
        else -> ""
    }

    val builder = NotificationCompat.Builder(this, channelId)
        .setContentTitle(contentType)
        .setContentText(title)
        .setSmallIcon(R.drawable.apk_icon)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(Notification.CATEGORY_EVENT)
        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
        .setAutoCancel(true)

    return builder.build()
}