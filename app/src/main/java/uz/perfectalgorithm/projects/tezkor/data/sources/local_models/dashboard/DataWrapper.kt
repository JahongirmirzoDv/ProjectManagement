package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard

/**
 *Created by farrukh_kh on 7/15/21 2:00 PM
 *uz.rdo.projects.projectmanagement.data.sources.local_models.dashboard
 **/

sealed class DataWrapper<T> {
    class Empty<T> : DataWrapper<T>()
    class Loading<T> : DataWrapper<T>()
    data class Success<T>(val data: T) : DataWrapper<T>()
    data class Error<T>(val error: Exception) : DataWrapper<T>()
}