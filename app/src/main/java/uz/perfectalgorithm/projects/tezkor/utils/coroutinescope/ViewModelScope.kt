package uz.perfectalgorithm.projects.tezkor.utils.coroutinescope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *Created by farrukh_kh on 10/28/21 10:44 AM
 *uz.perfectalgorithm.projects.tezkor.utils.coroutinescope
 **/
fun ViewModel.launchCoroutine(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        block.invoke(this)
    }
}