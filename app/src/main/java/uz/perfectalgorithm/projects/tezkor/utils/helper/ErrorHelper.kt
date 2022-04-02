package uz.perfectalgorithm.projects.tezkor.utils.helper

import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App

fun ErrorState.findSuitableState(): String {
    return when (this) {
        ErrorState.NOTFOUND -> App.instance.getString(R.string.user_not_found)
        ErrorState.UNAUTHORIZED -> App.instance.getString(R.string.user_un_authorized)
        ErrorState.FORBIDDEN -> App.instance.getString(R.string.user_forbidden)
        ErrorState.NONETWORK -> App.instance.getString(R.string.user_offline)
        ErrorState.INCORRECTDATA -> App.instance.getString(R.string.incorrect_data)
        ErrorState.LARGE_DATA -> App.instance.getString(R.string.large_data)
        ErrorState.BAD_CREDENTIALS -> App.instance.getString(R.string.user_un_authorized)
        ErrorState.UNKNOWN -> App.instance.getString(R.string.unknown_error_please_try_again)
        else -> App.instance.getString(R.string.unknown_error_please_try_again)
    }
}

sealed class ErrorState {
    object UNKNOWN : ErrorState()
    object UNAUTHORIZED : ErrorState()
    object FORBIDDEN : ErrorState()
    object NOTFOUND : ErrorState()
    object INCORRECTDATA : ErrorState()
    object NONETWORK : ErrorState()
    object BAD_CREDENTIALS : ErrorState()
    object LARGE_DATA : ErrorState()


    companion object {
        private fun detectState(errorCode: Int): ErrorState =
            when (errorCode) {
                400 -> INCORRECTDATA
                401 -> UNAUTHORIZED
                403 -> FORBIDDEN
                404 -> NOTFOUND
                500 -> BAD_CREDENTIALS
                513 -> LARGE_DATA
                else -> UNKNOWN
            }

        fun detectByException(code: Int): String {
            return detectState(code).findSuitableState()
        }
    }
}
//companion object {
//    fun detectState(errorCode: Int): ErrorState =
//        when (errorCode) {
//            401 -> ErrorState.UNAUTHORIZED
//            403 -> ErrorState.FORBIDDEN
//            404 -> ErrorState.NOTFOUND
//            400 -> ErrorState.INCORRECTDATA
//            500 -> ErrorState.BAD_CREDENTIALS
//            else -> ErrorState.UNKNOWN
//        }
//
//    fun detectByException(e: Exception): String  {
//        return when (e) {
//            is HttpException -> detectState(e.code()).findSuitableState()
//            is UnknownHostException -> ErrorState.NONETWORK.findSuitableState()
//            else -> ErrorState.UNKNOWN.findSuitableState()
//        }
//    }
//}