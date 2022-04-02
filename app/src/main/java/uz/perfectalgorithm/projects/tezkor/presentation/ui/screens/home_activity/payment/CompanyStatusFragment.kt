package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.PackageErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItemData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompanyStatusBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment.PackageAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.CompanyStatusViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.setNavigationIcon
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone

/**
 * Kurganbayev Jasurbek
 * CompanyStatusFragment oynasi
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/
@AndroidEntryPoint
class CompanyStatusFragment : Fragment() {

    private var _binding: FragmentCompanyStatusBinding? = null
    private val binding: FragmentCompanyStatusBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val companyStatusViewModel: CompanyStatusViewModel by viewModels()

    private lateinit var packageAdapter: PackageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyStatusBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationIcon()
        hideBottomMenu()

        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        companyStatusViewModel.packageLiveData.observe(viewLifecycleOwner, packageObserver)
        companyStatusViewModel.purchasedPackageList.observe(
            viewLifecycleOwner,
            purchasedPackageListObserver
        )
        companyStatusViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        companyStatusViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.progressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private val purchasedPackageListObserver = Observer<List<PackageItemData>> {
        Log.d("JIRA", "$it")


        if (it != null || it?.size != 0) {
            packageAdapter.submitList(it)
        } else {
            binding.purchasedPackageList.gone()
            binding.inactiveText.gone()

        }
    }

    private val errorObserver = Observer<Throwable> { throwable ->
        if (throwable is Exception) {
            handleCustomException(throwable) { error ->
                when {
                    error.errors?.map { it.error }
                        ?.contains(PackageErrorsEnum.PACKAGE_NOT_FOUND.message) == true ->
                        makeErrorSnack("Kompaniyangiz paketi topilmadi")
                    else -> handleException(throwable)
                }
            }
        } else {
            makeErrorSnack(throwable.message.toString())
        }
    }

    private val packageObserver = Observer<PackageItem> {


        if (it.isDemo!!) {
            val demoText = "Demo Versiya"
            val activeStatus = "Active"
            binding.apply {
                title.text = demoText
                activeText.text = activeStatus
                activeText.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.active_status,
                    0,
                    0,
                    0
                )
                demoVersionDeadline.text = getString(R.string.package_deadline, it.expireDate)
                demoVersionStaffLimit.text =
                    getString(R.string.limit_staffs, it.staffLimit.toString())
            }

        } else {
            val demoText = "Joriy paket"
            val activeStatus = "Active"

            binding.apply {
                title.text = demoText
                activeText.text = activeStatus
                activeText.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.active_status,
                    0,
                    0,
                    0
                )

                demoVersionDeadline.text = getString(R.string.package_deadline, it.expireDate)
                demoVersionStaffLimit.text =
                    getString(R.string.limit_staffs, it.staffLimit.toString())
            }


        }
    }


    private fun loadViews() {
        binding.apply {

            packageAdapter = PackageAdapter()
            purchasedPackageList.adapter = packageAdapter

            btnBuyingNewPackage.setOnClickListener {
                findNavController().navigate(
                    CompanyStatusFragmentDirections.actionCompanyStatusFragmentToPaymentStaffConfigurationFragment(
                        "new"
                    )
                )
            }

            btnMoveToBalance.setOnClickListener {
                findNavController().navigate(CompanyStatusFragmentDirections.actionCompanyStatusFragmentToPaymentContainerFragment())
            }

        }
    }

    override fun onResume() {
        super.onResume()
        companyStatusViewModel.getPackage()
        companyStatusViewModel.getPurchasedPackageList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}