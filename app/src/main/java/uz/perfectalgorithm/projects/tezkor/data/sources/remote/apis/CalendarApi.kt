package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse

/***
 * Bu calendar qismi uchun api
 */

interface CalendarApi {

    @GET("/api/v1/company/month/{year}/{month}")
    suspend fun getMonthEvents(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Response<CalendarResponse.Result>

    @GET("/api/v1/company/day/{year}/{month}/{day}")
    suspend fun getDayEvents(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Response<CalendarResponse.Result>

    @GET("/api/v1/company/week/{year}/{month}/{day}")
    suspend fun getWeekEvents(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Response<CalendarResponse.Result>

    @GET("/api/v1/user/month/{year}/{month}/{staff_id}/")
    suspend fun getMonthEventsStaff(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("staff_id") staffId: Int
    ): Response<CalendarResponse.Result>

    @GET("/api/v1/user/day/{year}/{month}/{day}/{staff_id}/")
    suspend fun getDayEventsStaff(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Path("staff_id") staffId: Int
    ): Response<CalendarResponse.Result>

    @GET("/api/v1/user/week/{year}/{month}/{day}/{staff_id}/")
    suspend fun getWeekEventsStaff(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Path("staff_id") staffId: Int
    ): Response<CalendarResponse.Result>
}