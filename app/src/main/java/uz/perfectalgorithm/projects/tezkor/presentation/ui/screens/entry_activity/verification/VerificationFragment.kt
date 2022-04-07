package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.verification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.RegisterErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.VerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password.ResetPasswordVerificationRequest
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentVerificationBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.entry.reset_password.SuccessDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.verification.VerificationViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject


/***
 * bu Kirishdagi Ownerni smsni tasdiqlash qismi
 */


@AndroidEntryPoint
class VerificationFragment : Fragment() {
    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerificationViewModel by viewModels()

    private var verification: VerificationRequest? = null
    private var resetPasswordVerification: ResetPasswordVerificationRequest? = null

    @Inject
    lateinit var successDialogMaker: SuccessDialogMaker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verification = arguments?.getParcelable("verification")
        resetPasswordVerification = arguments?.getParcelable("resetPasswordVerificationRequest")
        binding.etSmsCode.setText(verification?.code)
        viewModel.startTimer(120_000, 1_000)
        loadObserver()
        loadAction()
    }

    private fun loadAction() {
        binding.btnSubmit.setOnClickListener {
            val code = binding.etSmsCode.text.toString()
            if (code.length != 5) {
                makeErrorSnack("Sms code xato formatda")
                return@setOnClickListener
            }
            if (verification != null) {
                Log.d("RRRT", " Action Register verification")
                verification?.code = code
                viewModel.verification(verification!!)
            } else {
                Log.d("RRRT", "Action NewPassword verification")

                resetPasswordVerification?.code = code
                viewModel.verificationNewPassword(resetPasswordVerification!!)
            }
        }
    }

    private fun loadObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.verificationResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnSubmit.isEnabled = true
                        handleCustomException(dataWrapper.error) { response ->
                            when {
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INCORRECT_CODE.message) == true -> {
                                    makeErrorSnack("Noto'g'ri kod")
                                }
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INCORRECT_PHONE_NUMBER.message) == true -> {
                                    makeErrorSnack("Telefon raqam noto'g'ri formatda")
                                }
                                else -> {
                                    handleException(dataWrapper.error)
                                }
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                        binding.btnSubmit.isEnabled = false
                    }
                    is DataWrapper.Success -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnSubmit.isEnabled = true

                        if (verification != null) {
                            findNavController().navigate(
                                R.id.action_verificationFragment_to_chooseCompanyFragment
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.verificationNewPasswordResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
//                        binding.btnSubmit.isEnabled = true
                        handleCustomException(dataWrapper.error) { response ->
                            when {
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INCORRECT_CODE.message) == true -> {
                                    makeErrorSnack("Noto'g'ri kod")
                                }
                                response.errors?.map { it.error }
                                    ?.contains(RegisterErrorsEnum.INCORRECT_PHONE_NUMBER.message) == true -> {
                                    makeErrorSnack("Telefon raqam noto'g'ri formatda")
                                }
                                else -> {
                                    handleException(dataWrapper.error)
                                }
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                        binding.btnSubmit.isEnabled = false
                    }
                    is DataWrapper.Success -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnSubmit.isEnabled = true
                        successDialogMaker.showSuccessDialog(requireContext()) {
                            findNavController().navigate(VerificationFragmentDirections.actionVerificationFragmentToLoginFragment())
                        }
                    }
                }
            }
        }
        viewModel.timer.observe(viewLifecycleOwner, getTimerObserver)
    }

    private val getTimerObserver = Observer<String> {
        binding.tvTimer.text = it
        if (it == "00:00") {
            makeErrorSnack("Time Finish")
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
