package uz.perfectalgorithm.projects.tezkor.utils.`typealias`

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper

/**
 *Created by farrukh_kh on 10/27/21 11:48 PM
 *uz.perfectalgorithm.projects.tezkor.utils.typealias
 **/
typealias StateFlowWrapper<T> = StateFlow<DataWrapper<T>>
typealias MutableStateFlowWrapper<T> = MutableStateFlow<DataWrapper<T>>

@Suppress("FunctionName")
fun <T> MutableStateFlowWrapper(value: DataWrapper<T> = DataWrapper.Empty()) =
    MutableStateFlow(value)