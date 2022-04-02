package uz.perfectalgorithm.projects.tezkor.utils.date_utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Davronbek Raximjanov 11.07.2021
 **/

object DateTypes {
    @JvmField
    val FORMAT_AS_NUMBER: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("yyyyMMdd", Locale.forLanguageTag("ru"))
            }
        }
    val FORMAT_AS_DATE: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru"))
            }
        }

    @JvmField
    val FORMAT_AS_CHOOSEN_DATE: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("yyyy-mm-dd", Locale.forLanguageTag("ru"))
            }
        }
    val FORMAT_AS_DATETIME: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.forLanguageTag("ru"))
            }
        }
    val YYYYMMDDHHMMSS: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("yyyyMMddHHmmss", Locale.forLanguageTag("ru"))
            }
        }

    val YYYY_MM_DD_HHMMSS: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.forLanguageTag("ru"))
            }
        }

    val YYYY_MM_DD_HHMM: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.forLanguageTag("ru"))
            }
        }

    val HH_MM: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru"))
            }
        }
    val FORMAT_AS_WEEK_DATE: ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.forLanguageTag("ru"))
            }
        }

    // aa_
}