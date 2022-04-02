package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentQuickIdeasCategoriesBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.ViewStateIdeasPagerAdapter
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Kurganbayev Jasurbek
 * QuickIdeasCategoriesFragment idealar 3ta kategoriyaga bo'lingan
 * bajarilgan, jarayonda(in progress), tugatilgan oynasi
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/

class QuickIdeasCategoriesFragment : Fragment() {
    private var _binding: FragmentQuickIdeasCategoriesBinding? = null
    private val binding: FragmentQuickIdeasCategoriesBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuickIdeasCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showAppBar()

        loadViews()
    }

    private fun loadViews() {
        binding.viewPager.adapter = ViewStateIdeasPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = requireContext().getString(R.string.ideas)
                1 -> tab.text = requireContext().getString(R.string.rating)
                2 -> tab.text = requireContext().getString(R.string.carried_out)
            }
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()

        binding.apply {
            fabAddIdea.setOnClickListener {
                if (addIdeaBtn.visibility == View.VISIBLE) {
                    addIdeaBtn.gone()
                    fabAddIdea.rotation = 0F
                } else {
                    addIdeaBtn.visible()
                    fabAddIdea.rotation = 45F
                }
            }

            addIdeaBtn.setOnClickListener {
                findNavController().navigate(
                    QuickIdeasCategoriesFragmentDirections.actionQuickIdeasCategoriesFragmentToCreateQuickIdeaFragment(
                        screen = "category_screen"
                    )
                )
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}