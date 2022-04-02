package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.intro.viewpager_pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.PageIntroBinding

@AndroidEntryPoint
class IntroPage : Fragment() {
    private var _binding: PageIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (arguments?.getInt("position", 0)) {
            0 -> {
                binding.apply {
                    imgMain.setImageResource(R.drawable.ic_img_business_finance_intro)
                    txtDesc.text =
                        getString(R.string.intro_desc_first)
                    definition.text = getString(R.string.intro_definition_1)
                }
            }
            1 -> {
                binding.apply {
                    imgMain.setImageResource(R.drawable.ic_img_business_decisions_intro)
                    txtDesc.text =
                        getString(R.string.intro_desc_second)
                    definition.text = getString(R.string.intro_definition_2)

                }
            }
            else -> {
                binding.apply {
                    imgMain.setImageResource(R.drawable.ic_img_business_chat_intro)
                    txtDesc.text =
                        getString(R.string.intro_desc_third)
                    definition.text = getString(R.string.intro_definition_3)

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
