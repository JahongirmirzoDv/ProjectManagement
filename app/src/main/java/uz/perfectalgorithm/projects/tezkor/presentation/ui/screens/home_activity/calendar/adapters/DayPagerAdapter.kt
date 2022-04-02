package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.day.DayFragment
import uz.perfectalgorithm.projects.tezkor.utils.calendar.DAY_CODE
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.calendar.VIEW_STATE

class DayPagerAdapter(
    fm: FragmentManager,
    private val dayList: List<String>,
    private val staffId: Int
) :
    FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private val mFragments = SparseArray<DayFragment>()


    private var viewState = 0
    override fun getCount(): Int = dayList.size

    override fun getItem(position: Int): Fragment {
        val fragment = DayFragment()
        val bundle = Bundle()
        val code = dayList[position]
        bundle.putString(DAY_CODE, code)
        bundle.putInt(VIEW_STATE, viewState)
        bundle.putInt(STAFF_ID, staffId)
        fragment.arguments = bundle
        mFragments.put(position, fragment)
        return fragment
    }


    fun updateScrollY(pos: Int, y: Float) {
        mFragments[pos - 1]?.updateScrollY(y)
        mFragments[pos + 1]?.updateScrollY(y)
    }

    fun updateViewState(pos: Int, state: Int) {
        mFragments[pos - 1]?.updateViewState(state)
        mFragments[pos + 1]?.updateViewState(state)
    }


    fun getViewState(viewState: Int) {
        this.viewState = viewState
    }

}