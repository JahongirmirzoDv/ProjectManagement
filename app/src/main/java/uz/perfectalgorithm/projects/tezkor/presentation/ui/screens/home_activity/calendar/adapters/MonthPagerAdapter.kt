package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.month.MonthFragment
import uz.perfectalgorithm.projects.tezkor.utils.calendar.DAY_CODE
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID

class MonthPagerAdapter(
    fm: FragmentManager,
    private val monthList: List<String>,
    private val staffId: Int
) :
    FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    override fun getCount(): Int = monthList.size

    override fun getItem(position: Int): Fragment {
        val fragment = MonthFragment()
        val bundle = Bundle()
        val code = monthList[position]
        bundle.putString(DAY_CODE, code)
        bundle.putInt(STAFF_ID, staffId)
        fragment.arguments = bundle
        return fragment
    }

}