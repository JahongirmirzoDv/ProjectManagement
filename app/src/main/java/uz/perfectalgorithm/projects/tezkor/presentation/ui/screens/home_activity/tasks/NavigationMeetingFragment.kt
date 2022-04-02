package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentNavigationMeetingBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.ViewStateMeetingDashboardAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.ViewStateTaskDashboardAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.NavigationTasksViewModel
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu


/**
 * BottomNavBar markazidagi item bosilganda ko'rsatiladigan parent fragment
 * ViewPager orqali vazifa, proyekt, majlis, uchrashuv va taktik rejalar
 * oynalarini ko'rsatadi
 */
@AndroidEntryPoint
class NavigationMeetingFragment : Fragment() {

    private var _binding: FragmentNavigationMeetingBinding? = null
    private val binding: FragmentNavigationMeetingBinding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val sharedViewModel by activityViewModels<NavigationTasksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationMeetingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showBottomMenu()
        showAppBar()

        initTabLayout()
        setupViews()
    }

    private fun initTabLayout() {
        binding.vpFragments.apply {
            offscreenPageLimit = 4
            adapter = ViewStateMeetingDashboardAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(binding.tabLayout, this) { tab, position ->
                tab.text = resources.getStringArray(R.array.tab_in_meeting)[position]
                setCurrentItem(tab.position, true)
            }.attach()
        }
    }

    private fun setupViews() = with(binding) {
        if (sharedViewModel.isFolderAdapter.value) {
            ivChangeTaskMode.setImageResource(R.drawable.ic_kanban_on)
        } else {
            ivChangeTaskMode.setImageResource(R.drawable.ic_kanban_off)
        }

        ivChangeTaskMode.setOnClickListener {
            if (sharedViewModel.isFolderAdapter.value) {
                ivChangeTaskMode.setImageResource(R.drawable.ic_kanban_off)
                sharedViewModel.setIsFolderAdapter(false)
            } else {
                ivChangeTaskMode.setImageResource(R.drawable.ic_kanban_on)
                sharedViewModel.setIsFolderAdapter(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.setIsFolderAdapter(true)
        _binding = null
    }
}