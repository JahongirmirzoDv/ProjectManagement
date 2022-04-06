package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentSplashBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.splash.SplashViewModel
import uz.perfectalgorithm.projects.tezkor.utils.changeNavigationBarColor
import uz.perfectalgorithm.projects.tezkor.utils.changeStatusBarColor
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.toHandledError
import uz.perfectalgorithm.projects.tezkor.utils.toDarkenColor
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadViews()
        loadObservers()
    }

    private fun loadViews() {

        requireActivity().changeStatusBarColor(
            resources.getColor(R.color.splash_bg).toDarkenColor()
        )
        requireActivity().changeNavigationBarColor(
            resources.getColor(R.color.splash_bg).toDarkenColor()
        )
        Handler().postDelayed(
            Runnable {
                checkUser()
            }, 2000
        )
    }

    private fun checkUser() {
        if (!storage.completeIntro) {
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToIntroFragment())
            }
        } else if (!storage.logged) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        } else if (!storage.isChosenCompany && storage.role == RoleEnum.OWNER.text) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToChooseCompanyFragment())
        } else {
            viewModel.refreshToken()
        }
    }

    private fun loadObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tokenResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        makeErrorSnack(dataWrapper.error.toHandledError().title)
                        goToMainActivity()
                    }
                    is DataWrapper.Loading -> Unit
                    is DataWrapper.Success -> goToMainActivity()
                }
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().changeStatusBarColor(
            resources.getColor(R.color.white)
        )
        requireActivity().changeNavigationBarColor(resources.getColor(R.color.black))
    }
}

