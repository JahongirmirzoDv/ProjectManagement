package uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.chat

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.outsource.OutsourceForChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.structure.StructureForChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.team.TeamForChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.worker_list.WorkerListForChatFragment

class WorkersForChatPagerAdapter(fm: FragmentManager, behavior: Int, private val context: Context) :
    FragmentPagerAdapter(fm, behavior) {
    private var myString = ""

    private val fragmentList = arrayListOf(
        TeamForChatFragment(),
        WorkerListForChatFragment(),
        StructureForChatFragment(),
        OutsourceForChatFragment()
//        TeamFragment(), WorkerListFragment(), StructureFragment(), OutsourceFragment()
    )

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    fun update(string: String) {
        myString = string
        notifyDataSetChanged()
    }

    override fun getItemPosition(myValue: Any): Int {
        if (myValue is UpdateCallback) {
            (myValue as UpdateCallback).update(myString)
        }
        return super.getItemPosition(myValue)

    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> {
            context.getString(R.string.worker_tab_0_title)
        }
        1 -> {
            context.getString(R.string.worker_tab_1_title)
        }
        2 -> {
            context.getString(R.string.worker_tab_2_title)
        }
        else -> {
            context.getString(R.string.worker_tab_3_title)
        }
    }
}

interface UpdateCallback {
    fun update(string: String)

}

