package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.setting

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalDatabase
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentSettingBinding
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/***
 * bu others qismi uchun setting bo'limi
 * bu yerga google integratsiya qo'shish kerak
 * */

@AndroidEntryPoint
class SettingFragment : Fragment(), CoroutineScope {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var job: Job

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var localDatabase: LocalDatabase

    @Inject
    lateinit var credential: GoogleAccountCredential

    @Inject
    lateinit var client: com.google.api.services.calendar.Calendar

    private lateinit var googleSignIn: GoogleSignInClient

    private var calendarID = ""
    private var token: String? = null
    private val TAG = "SettingFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        job = Job()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val build =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            googleSignIn = GoogleSignIn.getClient(requireContext(), build)

            rlChangeLan.setOnClickListener { findNavController().navigate(R.id.languageFragment) }


            rlAddCalendar.setOnClickListener {
                chooseAccount()
            }
        }
    }

    fun chooseAccount() {
        val signInIntent = googleSignIn.signInIntent
        launcher.launch(signInIntent)
    }

    //
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it != null) {
//                Log.e(TAG, "result code : ${it.data?.dataString}")
//                Log.e(TAG, "result code : ${it.resultCode}")
                if (it.resultCode == RESULT_OK) {
                    Toast.makeText(requireContext(), R.string.saving, Toast.LENGTH_SHORT).show()
                    val signedInAccountFromIntent =
                        GoogleSignIn.getSignedInAccountFromIntent(it.data) as Task<GoogleSignInAccount>
                    handleSignInResult(signedInAccountFromIntent)
                }
            }
        }

    fun handleSignInResult(complete: Task<GoogleSignInAccount>) {
        try {
            val account = complete.getResult(ApiException::class.java) as GoogleSignInAccount
            storage.googleCalendarAccountName = account.account.name
            storage.calendarID = account.email
            credential.selectedAccountName = account.account.name
            calendarID = account.email
            Log.d("GoogleCalendarFragment", "signInResult:failed code=${account.displayName}")
//            syncGoogleCalendar()
            getCalendar()
//            getListCalendar()
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), "exception", Toast.LENGTH_SHORT).show()
//            Log.d("GoogleCalendarFragment", "signInResult:failed code=${e.statusCode}")
        }
    }

    fun getCalendar() {
        launch {
            var pageToken: String? = null
            do {
                val events: Events =
                    client.events().list("primary").setPageToken(pageToken).execute();
                val items = events.items
                var i = 0
                for (event in items) {
                    i++
//                    Log.d("calendar page $i", "getCalendar: ${event.summary} - ${event.description}")
                }
                pageToken = events.nextPageToken
            } while (pageToken != null)
        }
    }

    private fun syncGoogleCalendar() {
        launch {

            val event: Event = Event()
                .setSummary("custom event insert mobile")
                .setLocation("Shayxontohur tumani, Ташкент, Узбекистан")
                .setDescription("A chance to hear more about Google's developer products.")

            val startDateTime = DateTime("2022-04-24T16:00:00-07:00")
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setStart(start)

            val endDateTime = DateTime("2022-04-24T17:00:00-07:00")
            val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setEnd(end)

            val recurrence = arrayOf("RRULE:FREQ=DAILY", "COUNT=2")
            event.recurrence = listOf(recurrence) as MutableList<String>

            val arrayList = ArrayList<EventAttendee>()
            arrayList.add(EventAttendee().setEmail(calendarID))
            event.attendees = arrayList

            val remindersOverrides = ArrayList<EventReminder>()
            remindersOverrides.add(EventReminder().setMethod("email").setMinutes(24 * 60))
            remindersOverrides.add(EventReminder().setMethod("popup").setMinutes(10))

            val reminders = Event.Reminders().setUseDefault(false).setOverrides(remindersOverrides)
            event.reminders = reminders


            try {
                val e = client.events().insert("$calendarID", event).setOauthToken(token.toString())
                    .execute()
                // Do whatever you want with the Drive service
            } catch (e: UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, 1)
            }
        }
    }

    fun getListCalendar() {
        var pageToken: String? = null
        do {
            val calendarList: CalendarList =
                client.calendarList().list().setPageToken(pageToken).execute()
            val items = calendarList.items
            for (calendarListEntry in items) {
//                Log.d("Calendar List", "getListCalendar: ${calendarListEntry.summary} ")
            }
            pageToken = calendarList.nextPageToken
        } while (pageToken != null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = job

}