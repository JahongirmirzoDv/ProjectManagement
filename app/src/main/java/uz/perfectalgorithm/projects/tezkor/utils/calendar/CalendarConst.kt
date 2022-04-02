package uz.perfectalgorithm.projects.tezkor.utils.calendar


const val LOWER_ALPHA = 0.25f
const val MEDIUM_ALPHA = 0.5f
const val HIGHER_ALPHA = 0.75f

const val STORED_LOCALLY_ONLY = 0
const val ROW_COUNT = 6
const val COLUMN_COUNT = 7
const val DAYS_CNT = 42

const val DAY_CODE = "day_code"
const val YEAR_LABEL = "year"
const val EVENT_ID = "event_id"
const val EVENT_TYPE = "event_id"
const val EVENT_TYPE_VS_ID = "event_id_vs_type"
const val EVENT_OCCURRENCE_TS = "event_occurrence_ts"
const val NEW_EVENT_START_TS = "new_event_start_ts"
const val WEEK_START_DATE_TIME = "week_start_date_time"
const val CURRENT_DATE = "current_date"
const val CURRENT_EXCEPTION = "current_exception"
const val YEAR_NAME = "year"

const val MONTHLY_VIEW = 1


const val DEFAULT_START_TIME_NEXT_FULL_HOUR = -1

const val DAY = 86400
const val WEEK = 604800
const val MONTH = 2592001//2592001
const val YEAR = 31536000

const val HOUR_MINUTES = 60
const val DAY_MINUTES = 24 * HOUR_MINUTES
const val WEEK_MINUTES = DAY_MINUTES * 7
const val MONTH_MINUTES = DAY_MINUTES * 30
const val YEAR_MINUTES = DAY_MINUTES * 365

const val MINUTE_SECONDS = 60
const val HOUR_SECONDS = HOUR_MINUTES * 60
const val DAY_SECONDS = DAY_MINUTES * 60
const val WEEK_SECONDS = WEEK_MINUTES * 60
const val MONTH_SECONDS = MONTH_MINUTES * 60
const val YEAR_SECONDS = YEAR_MINUTES * 60

const val FLAG_ALL_DAY = 1
const val FLAG_IS_PAST_EVENT = 2

const val MONDAY_SUM = 1
const val TUESDAY_SUM = 2
const val WEDNESDAY_SUM = 4
const val THURSDAY_SUM = 8
const val FRIDAY_SUM = 16
const val SATURDAY_SUM = 32
const val SUNDAY_SUM = 64
const val EVERY_DAY =
    MONDAY_SUM or TUESDAY_SUM or WEDNESDAY_SUM or THURSDAY_SUM or FRIDAY_SUM or SATURDAY_SUM or SUNDAY_SUM
const val WORK_DAY = MONDAY_SUM or TUESDAY_SUM or WEDNESDAY_SUM or THURSDAY_SUM or FRIDAY_SUM
const val WEEKEND_DAY = SATURDAY_SUM or SUNDAY_SUM


// repeat_rule for monthly and yearly repetition
const val REPEAT_NOT_SELECTED =
    0                           // i.e. 25th every month, or 3rd june (if yearly repetition)
const val REPEAT_SAME_DAY =
    1                           // i.e. 25th every month, or 3rd june (if yearly repetition)
const val REPEAT_ORDER_WEEKDAY =
    2                      // i.e. every 4th sunday, even if a month has 4 sundays only (will stay 4th even at months with 5)
const val REPEAT_LAST_DAY = 3                           // i.e. every last day of the month
const val REPEAT_ORDER_WEEKDAY_USE_LAST =
    4             // i.e. every last sunday. 4th if a month has 4 sundays, 5th if 5 (or last sunday in june, if yearly)


const val VIEW_STATE = "viewState"
const val START_DATE = "Start_Date"

const val STAFF_ID = "staff_id"

fun getNowSeconds() = System.currentTimeMillis() / 1000L


