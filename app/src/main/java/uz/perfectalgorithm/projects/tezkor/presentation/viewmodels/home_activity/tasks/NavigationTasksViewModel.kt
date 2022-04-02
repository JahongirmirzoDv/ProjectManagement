package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 13:17
 **/
@HiltViewModel
class NavigationTasksViewModel @Inject constructor() : ViewModel() {

    private val _isFolderAdapter = MutableStateFlow(true)
    val isFolderAdapter: StateFlow<Boolean> get() = _isFolderAdapter

    fun setIsFolderAdapter(isFolderAdapter: Boolean) {
        _isFolderAdapter.value = isFolderAdapter
    }
}