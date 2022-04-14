package uz.perfectalgorithm.projects.tezkor.data.sources.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


object Coroutines {
    fun <T : Any> ioThenMain(work: suspend (() -> T?), callback: ((T?) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async rt@{
                return@rt work()
            }.await()
            callback(data)
        }

    fun <T : Any> dispatcherThenMain(work: suspend (() -> T?), callback: ((T?) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.Default).async rt@{
                return@rt work()
            }.await()
            callback(data)
        }
}