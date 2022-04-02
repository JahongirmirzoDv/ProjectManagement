package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.RegistrationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.ResetPasswordResponse
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 9/18/2021 10:39 AM
 **/
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private var _resetPassword =
        MutableSharedFlow<DataWrapper<ResetPasswordResponse.ResetPasswordNotifyResponse>>(
            replay = 0
        )

    val resetPasswordNotifyResponse: SharedFlow<DataWrapper<ResetPasswordResponse.ResetPasswordNotifyResponse>> =
        _resetPassword

    fun sendPhoneAndNewPassword(phone: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _resetPassword.emit(DataWrapper.Loading())
            val resetPasswordRequest = RegistrationRequest(phoneNumber = phone)
            _resetPassword.emit(
                repository.resetPhoneNumberForNewPasswordVerification(
                    resetPasswordRequest
                )
            )


        }


}
