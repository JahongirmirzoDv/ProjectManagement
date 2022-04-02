package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentNavigationChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.chat.ViewStateChatPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.NavigationChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu


@AndroidEntryPoint
class NavigationChatFragment : Fragment() {
    private var _binding: FragmentNavigationChatBinding? = null
    private val binding: FragmentNavigationChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: NavigationChatViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewStateChatPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showBottomMenu()
        showAppBar()

        loadViews()
        loadObservers()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadViews() {
        initTabLayout()
    }

    private fun initTabLayout() {

        viewPagerAdapter = ViewStateChatPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.icon = requireActivity().getDrawable(R.drawable.ic_compass)
                1 -> tab.icon = requireActivity().getDrawable(R.drawable.ic_user)
                2 -> tab.icon = requireActivity().getDrawable(R.drawable.ic_users)
                3 -> tab.icon = requireActivity().getDrawable(R.drawable.ic_channels)
                4 -> tab.icon = requireActivity().getDrawable(R.drawable.ic_briefcase)
            }
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }


    private fun loadObservers() {

        viewModel.openQuickIdeasScreen.observe(viewLifecycleOwner, openQuickIdeasObserver)
        viewModel.openQuickPlansScreen.observe(viewLifecycleOwner, openQuickPlansObserver)
    }

    private val openQuickIdeasObserver = Observer<Unit> {
        findNavController().navigate(R.id.action_navigation_chat_to_quickIdeasFragment)
    }

    private val openQuickPlansObserver = Observer<Unit> {
        findNavController().navigate(R.id.action_navigation_chat_to_quickPlansFragment)

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