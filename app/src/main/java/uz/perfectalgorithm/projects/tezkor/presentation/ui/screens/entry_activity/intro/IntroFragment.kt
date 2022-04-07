package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.intro

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentIntroBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.intro.IntroPageAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.setting.LanguageFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.intro.IntroViewModel
import uz.perfectalgorithm.projects.tezkor.utils.SharedPref
import uz.perfectalgorithm.projects.tezkor.utils.pageChangeListener
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IntroViewModel by viewModels()
    private val adapter by lazy { IntroPageAdapter(childFragmentManager) }
    private val sharedPref by lazy { SharedPref(requireContext()) }

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }


    private fun loadViews() {
        binding.apply {
            introPager.adapter = adapter
            btnNext.setOnClickListener { viewModel.nextStep(introPager.currentItem + 1) }
            btnSkip.setOnClickListener {
                storage.completeIntro = true
                findNavController().navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())

            }
            introPager.pageChangeListener { viewModel.nextStep(it) }
            dotsIndicator.setViewPager(introPager)
        }
    }

    private fun loadObservers() {
        viewModel.nextStepLiveData.observe(viewLifecycleOwner, nextStepObserver)
        viewModel.openNextScreenLiveData.observe(viewLifecycleOwner, openNextScreenObserver)
    }

    private val nextStepObserver = Observer<Int> { binding.introPager.currentItem = it }

    private val openNextScreenObserver = Observer<Unit> {
        findNavController().navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())
    }

    companion object {
        fun setLocale(activity: Activity, languageCode: String?) {
            val locale = Locale(languageCode ?: "en")
            Locale.setDefault(locale)
            val resources: Resources = activity.resources
            val config: Configuration = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}