package uz.perfectalgorithm.projects.tezkor.data.sources.local

import android.content.Context
import android.content.SharedPreferences
import uz.perfectalgorithm.projects.tezkor.utils.calendar.MONTHLY_VIEW
import uz.perfectalgorithm.projects.tezkor.utils.helper.*

class LocalStorage private constructor(context: Context) {
    companion object {

        @Volatile
        lateinit var instance: LocalStorage
            private set

        fun init(context: Context) {
            synchronized(this) {
                instance = LocalStorage(context)
            }
        }
    }

    val pref: SharedPreferences =
        context.getSharedPreferences("LocalStorage", Context.MODE_PRIVATE)
    var logged: Boolean by BooleanPreference(pref, false)
    var completeIntro: Boolean by BooleanPreference(pref, false)
    var token: String by StringPreference(pref)
    var refreshToken: String by StringPreference(pref)
    var id: String by StringPreference(pref)
    var userId: Int by IntPreference(pref)


    var userFirstName: String by StringPreference(pref, "Siz")
    var userLastName: String by StringPreference(pref, "")
    var userImage: String by StringPreference(pref, "")

    var calendarAccountName: String by StringPreference(pref, "")
    var calendarID: String by StringPreference(pref, "")

    var role: String by StringPreference(pref)
    var lan: String by StringPreference(pref)

    var isChosenCompany: Boolean by BooleanPreference(pref, false)

    var selectedCompanyName: String by StringPreference(pref)

    var selectedCompanyId: Int by IntPreference(pref, 0)

    /*variables for Create Conditions*/
    var participant: String by StringPreference(pref)


    var storedView: Int by IntPreference(pref, MONTHLY_VIEW)

//    var spinner: String by StringPreference(pref, OWN_TASK)

    /*ChAT*/
    var chatId: Int by IntPreference(pref, 9)
    var chatIdGroup: Int by IntPreference(pref, 9)

    /*Quick Idea*/
    var ideaBoxId: Int by IntPreference(pref)
    var isGeneralIdeaBox: Boolean by BooleanPreference(pref, false)
    var ideaId: Int by IntPreference(pref)


    //notification
    var firebaseToken: String by StringPreference(pref)

    //SelectPerson frags
    var persons: Set<String> by StringSetPreference(pref)

    var selectedDepartmentId: Int by IntPreference(pref)


    //calendar
    var rowHeight: Float by FloatPreference(pref)

    var commentType: String by StringPreference(pref)
    var googleCalendarAccountName: String by StringPreference(pref)
    var parentTaskId: Int by IntPreference(pref, 0)
}
