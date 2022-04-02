package uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.chat

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.channel.ChannelFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.company_group.CompanyGroupChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.compass.CompassChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.group.GroupChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.personal.PersonalChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.project.ProjectChatFragment

/**
 * Created by Raximjanov Davronbek on 11.09.2021 14:55
 **/

class ViewStateChatPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentList =
        arrayListOf(
            CompanyGroupChatFragment(),
            PersonalChatFragment(),
            GroupChatFragment(),
            CompanyGroupChatFragment.getInstance(true),
            ProjectChatFragment()
        )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}
