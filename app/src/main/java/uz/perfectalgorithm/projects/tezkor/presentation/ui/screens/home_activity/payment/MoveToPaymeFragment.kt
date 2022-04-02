package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.UrlData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentMoveToPaymeBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.MoveToPaymeViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone

/**
 * Kurganbayev Jasurbek
 * MoveToPaymentFragment oynasi
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/
@AndroidEntryPoint
class MoveToPaymeFragment : Fragment() {

    private var _binding: FragmentMoveToPaymeBinding? = null
    private val binding: FragmentMoveToPaymeBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val moveToPaymeViewModel: MoveToPaymeViewModel by viewModels()

    private val args: MoveToPaymeFragmentArgs by navArgs()

    private var paymentUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoveToPaymeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadViews()
        loadObservers()

        moveToPaymeViewModel.getPaymentUrl(args.id)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        moveToPaymeViewModel.moveToPaymeUrlLiveData.observe(this, urlObserver)
        moveToPaymeViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        moveToPaymeViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
    }

    private val urlObserver = Observer<UrlData> {
        binding.apply {
            title.show()
            btnMovePaymeApk.show()
        }
        paymentUrl = it.url
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.paymeProgressBar
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

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnMovePaymeApk.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
                startActivity(browserIntent)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}