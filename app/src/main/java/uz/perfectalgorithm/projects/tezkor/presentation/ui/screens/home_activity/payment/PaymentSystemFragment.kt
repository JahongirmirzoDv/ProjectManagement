package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPaymentSystemBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar



@AndroidEntryPoint
class PaymentSystemFragment : Fragment() {

    private var _binding: FragmentPaymentSystemBinding? = null
    private val binding: FragmentPaymentSystemBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentSystemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()

        loadViews()
        loadObservers()

    }

    private fun loadObservers() {

    }

    private fun loadViews() {
        binding.apply {
            cvPayme.setOnClickListener {
                findNavController().navigate(R.id.action_paymentSystemFragment_to_paymentOverPaymeFragment)
            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showAppBar()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}