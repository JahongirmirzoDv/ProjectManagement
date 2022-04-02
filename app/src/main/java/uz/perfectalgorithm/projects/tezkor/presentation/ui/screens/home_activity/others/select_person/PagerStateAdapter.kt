package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.select_person

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *Created by farrukh_kh on 8/10/21 8:49 AM
 *uz.rdo.projects.projectmanagement.utils.adding
 **/
class PagerStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    val fragmentList: List<Fragment> =
        listOf(
            FavoritesSelectFragment(),
            AllStaffSelectFragment(),
            StructureSelectFragment(),
            OutsourceSelectFragment()
        )

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size
}