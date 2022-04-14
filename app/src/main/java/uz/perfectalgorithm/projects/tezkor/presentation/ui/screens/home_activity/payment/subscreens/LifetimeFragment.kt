package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.subscreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItem
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentLifetimeBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.PaymentContainerFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.LifetimeViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone



@AndroidEntryPoint
class LifetimeFragment : Fragment() {

    private var _binding: FragmentLifetimeBinding? = null
    private val binding: FragmentLifetimeBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    private val lifetimeViewModel: LifetimeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLifetimeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        lifetimeViewModel.packageLiveData.observe(viewLifecycleOwner, packageObserver)

        lifetimeViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        lifetimeViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)

    }

    private val packageObserver = Observer<PackageItem> {
        if (it != null) {
            binding.apply {

                tvCompanyStaff.text = it.staffLimit.toString()
                tvDurationDay.text = getString(R.string.lost_day, it.lostDays.toString())
                tvInCalendarTime.text = getString(R.string.in_calendar_day, it.expireDate)
            }
        }

    }


    private val progressObserver = Observer<Boolean> {
        val progressView = binding.progressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private fun loadViews() {

        binding.apply {

            btnAddLifetime.setOnClickListener {
                findNavController().navigate(
                    PaymentContainerFragmentDirections.actionPaymentContainerFragmentToPaymentStaffConfigurationFragment(
                        "lifetime"
                    )
                )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}