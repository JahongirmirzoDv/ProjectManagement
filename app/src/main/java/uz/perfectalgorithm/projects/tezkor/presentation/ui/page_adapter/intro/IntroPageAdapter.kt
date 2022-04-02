package uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.intro.viewpager_pages.IntroPage

class IntroPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        val fm = IntroPage()
        val bundle = Bundle()
        bundle.putInt("position", position)
        fm.arguments = bundle
        return fm
    }
}

