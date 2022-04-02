package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.intro

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val storage: LocalStorage
) : ViewModel() {

    val nextStepLiveData: MutableLiveData<Int> = MutableLiveData()
    val openNextScreenLiveData: MutableLiveData<Unit> = MutableLiveData()

    fun nextStep(pos: Int) {
        if (pos > 2) {
            storage.completeIntro = true
            openNextScreenLiveData.value = Unit
        } else nextStepLiveData.value = pos
    }
}
