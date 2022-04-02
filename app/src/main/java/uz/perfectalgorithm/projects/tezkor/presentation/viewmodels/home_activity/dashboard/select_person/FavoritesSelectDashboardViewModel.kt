package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard.select_person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.domain.home.dashboard.DashboardRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/25/21 10:44 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.others.adding_helpers
 **/
@HiltViewModel
class FavoritesSelectDashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _favoritesBelow =
        MutableStateFlow<DataWrapper<List<StaffBelow>>>(DataWrapper.Empty())
    val favoritesBelow: StateFlow<DataWrapper<List<StaffBelow>>> =
        _favoritesBelow.asStateFlow()

    init {
        initFavoritesBelow()
    }

    fun initFavoritesBelow() = viewModelScope.launch(Dispatchers.IO) {
        _favoritesBelow.value = DataWrapper.Loading()
        _favoritesBelow.value = repository.getFavoritesBelow()
    }
}