package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.subscreens.CompanyBalanceFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.subscreens.CompanyPaymentStaffFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.subscreens.LifetimeFragment

/**
 * Created by Jasurbek Kurganbaev on 09.08.2021 14:29
 **/
class PaymentStateDashboardAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList =
        arrayListOf(
            CompanyBalanceFragment(),
            CompanyPaymentStaffFragment(),
            LifetimeFragment()
        )

    override fun createFragment(position: Int): Fragment = fragmentList[position]


    override fun getItemCount(): Int {
        return fragmentList.size
    }

}