package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.functional.TasksFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.meeting.MeetingsFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.project.ProjectsFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.dating.DatingsFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.tactic_plan.TacticPlansFragment


class ViewStateMeetingDashboardAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList =
        arrayListOf(
            MeetingsFragment(),
            DatingsFragment(),
        )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}