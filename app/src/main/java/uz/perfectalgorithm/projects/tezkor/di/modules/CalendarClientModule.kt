package uz.perfectalgorithm.projects.tezkor.di.modules

import android.content.Context
import androidx.compose.ui.graphics.colorspace.Illuminant.B
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CalendarClientModule {

    @Provides
    @Singleton
    fun getClientCalendar(
        transport: HttpTransport,
        jsonFactory: GsonFactory,
        credential: GoogleAccountCredential
    ): com.google.api.services.calendar.Calendar{
       return com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, credential
        ).setApplicationName("project-851908959408").build()
    }

    @Provides
    @Singleton
    fun getCredential(@ApplicationContext context: Context): GoogleAccountCredential =
        GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(
                CalendarScopes.CALENDAR
            )
        )

    @Provides
    @Singleton
    fun getTransport(): HttpTransport = AndroidHttp.newCompatibleTransport()

    @Provides
    @Singleton
    fun getJsonFactory(): GsonFactory = GsonFactory.getDefaultInstance()
}