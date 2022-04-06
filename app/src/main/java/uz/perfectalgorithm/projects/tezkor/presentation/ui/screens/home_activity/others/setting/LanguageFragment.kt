package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.setting

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentLanguageBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : Fragment() {


    @Inject
    lateinit var storage: LocalStorage

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).hideAppBar()
        (activity as HomeActivity).hideBottomMenu()


        binding.apply {
            backImage.setOnClickListener { findNavController().popBackStack() }
            selectLan(storage.lan?:"uz")
            enLanCard.setOnClickListener {
                selectLan("en")
                setLocale(requireActivity(), "en")
                storage.lan = "en"
                title.text = "Language"
                requireActivity().recreate()
            }

            ruLanCard.setOnClickListener {
                selectLan("ru")
                setLocale(requireActivity(), "ru")
                storage.lan = "ru"
                title.text = "Russian"
                requireActivity().recreate()
            }

            uzLanCard.setOnClickListener {
                selectLan("uz")
                setLocale(requireActivity(), "uz")
                storage.lan = "uz"
                title.text = "Uzbek"
                requireActivity().recreate()
            }
        }
    }

    private fun selectLan(str: String) {
        binding.apply {
            when (str) {
                "ru" -> {
                    txtRu.setTextColor(Color.WHITE)
                    txtEn.setTextColor(Color.BLACK)
                    txtUz.setTextColor(Color.BLACK)
                    imageLanRu.visibility = View.VISIBLE
                    imageLanEn.visibility = View.GONE
                    imageLanUz.visibility = View.GONE
                    ruLanCard.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    enLanCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                    uzLanCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                }
                "en" -> {
                    txtEn.setTextColor(Color.WHITE)
                    txtRu.setTextColor(Color.BLACK)
                    txtUz.setTextColor(Color.BLACK)
                    imageLanEn.visibility = View.VISIBLE
                    imageLanRu.visibility = View.GONE
                    imageLanUz.visibility = View.GONE
                    enLanCard.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    ruLanCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                    uzLanCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                }
                else -> {
                    txtUz.setTextColor(Color.WHITE)
                    txtRu.setTextColor(Color.BLACK)
                    txtEn.setTextColor(Color.BLACK)
                    imageLanUz.visibility = View.VISIBLE
                    imageLanEn.visibility = View.GONE
                    imageLanRu.visibility = View.GONE
                    uzLanCard.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    enLanCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                    ruLanCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as HomeActivity).showAppBar()
        (activity as HomeActivity).showBottomMenu()
        _binding = null
    }

}