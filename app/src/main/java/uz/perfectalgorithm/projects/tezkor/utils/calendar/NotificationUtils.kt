package uz.perfectalgorithm.projects.tezkor.utils.calendar

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.receiver.NotificationReceiver
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.CoroutinesScope
import uz.perfectalgorithm.projects.tezkor.utils.showToast

fun Context.rescheduleReminder(event: CalendarResponse.Event?) {
    if (event != null) {
        cancelPendingIntent(event.idLocalBase)
        cancelNotification(event.idLocalBase)
        if (DateTime(event.startTime).isAfterNow || DateTime(event.untilDate).isAfterNow || event.untilDate.isNullOrEmpty()) {
            nextEventReminder(
                event
            )
        }
    }
}

fun Context.cancelNotification(id: Int) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(id)
}

fun Context.scheduleEventIn(notifTS: Long, event: CalendarResponse.Event, showToasts: Boolean) {
    if (notifTS < System.currentTimeMillis()) {
        if (showToasts) {
            showToast(getString(R.string.calendar))
        }
        return
    }

    val newNotifTS = notifTS + 1000

    val pendingIntent = getNotificationIntent(event)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            newNotifTS,
            pendingIntent
        )
    } catch (e: Exception) {
        showToast("${e.message}")
    }
}

fun Context.getNotificationIntent(event: CalendarResponse.Event): PendingIntent {
    val intent = Intent(this, NotificationReceiver::class.java)
    intent.putExtra(EVENT_ID, event.idLocalBase)
    intent.putExtra(EVENT_OCCURRENCE_TS, event.startTime)
    return PendingIntent.getBroadcast(
        this,
        event.idLocalBase,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

fun Context.cancelPendingIntent(id: Int) {
    val intent = Intent(this, NotificationReceiver::class.java)
    PendingIntent.getBroadcast(
        this,
        id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    ).cancel()
}


fun Context.scheduleEventReminder(event: CalendarResponse.Event, minusMinutes: Int) {
    val pendingIntent = getNotificationIntent(event)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            DateTime(event.startTime).withSecondOfMinute(0)
                .minusMinutes(minusMinutes).millis,
            pendingIntent
        )
    } catch (e: Exception) {
        showToast(e.message.toString())
    }
}

fun Context.nextEventReminder(event: CalendarResponse.Event) {
    val now = getNowSeconds() + 1
    if (event.repeat != getString(R.string.once_)) {
        getEvents(now, now + YEAR, event, true) {
            for (currEvent in it) {
                for (reminder in currEvent.reminder.sorted().reversed()) {
                    if (DateTime(currEvent.startTime).minusMinutes(reminder)
                            .isAfter(DateTime.now())
                    ) {
                        scheduleEventReminder(currEvent, reminder)
                        return@getEvents
                    }
                }
            }
        }
    } else {
        for (reminder in event.reminder.sorted().reversed()) {
            if (DateTime(event.startTime).minusMinutes(reminder)
                    .isAfter(DateTime.now())
            ) {
                scheduleEventReminder(event, reminder)
                break
            }
        }
    }
}


fun Context.notifyEvent(event: CalendarResponse.Event) {
    val pendingIntent = getPendingIntent(applicationContext, event)

    CoroutinesScope.io {
        val channelId =
            "notfication_calendar_${event.idLocalBase}"
        val notification = getNotification(pendingIntent, event, channelId)
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
            notificationManager.notify(event.idLocalBase, notification)
        } catch (e: Exception) {
            showToast("Error")
        }
    }
}


private fun getPendingIntent(context: Context, event: CalendarResponse.Event): PendingIntent {
    when (event.type) {
        "dating" -> {
            val args = bundleOf("datingId" to event.idType)

            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.datingDetailsFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        "meeting" -> {
            val args = bundleOf("meetingId" to event.idType)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.meetingDetailsFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        "note" -> {
            val args = bundleOf(EVENT_ID to event.idType)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.navigation_detail_note)
                .setArguments(args)
                .createPendingIntent()
        }
        "task" -> {
            val args = bundleOf("taskId" to event.idType)
            return NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.home_navigation)
                .setDestination(R.id.taskDetailUpdateFragment)
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


fun Context.getNotification(
    pendingIntent: PendingIntent,
    event: CalendarResponse.Event,
    channelId: String
): Notification {


    val contentType = when (event.type) {
        "task" -> "vazifa"
        "dating" -> "uchrashuv"
        "meeting" -> "majlis"
        "note" -> "eslatma"
        else -> ""
    }

    val contentTitle = event.title

    val builder = NotificationCompat.Builder(this, channelId)
        .setContentTitle(contentType)
        .setContentText(contentTitle)
        .setSmallIcon(R.drawable.ic_calendar)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_LIGHTS)
        .setCategory(Notification.CATEGORY_EVENT)
        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
        .setAutoCancel(true)

    return builder.build()
}

fun Context.deleteEvents(ids: MutableList<Int>) {
    if (ids.isEmpty()) {
        return
    }

    ids.forEach {
        cancelNotification(it)
        cancelPendingIntent(it)
    }
}