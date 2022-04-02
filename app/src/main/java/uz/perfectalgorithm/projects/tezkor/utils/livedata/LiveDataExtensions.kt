package uz.perfectalgorithm.projects.tezkor.utils.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> MediatorLiveData<*>.addSourceDisposable(
    liveData: LiveData<T>,
    onChange: (T) -> Unit
): MediatorLiveData<*> {
    addSource(liveData) {
        onChange(it)
        removeSource(liveData)
    }
    return this
}

fun <T> MediatorLiveData<*>.addSourceDisposableNoAction(liveData: LiveData<T>): MediatorLiveData<*> {
    addSource(liveData) {
        removeSource(liveData)
    }
    return this
}

fun <T> MediatorLiveData<*>.addSourceNonDisposable(
    liveData: LiveData<T>,
    onChange: (T) -> Unit
): MediatorLiveData<*> {
    addSource(liveData) {
        onChange(it)
    }
    return this
}
