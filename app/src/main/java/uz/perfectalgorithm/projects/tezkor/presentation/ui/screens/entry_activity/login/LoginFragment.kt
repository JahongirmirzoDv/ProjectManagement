package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.LoginErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentLoginBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.setting.LanguageFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.login.LoginViewModel
import uz.perfectalgorithm.projects.tezkor.utils.SharedPref
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.HandledError
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.util.*
import javax.inject.Inject


/***
 * bu Kirishdagi Login oynasi
 */

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val sharedPref by lazy { SharedPref(requireContext()) }

    @Inject
    lateinit var storage: LocalStorage
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        var locale = Locale(storage.lan ?: "uz")

//        Locale.setDefault(locale)
//        val resources: Resources = resources
//        val config: Configuration = resources.configuration
//        config.setLocale(locale)
//        resources.updateConfiguration(config, resources.displayMetrics)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        hideKeyboard(binding.loginCountryCodePickerView)


        loadViews()
        loadObserver()
    }

    private fun loadViews() {
        binding.apply {

            loginCountryCodePickerView.setNumberAutoFormattingEnabled(true)
            loginCountryCodePickerView.registerCarrierNumberEditText(loginEtPhone)

            btnLogin.setOnClickListener {
                login()
                hideKeyboard(btnLogin)
            }
            txtRegistration.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
            }

            tvResetPassword.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
                hideKeyboard(binding.loginCountryCodePickerView)

            }
        }
    }

    private fun login() {
        binding.apply {
            val phoneNumber = loginEtPhone.text.toString().replace(" ", "")
            val password = etPassword.text.toString()

            if (phoneNumber.length != 9) {
                binding.loginEtPhone.error = getString(R.string.invalid_phone_number)
                return
            }

            if (password.isNullOrBlank()) {
                binding.etPassword.error = getString(R.string.enter_password)
                return
            }

            viewModel.login(
                phone = loginCountryCodePickerView.fullNumber,
                password = password
            )
        }
    }

    private fun loadObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        handleCustomException(dataWrapper.error) { error ->
                            if (error.detail == LoginErrorsEnum.WRONG_CREDENTIALS.message) {
                                findNavController().navigate(
                                    R.id.handledErrorDialogFragment,
                                    bundleOf(
                                        "error" to HandledError.WrongCredentialsError()
                                    )
                                )
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                    }
                    is DataWrapper.Success -> {
                        binding.progressLayout.progressLoader.gone()

                        if (dataWrapper.data.role == RoleEnum.OWNER.text) {
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToChooseCompanyFragment())
                        } else {
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }


}
