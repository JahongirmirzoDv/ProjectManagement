package uz.perfectalgorithm.projects.tezkor.utils

import android.annotation.SuppressLint
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object DateUtil {

    fun convertDate(date: String): String {
        val formatIn = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        val sdfIn = SimpleDateFormat(formatIn, Locale.getDefault())
        val dateIn = sdfIn.parse(date)
        val formatOut = "dd-MMMM, HH:mm"
        val sdfOut = SimpleDateFormat(formatOut, Locale.getDefault())
        val dateOut = sdfOut.format(dateIn)
        return dateOut
    }

    fun String.getTimeFormatFromDate(): String {
        val formatIn = "yyyy-mm-dd HH:mm"
        val sdfIn = SimpleDateFormat(formatIn, Locale.getDefault())
        val dateIn = sdfIn.parse(this)
        val formatOut = "HH:mm"
        val sdfOut = SimpleDateFormat(formatOut, Locale.getDefault())
        return sdfOut.format(dateIn)
    }

    fun getCurrentDateStamp(): String? {
        val sdfDate =
            SimpleDateFormat("yyyy-MM-dd")
        val now = Date()
        return sdfDate.format(now)
    }


    fun getTimeForChat(sDate: String): String {

        myLogD("Current Day : ${getCurrentDateStamp()}", "TAG_CD")

        return if (sDate.getDateDay() == getCurrentDateStamp()) {
            sDate.getTimeFormatFromDate()
        } else {
            sDate.getDateDay()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getWeekDays(): ArrayList<Int> {
        val sC = Calendar.getInstance()
        val cC = Calendar.getInstance()
        val currentDay = Date()

        val sDate = SimpleDateFormat("yyyy-mm-dd").parse("2021-09-21")
        val cDate = SimpleDateFormat("yyyy-mm-dd").parse(getCurrentDateStamp())


        sC.time = sDate // yourdate is an object of type Date
        cC.time = cDate // yourdate is an object of type Date

        val sDayOfWeek = sC[Calendar.DAY_OF_WEEK]
        val cDayOfWeek = cC[Calendar.DAY_OF_WEEK]

        val weekDays = ArrayList<Int>()
        weekDays.add(cDayOfWeek)
        weekDays.add(sDayOfWeek)

        return weekDays

    }

    fun String.getDateDay(): String {
        return this.split(" ")[0]
    }

    fun typeConverterDateByLong(givenDateString: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val mDate: Date = sdf.parse(givenDateString)
        return mDate.time
    }

    fun convertStringToDate(date: String): Date {
        val formatIn = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        val sdfIn = SimpleDateFormat(formatIn, Locale.getDefault())
        return sdfIn.parse(formatIn)
    }
}