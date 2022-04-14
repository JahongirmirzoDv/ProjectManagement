package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingDataBody
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingIdea
import uz.perfectalgorithm.projects.tezkor.domain.home.rating_idea.RatingRepository
import javax.inject.Inject


@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingIdeaRepository: RatingRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _getRatingIdeaLiveData = MutableLiveData<RatingIdea>()
    val getRatingIdeaLiveData: LiveData<RatingIdea> get() = _getRatingIdeaLiveData

    private val _rateIdeaLiveData = MutableLiveData<RatingDataBody>()
    val rateIdeaLiveData: LiveData<RatingDataBody> get() = _rateIdeaLiveData

    fun getRatingIdea(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            ratingIdeaRepository.getRatingIdea(id).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _getRatingIdeaLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: ${throwable.cause.toString()}")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun rateIdea(id: Int, data: RatingDataBody) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            ratingIdeaRepository.rateIdea(id, data).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _rateIdeaLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: ${throwable.cause.toString()}")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}