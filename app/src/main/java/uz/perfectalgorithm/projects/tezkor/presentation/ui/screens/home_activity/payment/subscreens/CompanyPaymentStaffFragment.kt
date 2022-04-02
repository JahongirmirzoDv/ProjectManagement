package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.subscreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.StaffItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.UserActivateResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompanyPaymentStaffBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment.StaffListAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.PaymentContainerFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.StaffCountViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.showAlertDialogStuff


/**
 * Kurganbayev Jasurbek
 * CompanyPaymentStatusFragment ViewPagerdagi page qismlardan biri
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/
@AndroidEntryPoint
class CompanyPaymentStaffFragment : Fragment() {
    private var _binding: FragmentCompanyPaymentStaffBinding? = null
    private val binding: FragmentCompanyPaymentStaffBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val staffCountViewModel: StaffCountViewModel by viewModels()

    private lateinit var staffListAdapter: StaffListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyPaymentStaffBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {

        binding.apply {

            staffListAdapter = StaffListAdapter()
            staffList.adapter = staffListAdapter

            btnStaffLimit.setOnClickListener {

                findNavController().navigate(
                    PaymentContainerFragmentDirections.actionPaymentContainerFragmentToPaymentStaffConfigurationFragment(
                        "staff"
                    )
                )
            }

            staffListAdapter.setItemToggleListener { staffItem, b, triStateToggleButton ->
                showAlertDialogStuff(
                    "Ushbu xodimni aktivligini o'zgartishini xohlaysizmi",
                    {
                        var activeStatus = 0
                        if (b) {
                            activeStatus = 1
                            triStateToggleButton.setToggleOn(true)
                        } else {
                            activeStatus = 0
                            triStateToggleButton.setToggleOff(true)
                        }
                        staffCountViewModel.userActivate(staffItem.id!!, activeStatus)
                    },
                    {
                        if (b) {
                            triStateToggleButton.setToggleOff(true)
                        } else {
                            triStateToggleButton.setToggleOn(true)
                        }
                    }
                )
            }
        }
    }


    private fun loadObservers() {

        staffCountViewModel.userActivateLiveData.observe(
            viewLifecycleOwner,
            userActivateObserver
        )
        staffCountViewModel.packageLiveData.observe(viewLifecycleOwner, packageObserver)
        staffCountViewModel.staffListLiveData.observe(viewLifecycleOwner, staffListObserver)
        staffCountViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        staffCountViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
    }

    private val userActivateObserver = Observer<UserActivateResponse> {
        if (it.success!!) {
            makeSuccessSnack("Xodim holati o'zgartirildi")
        }
    }

    private val packageObserver = Observer<PackageItem> {
        if (it != null) {
            binding.tvCompanyStaffCountLimit.text = it.staffLimit.toString()
        }
    }

    private val staffListObserver = Observer<List<StaffItem>> {
        if (it != null) {
            binding.apply {
                tvCompanyStaffCount.text = it.size.toString()
                staffListAdapter.submitList(it)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}