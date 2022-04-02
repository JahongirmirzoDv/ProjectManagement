package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *Created by farrukh_kh on 10/1/21 10:47 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person
 **/
class DashboardPagerStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    val fragmentList: List<Fragment> =
        listOf(
            FavoritesBelowSelectFragment(),
            StaffBelowSelectFragment(),
            StructureBelowSelectFragment(),
            OutsourceBelowSelectFragment()
        )

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size
}