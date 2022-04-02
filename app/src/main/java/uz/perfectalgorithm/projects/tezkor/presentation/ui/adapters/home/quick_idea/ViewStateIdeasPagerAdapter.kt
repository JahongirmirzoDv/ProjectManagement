package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.idea_categories.CompletedIdeasFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.idea_categories.RatingIdeasFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.idea_categories.UndoneIdeasFragment

/**
 * Created by Jasurbek Kurganbaev on 27.07.2021 19:01
 **/
class ViewStateIdeasPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    private val fragmentList =
        arrayListOf(
            UndoneIdeasFragment(),
            RatingIdeasFragment(),
            CompletedIdeasFragment()
        )


    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]


}