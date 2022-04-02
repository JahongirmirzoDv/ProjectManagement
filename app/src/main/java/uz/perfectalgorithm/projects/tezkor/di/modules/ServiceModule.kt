package uz.perfectalgorithm.projects.tezkor.di.modules

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import uz.perfectalgorithm.projects.tezkor.R


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {


    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent,
        channelId: String
    ): NotificationCompat.Builder = NotificationCompat.Builder(app, channelId)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_calendar)
        .setContentTitle("Event title")
        .setContentIntent(pendingIntent)

}
