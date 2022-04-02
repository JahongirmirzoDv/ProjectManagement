package uz.perfectalgorithm.projects.tezkor.utils.date_utils

import android.text.TextUtils
import android.text.format.DateUtils
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.utils.date_utils.DateTypes.FORMAT_AS_DATE
import uz.perfectalgorithm.projects.tezkor.utils.date_utils.DateTypes.FORMAT_AS_DATETIME
import uz.perfectalgorithm.projects.tezkor.utils.date_utils.DateTypes.FORMAT_AS_NUMBER
import uz.perfectalgorithm.projects.tezkor.utils.date_utils.DateTypes.YYYYMMDDHHMMSS
import uz.perfectalgorithm.projects.tezkor.utils.date_utils.DateTypes.YYYY_MM_DD_HHMM
import uz.perfectalgorithm.projects.tezkor.utils.date_utils.DateTypes.YYYY_MM_DD_HHMMSS
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Davronbek Raximjanov 11.07.2021
 **/

object DateUtil2 {
    fun convertUTCDateTimetoLocal(dateStr: String): String {
        val df = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.forLanguageTag(Locale.getDefault().toLanguageTag())
        )
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(dateStr)
        df.timeZone = TimeZone.getDefault()
        return df.format(date ?: "")
    }

    val String.getURConvertedTime: String
        get() {
            return try {
                val now = SimpleDateFormat(
                    "yyyy-MM-dd ",
                    Locale.forLanguageTag(Locale.getDefault().toLanguageTag())
                ).format(Date())
                val dateStr = convertUTCDateTimetoLocal(now + this)
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
                    .parseDateTime(dateStr).toString("HH:mm:ss")
            } catch (e: Exception) {
                ""
            }
        }

    val String.getURConvertedCreatedAt: String
        get() {
            return try {
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(
                    convertUTCDateTimetoLocal(this)
                ).toString("dd.MM.yyyy")
            } catch (e: Exception) {
                ""
            }
        }

    fun convert(s: String, fmt: SimpleDateFormat?): String {
        return format(parse(s), fmt)
    }

    @JvmStatic
    fun format(date: Date?, fmt: SimpleDateFormat?): String {
        return try {
            if (date != null) {
                fmt!!.format(date)
            } else throw (NullPointerException("DATE IS NULL"))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun formatLocalToUtc(date: Date?, fmt: SimpleDateFormat?): String {
        return try {
            if (date != null) {
                fmt?.timeZone = TimeZone.getTimeZone("UTC")
                fmt!!.format(date)
            } else throw (NullPointerException("DATE IS NULL"))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    @JvmStatic
    fun format(
        date: Date?,
        fmt: ThreadLocal<SimpleDateFormat?>
    ): String {
        return format(date, fmt.get())
    }

    fun parse(s: String): Date? {
        return if (TextUtils.isEmpty(s)) {
            null
        } else try {
            when (s.length) {
                8 -> parseUtcToLocal(s, FORMAT_AS_NUMBER.get())
                10 -> parseUtcToLocal(s, FORMAT_AS_DATE.get())
                14 -> parseUtcToLocal(s, YYYYMMDDHHMMSS.get())
                19 -> parseUtcToLocal(s, YYYY_MM_DD_HHMMSS.get())
                16 -> parseUtcToLocal(s, YYYY_MM_DD_HHMM.get())
                else -> parseUtcToLocal(s, FORMAT_AS_DATETIME.get())
            }
        } catch (e: ParseException) {
            throw IllegalArgumentException("Date time format error:$s")
        }
    }

    fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    private fun parseUtcToLocal(date: String?, fmt: SimpleDateFormat?): Date? {
        if (date == null || date.isEmpty()) return null
        if (fmt == null) return null
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        val d = fmt.parse(date) ?: return null
        fmt.timeZone = TimeZone.getDefault()
        return fmt.parse(fmt.format(d))
    }

    fun convertLocalToUtc(date: String?, fmt: SimpleDateFormat?): String {
        if (date == null || date.isEmpty()) return ""
        if (fmt == null) return date
        return formatLocalToUtc(parse(date), fmt)
    }

    fun String.convertDateToLong(): Long {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return df.parse(this).time
    }


}