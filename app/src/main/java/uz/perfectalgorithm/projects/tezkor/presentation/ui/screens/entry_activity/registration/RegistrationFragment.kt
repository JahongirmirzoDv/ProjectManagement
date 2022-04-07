package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.RegisterErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.VerificationRequest
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentRegistrationOwnerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.registration.RegistrationViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible


/***
 * bu Kirishdagi Ownerni registratsiya qilish bo'limi
 */

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationOwnerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels()
    private var firstName = ""
    private var lastName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationOwnerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObserver()
    }

    private fun loadViews() {
        binding.apply {

            countryCodePickerView.setNumberAutoFormattingEnabled(true)
            countryCodePickerView.registerCarrierNumberEditText(etPhone)

            btnRegistration.setOnClickListener {
                registration()
            }
            tvBackSystem.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun registration() {
        binding.apply {

            val phoneNumber = etPhone.text.toString().replace(" ", "")
            val password = etPassword.text.toString()
            val passwordConfirm = etPasswordConfirm.text.toString()

            if (phoneNumber.length != 9) {
                binding.etPhone.error = getString(R.string.invalid_phone_number)
                return
            }
            if (password.length < 6) {
                binding.etPassword.error = getString(R.string.invalid_password)
                return
            }

            if (etUsername.text.isNullOrEmpty()) {
                binding.etUsername.error = getString(R.string.enter_username)
                return
            }

            if (passwordConfirm.isBlank() || passwordConfirm != password) {
                binding.etPasswordConfirm.error = getString(R.string.password_not_confirm)
                return
            }
            viewModel.registration(phone = binding.countryCodePickerView.fullNumber/*"998$phoneNumber"*/)
        }
    }

    private fun loadObserver() {
        this.lifecycleScope.launch {
            viewModel.registerResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnRegistration.isEnabled = true
                        binding.tvBackSystem.isEnabled = true
                        handleCustomException(dataWrapper.error) { response ->
                            when {
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.PHONE_NUMBER_EXISTS.message) == true -> {
                                    binding.etPhone.error =
                                        "Telefon raqam allaqachon ro'yxatdan o'tkazilgan"
                                }
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INVALID_PHONE_NUMBER.message) == true -> {
                                    binding.etPhone.error =
                                        "Telefon raqam noto'g'ri formatda"
                                }
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INCORRECT_PHONE_NUMBER.message) == true -> {
                                    binding.etPhone.error =
                                        "Telefon raqam noto'g'ri formatda"
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
                        binding.progressLayout.progressLoader.visible()
                        binding.btnRegistration.isEnabled = false
                        binding.tvBackSystem.isEnabled = false
                    }
                    is DataWrapper.Success -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnRegistration.isEnabled = true
                        binding.tvBackSystem.isEnabled = true
//                        val phoneNumber = binding.etPhone.text.toString().replace(" ", "")
                        val password = binding.etPassword.text.toString()
                        val username = binding.etUsername.text.toString().split(" ", limit = 2)
                        if (username.size >= 2) {
                            firstName = username.first()
                            lastName = username[1]
                        } else {
                            firstName = username.first()
                            lastName = ""
                        }
                        val verificationRequest =
                            VerificationRequest(
                                firstName,
                                lastName,
                                binding.countryCodePickerView.fullNumber/*"998$phoneNumber"*/,
                                password,
                                ""
                            )
                        findNavController().navigate(
                            R.id.action_registrationFragment_to_verificationFragment,
                            bundleOf(
                                "verification" to verificationRequest
                            )
                        )
                    }
                }
            }
        }
//        viewModel.registrationLiveData.observe(viewLifecycleOwner, registrationObserver)
//        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
//        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
//        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)
    }

//    private val registrationObserver = EventObserver<RegistrationResponse.Result> {
//        val phoneNumber = binding.etPhone.text.toString().replace(" ", "")
//        val password = binding.etPassword.text.toString()
//        val username = binding.etUsername.text.toString().split(" ", limit = 2)
//        if (username.size >= 2) {
//            firstName = username.first()
//            lastName = username[1]
//        } else {
//            firstName = username.first()
//            lastName = ""
//        }
//        val verificationRequest =
//            VerificationRequest(
//                firstName,
//                lastName,
//                binding.countryCodePickerView.fullNumber/*"998$phoneNumber"*/,
//                password,
//                ""
//            )
//        findNavController().navigate(
//            R.id.action_registrationFragment_to_verificationFragment,
//            bundleOf(
//                "verification" to verificationRequest
//            )
//        )
//    }
//
//    private val progressObserver = EventObserver<Boolean> {
//        if (it) {
//            binding.progressBar.visible()
//            binding.btnRegistration.isEnabled = false
//            binding.tvBackSystem.isEnabled = false
//        } else {
//            binding.btnRegistration.isEnabled = true
//            binding.tvBackSystem.isEnabled = true
//            binding.progressBar.gone()
//        }
//    }
//
//    private val notConnectionObserver = EventObserver<Unit> {
//        showToast(resources.getString(R.string.not_connection))
//    }
//
//    private val errorObserver = EventObserver<String> {
//        showToast(it)
//        binding.progressBar.gone()
//        binding.btnRegistration.isEnabled = true
//        binding.tvBackSystem.isEnabled = true
//    }
}
