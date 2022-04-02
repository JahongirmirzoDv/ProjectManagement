package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.PackageErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.PackagePurchaseItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.AddDateData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.AddStaffData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TariffData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TariffListItem
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPaymentStaffConfigurationBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment.TariffPlansAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.payment.ConfirmPackageDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.PackagePurchaseViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.setNavigationIcon
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.gone
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Kurganbayev Jasurbek
 * PaymentStaffConfigurationFragment oynasi
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/

@AndroidEntryPoint
class PaymentStaffConfigurationFragment : Fragment() {
    private var _binding: FragmentPaymentStaffConfigurationBinding? = null
    private val binding: FragmentPaymentStaffConfigurationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val packagePurchaseViewModel: PackagePurchaseViewModel by viewModels()

    private val args: PaymentStaffConfigurationFragmentArgs by navArgs()

    private lateinit var tariffPlansAdapter: TariffPlansAdapter


    private var stuffCount = 5

    private var duration = 24
    private var costAmount = 0

    private var tariffItemList: List<TariffListItem?>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentStaffConfigurationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationIcon()
        showAppBar()
        packagePurchaseViewModel.getTariffPlans()

        loadObservers()
        loadViews()
        initData()
    }

    private fun initData() {
        binding.apply {
            stuffsValue.text = "$stuffCount ta"
            durationValue.text = "$duration oy"

            costAmount = stuffCount * duration * 10
            binding.costValue.text =
                getString(R.string.cost_value, costAmount.toString())
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {

        packagePurchaseViewModel.getTariffPlanList.observe(
            this,
            tariffPlanListObserver
        )
        packagePurchaseViewModel.packageLiveData.observe(viewLifecycleOwner, packageObserver)
        packagePurchaseViewModel.staffLiveData.observe(viewLifecycleOwner, staffObserver)
        packagePurchaseViewModel.monthLiveData.observe(viewLifecycleOwner, monthObserver)
        packagePurchaseViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        packagePurchaseViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
    }

    @SuppressLint("SetTextI18n")
    private val tariffPlanListObserver = Observer<TariffData> {
        if (it != null) {
            tariffPlansAdapter.submitList(it.dataList)
            tariffItemList = it.dataList
            binding.tvCompanyBalance.text = "$${
                BigDecimal(it.companyBalance!!).setScale(2, RoundingMode.HALF_EVEN)
            }"
            tariffPlansAdapter.checkedToFalse(0)
            tariffPlansAdapter.notifyDataSetChanged()
        }
    }

    private val staffObserver = Observer<AddStaffData> {
        makeSuccessSnack("Xodim soni muvaffaqqiyatli sotib olindi")
        findNavController().navigateUp()
    }

    private val monthObserver = Observer<AddDateData> {
        makeSuccessSnack("Muddat muvaffaqqiyatli sotib olindi")
        findNavController().navigateUp()
    }

    private val packageObserver = Observer<PackagePurchaseItem> {
        makeSuccessSnack("Paket aktivlashtirildi")
        findNavController().navigateUp()
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.progressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private val errorObserver = Observer<Throwable> { throwable ->
        if (throwable is Exception) {
            handleCustomException(throwable) { error ->
                when {
                    error.errors?.map { it.error }
                        ?.contains(PackageErrorsEnum.PRICE_LIST_NOT_FOUND.message) == true ->
                        makeErrorSnack("Paketlar topilmadi. \nAdministrator bilan bog'laning")
                    error.errors?.map { it.error }
                        ?.contains(PackageErrorsEnum.LOW_BALANCE.message) == true ->
                        makeErrorSnack("Kompaniya balansi yetarli emas. \nHisobingizni to'ldiring")
                    error.errors?.map { it.error }
                        ?.contains(PackageErrorsEnum.STAFF_INCORRECT_COUNT.message) == true ->
                        makeErrorSnack("Xodimlar soni 1 dan katta bo'lishi lozim")
                    error.errors?.map { it.error }
                        ?.contains(PackageErrorsEnum.COMPANY_PACKAGE_NOT_FOUND.message) == true ->
                        makeErrorSnack("Kompaniyangiz paketi topilmadi. Paket sotib oling")
                    else -> handleException(throwable)
                }
            }
        } else {
            makeErrorSnack(throwable.message.toString())
        }
    }

    private fun loadViews() {

        binding.apply {
            tariffPlansAdapter = TariffPlansAdapter()
            tariffPlansList.adapter = tariffPlansAdapter

            btnFillingBalance.setOnClickListener {
                findNavController().navigate(PaymentStaffConfigurationFragmentDirections.actionPaymentStaffConfigurationFragmentToPaymentSystemFragment())
            }

            btnConfirm.setOnClickListener {
                val dialog =
                    ConfirmPackageDialog(
                        requireActivity(),
                        stuffCount.toString(),
                        duration.toString(),
                        costAmount.toString(),
                        args.screenName!!
                    )
                dialog.saveClickListener {

                    when (it) {
                        "lifetime" -> {
                            packagePurchaseViewModel.buyMonth(duration)
                        }
                        "staff" -> {
                            packagePurchaseViewModel.buyStaff(stuffCount)
                        }
                        else -> {
                            packagePurchaseViewModel.buyPackage(stuffCount, duration)

                        }
                    }
                }
                dialog.show()
            }

            when (args.screenName) {
                "lifetime" -> {
                    tvStuffsTitle.gone()
                    tvCompanyStaff.gone()
                    stuffsSeekbar.gone()
                    stuffs.gone()
                    stuffsValue.gone()
                }
                "staff" -> {
                    tvDurationTitle.gone()
                    tvCompanyDuration.gone()
                    durationSeekbar.gone()
                    durations.gone()
                    durationValue.gone()
                }
            }


            stuffsSeekbar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    binding.stuffsValue.text = seekParams.tickText

                    when (seekParams.thumbPosition) {
                        0 -> {
                            stuffCount = 5
                        }
                        1 -> {
                            stuffCount = 15
                        }

                        2 -> {
                            stuffCount = 25
                        }

                        3 -> {
                            stuffCount = 40
                        }
                        4 -> {
                            stuffCount = 60
                        }

                        5 -> {
                            stuffCount = 90
                        }

                        6 -> {
                            stuffCount = 100
                        }
                    }
                    when {
                        stuffCount < 20 -> {
                            costAmount = stuffCount * duration * 10
                            tariffPlansAdapter.submitList(tariffItemList)
                            tariffPlansAdapter.checkedToFalse(0)

                        }
                        stuffCount < 50 -> {
                            costAmount = stuffCount * duration * 9
                            tariffPlansAdapter.submitList(tariffItemList)
                            tariffPlansAdapter.checkedToFalse(1)

                        }
                        else -> {
                            costAmount = stuffCount * duration * 7
                            tariffPlansAdapter.submitList(tariffItemList)
                            tariffPlansAdapter.checkedToFalse(2)
                        }
                    }
                    tariffPlansAdapter.notifyDataSetChanged()
                    binding.costValue.text =
                        getString(R.string.cost_value, costAmount.toString())
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                }


                override fun onDragging(progress: Float) {

                }
            }

            durationSeekbar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
//                    binding.durationValue.text = seekParams.tickText

                    when (seekParams.thumbPosition) {
                        0 -> {
                            duration = 24
                            binding.durationValue.text = "24 oy"
                        }
                        1 -> {
                            duration = 12
                            binding.durationValue.text = "12 oy"
                        }
                        2 -> {
                            duration = 6
                            binding.durationValue.text = "6 oy"
                        }
                        3 -> {
                            duration = 3
                            binding.durationValue.text = "3 oy"
                        }

                    }

                    /// have been done changing
                    costAmount = when {
                        stuffCount < 20 -> {
                            stuffCount * duration * 10

                        }
                        stuffCount < 50 -> {
                            stuffCount * duration * 9
                        }
                        else -> {
                            stuffCount * duration * 7

                        }
                    }
                    binding.costValue.text =
                        getString(R.string.cost_value, costAmount.toString())


                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                }

                override fun onDragging(progress: Float) {
                }
            }

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