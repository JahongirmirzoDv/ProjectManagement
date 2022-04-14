package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPaymentContainerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment.PaymentStateDashboardAdapter
import uz.perfectalgorithm.projects.tezkor.utils.extensions.setNavigationIcon
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar




@AndroidEntryPoint
class PaymentContainerFragment : Fragment() {

    private var _binding: FragmentPaymentContainerBinding? = null
    private val binding: FragmentPaymentContainerBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentContainerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationIcon()
        showAppBar()

        loadViews()
        loadObservers()
    }

    private fun loadObservers() {

    }

    private fun loadViews() {
        binding.viewPager.adapter = PaymentStateDashboardAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = requireContext().getString(R.string.balance)
                1 -> tab.text = requireContext().getString(R.string.stuffs)
                2 -> tab.text = requireContext().getString(R.string.duration)
            }
            binding.viewPager.setCurrentItem(tab.position, false)
        }.attach()
    }


}