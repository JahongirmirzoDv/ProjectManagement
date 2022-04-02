package uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.workers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.outsource.OutsourceFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.structure.StructureFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.team.TeamFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.worker_list.WorkerListFragment

/**
 * Created by Raximjanov Davronbek on 11.09.2021 14:55
 **/

class ViewStateWorkersPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
       return when(position) {
            0 -> TeamFragment()
            1 -> WorkerListFragment()
            2 -> StructureFragment()
            3 -> OutsourceFragment()
            else -> TeamFragment()
        }
    }


}

interface UpdateCallback {
    fun update(string: String)
}