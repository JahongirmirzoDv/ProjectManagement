package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.reset_password

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.RegisterErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password.ResetPasswordVerificationRequest
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCalendarBinding
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentResetPasswordBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.login.LoginViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.reset_password.ResetPasswordViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.hideKeyboard
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding: FragmentResetPasswordBinding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))

    private val viewModel: ResetPasswordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadViews()
        loadObservers()

    }


    private fun loadObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resetPasswordNotifyResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        handleCustomException(dataWrapper.error) { response ->
                            when {
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INVALID_PHONE_NUMBER.message) == true -> {
                                    binding.resetEtPhone.error = "Mavjuda bo'lmagan telefon raqam"
                                }
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INCORRECT_PHONE_NUMBER.message) == true -> {
                                    binding.resetEtPhone.error = "Telefon raqam noto'g'ri formatda"
                                }
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.WAIT_3_MINUTE.message) == true -> {
                                    makeErrorSnack("3 daqiqadan so'ng qayta urinib ko'ring")
                                }
                                else -> handleException(dataWrapper.error)
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.show()
                    }
                    is DataWrapper.Success -> {

                        binding.progressLayout.progressLoader.gone()
                        hideKeyboard()
                        val phoneNumber = binding.resetCountryCodePickerView.fullNumber
                        val newPassword = binding.etPassword.text.toString()
                        val resetPasswordVerificationRequest =
                            ResetPasswordVerificationRequest(phoneNumber, newPassword, "")
                        findNavController().navigate(
                            R.id.action_resetPasswordFragment_to_verificationFragment,
                            bundleOf(
                                "resetPasswordVerificationRequest" to resetPasswordVerificationRequest
                            )
                        )
                    }
                }
            }
        }

    }

    private fun loadViews() {

        binding.apply {

            resetCountryCodePickerView.setNumberAutoFormattingEnabled(true)
            resetCountryCodePickerView.registerCarrierNumberEditText(resetEtPhone)

            btnContinue.setOnClickListener {
                sendPassword()
            }
        }

    }

    private fun sendPassword() {
        binding.apply {
            val phoneNumber = resetEtPhone.text.toString().replace(" ", "")
            val password = etPassword.text.toString()

            if (/*phoneNumber.length != 9*/ !resetCountryCodePickerView.isValidFullNumber) {
                binding.resetEtPhone.error = getString(R.string.invalid_phone_number)
                return
            }
            if (password.isNullOrBlank()) {
                binding.etPassword.error = getString(R.string.enter_password)
                return
            }
            viewModel.sendPhoneAndNewPassword(resetCountryCodePickerView.fullNumber)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}