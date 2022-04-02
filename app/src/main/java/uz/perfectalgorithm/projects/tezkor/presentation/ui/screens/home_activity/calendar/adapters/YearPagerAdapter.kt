package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.year.YearFragment


class YearPagerAdapter(private val activity: FragmentActivity, private val items: List<Int>) :
    FragmentStateAdapter(activity) {


    override fun createFragment(position: Int): Fragment =
        YearFragment.newInstance(items[position])

    override fun getItemCount() = items.size

}