package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import java.io.File
import javax.inject.Inject


/**
 * Created by Jasurbek Kurganbaev on 02.07.2021 17:11
 * Umumiy o'zgaruvchi maydonlar uchun SharedViewModel yaratilingan
 **/
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    val participants = MutableLiveData<MutableList<PersonData>?>()
    val spectators = MutableLiveData<MutableList<PersonData>?>()
    val functionalGroup = MutableLiveData<MutableList<PersonData>?>()
    val performer = MutableLiveData<PersonData?>()
    val master = MutableLiveData<PersonData?>()
    val files = MutableLiveData<ArrayList<File>?>()


    fun clear() {
        participants.value = null
        spectators.value = null
        functionalGroup.value = null
        performer.value = null
        master.value = null
        files.value = null
    }
}