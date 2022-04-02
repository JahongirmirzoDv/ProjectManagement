package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.idea_comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.CommentData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.PostCommentBody
import uz.perfectalgorithm.projects.tezkor.domain.home.idea_comment.IdeaCommentRepository
import uz.perfectalgorithm.projects.tezkor.utils.isAvailableNetwork
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 04.08.2021 14:17
 **/
@HiltViewModel
class IdeaCommentViewModel @Inject constructor(
    private val ideaCommentRepository: IdeaCommentRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    /*  private val _notConnectionLiveData = MutableLiveData<Unit>()
      val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData
  */
    val notConnectionLiveData = MutableLiveData<Unit>()


    private val _postIdeaCommentLiveData = MutableLiveData<CommentData>()
    val postIdeaCommentLiveData: LiveData<CommentData> get() = _postIdeaCommentLiveData

    private val _getIdeaCommentList = MutableLiveData<List<CommentData>>()
    val getIdeaCommentList: LiveData<List<CommentData>> get() = _getIdeaCommentList

    fun postIdeaComment(data: PostCommentBody) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            ideaCommentRepository.postComment(data).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")
                    _postIdeaCommentLiveData.postValue(userData.data!!)
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


    fun getIdeaCommentList(): Flow<PagingData<CommentData>> {
        /*if (isConnected()) {
            _progressLiveData.value = true
            viewModelScope.launch(Dispatchers.IO) {
                ideaCommentRepository.getCommentList(data).collect {
                    it.onSuccess { userData ->
                        Log.d("TTTT", "onSuccess : $it")
                        _getIdeaCommentList.postValue(userData.data!!)
                        _progressLiveData.postValue(false)
                    }

                    it.onFailure { throwable ->
                        Log.d("TTTT", "onFailure: ${throwable.cause.toString()}")
                        _progressLiveData.postValue(false)
                        _errorLiveData.postValue(throwable.message)
                    }

                }
            }
        }*/

        if (!App.instance.isAvailableNetwork())
            notConnectionLiveData.value = Unit
        return ideaCommentRepository.getCommentList(viewModelScope)
    }

}