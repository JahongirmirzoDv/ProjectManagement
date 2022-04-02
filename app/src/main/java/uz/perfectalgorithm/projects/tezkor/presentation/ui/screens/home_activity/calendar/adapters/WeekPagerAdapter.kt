package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.week.WeekFragment
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.calendar.WEEK_START_DATE_TIME

class WeekPagerAdapter(
    fm: FragmentManager,
    private val weekList: List<Long>,
    private val staffId: Int
) :
    FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    private val mFragments = SparseArray<WeekFragment>()


    override fun getCount(): Int = weekList.size

    override fun getItem(position: Int): Fragment {
        val fragment = WeekFragment()
        val bundle = Bundle()
        val code = weekList[position]
        bundle.putLong(WEEK_START_DATE_TIME, code)
        bundle.putInt(STAFF_ID, staffId)
        fragment.arguments = bundle
        mFragments.put(position, fragment)
        return fragment
    }

}
