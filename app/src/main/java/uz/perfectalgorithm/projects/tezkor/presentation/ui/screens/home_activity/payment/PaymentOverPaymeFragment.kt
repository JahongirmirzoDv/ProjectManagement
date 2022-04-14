package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.OrderData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPaymentOverPaymeBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.payment.ConfirmPaymentDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment.CourseViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import java.text.SimpleDateFormat
import java.util.*



@AndroidEntryPoint
class PaymentOverPaymeFragment : Fragment() {

    private var _binding: FragmentPaymentOverPaymeBinding? = null
    private val binding: FragmentPaymentOverPaymeBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val courserViewModel: CourseViewModel by viewModels()

    private var course: Double? = null
    private var dollarValue: Double? = 0.toDouble()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentOverPaymeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = df.format(date)
        courserViewModel.getCourse(formattedDate)

        loadViews()
        loadObserver()
    }

    private fun loadViews() {


        binding.apply {

            btnPaymentConfirm.setOnClickListener {
                Log.d("TTT", "Dollar value : $dollarValue")
                if (dollarValue == 0.toDouble()) {
                    makeErrorSnack(getString(R.string.input_dollar_value))
                } else {
                    val dialog = ConfirmPaymentDialog(requireActivity(), dollarValue?.toInt()!!)
                    dialog.saveClickListener {
                        courserViewModel.postOrder(it * course?.toInt()!! * 100)
                    }
                    dialog.show()
                }


            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            ussdValue.addTextChangedListener {

                if (it!!.isEmpty()) {
                    dollarValue = 0.toDouble()
                    uzbValue.setText("0")
                } else {
                    dollarValue = it.toString().toDouble()
                    uzbValue.setText((dollarValue!! * course!!).toBigDecimal().toPlainString())
                }
            }

        }
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObserver() {
        courserViewModel.courseLiveData.observe(this, courseObserver)
        courserViewModel.orderLiveData.observe(this, orderObserver)
        courserViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        courserViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)

    }

    private val orderObserver = Observer<OrderData> {
        findNavController().navigate(
            PaymentOverPaymeFragmentDirections.actionPaymentOverPaymeFragmentToMoveToPaymeFragment(
                it.id!!
            )
        )
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.courseIdeaProgressBar
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

    private val courseObserver = Observer<String> {
        course = it.toDouble()
        binding.courseValue.text = getString(R.string.ussd_course_value, it)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}