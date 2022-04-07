package uz.perfectalgorithm.projects.tezkor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.local.calendar.dao.EventDao
import uz.perfectalgorithm.projects.tezkor.utils.calendar.EVENT_ID
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.calendar.nextEventReminder
import uz.perfectalgorithm.projects.tezkor.utils.calendar.notifyEvent
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.CoroutinesScope
import javax.inject.Inject

/***
 * Bu notification qismi uchun reciver
 */

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var eventDao: EventDao

    override fun onReceive(context: Context?, intent: Intent?) {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "calendar:notificationreceiver"
        )
        wakeLock.acquire(3000)
        CoroutinesScope.io {
            handleIntent(context, intent)
        }
    }

    /**
     * handle intent event id orqali bazadan event olinadi
     */
    private fun handleIntent(context: Context?, intent: Intent?) {
        val id = intent?.getIntExtra(EVENT_ID, -1)
        if (id == -1) {
            return
        }
        val event = eventDao.getEventWithId(id!!.toLong())

        if (!event.repetitionExceptions.contains(Formatter.getDayCodeFromDateTime(DateTime(event.startTime)))) {
            context?.notifyEvent(event)
        }
        context?.nextEventReminder(event)
    }
}