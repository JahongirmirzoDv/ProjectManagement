package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment.subscreens

import android.annotation.SuppressLint
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TransactionData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompanyBalanceBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.payment.TransactionHistoryOuterAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.TransactionViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import java.math.BigDecimal
import java.math.RoundingMode


@AndroidEntryPoint
class CompanyBalanceFragment : Fragment() {

    private var _binding: FragmentCompanyBalanceBinding? = null
    private val binding: FragmentCompanyBalanceBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val transactionViewModel: TransactionViewModel by viewModels()

    private lateinit var transactionHistoryOuterAdapter: TransactionHistoryOuterAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyBalanceBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadViews()
        loadObservers()
    }

    private fun loadObservers() {

        transactionViewModel.transactionListLiveData.observe(viewLifecycleOwner, staffListObserver)
        transactionViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        transactionViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)

    }

    @SuppressLint("SetTextI18n")
    private val staffListObserver = Observer<TransactionData> {
        if (it.dataList!!.isEmpty()) {
            binding.tvError.show()
        } else {
            binding.apply {
                binding.tvError.gone()
                transactionHistoryOuterAdapter.submitList(it.dataList)
                tvCompanyBalance.text = "$${
                    BigDecimal(it.companyBalance!!).setScale(2, RoundingMode.HALF_EVEN)
                }"
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
            transactionHistoryOuterAdapter = TransactionHistoryOuterAdapter()
            paymentHistory.adapter = transactionHistoryOuterAdapter

            btnFillingBalance.setOnClickListener {
                findNavController().navigate(R.id.action_paymentContainerFragment_to_paymentSystemFragment)
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