package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.payment


/**
 * Craeted by Davronbek Raximjanov on 26.06.2021
 * **/


import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.ConfirmPaymentDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class ConfirmPaymentDialog(
    private val activity: Activity,
    private val ussdValue: Int
) : AlertDialog(activity) {

    private var _binding: ConfirmPaymentDialogBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var saveListener: SingleBlock<Int>? = null

    init {
        _binding = ConfirmPaymentDialogBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {


        binding.apply {

            txtDesc.text =
                activity.getString(R.string.confirm_payment_value_dialog, ussdValue.toString())

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnYes.setOnClickListener {
                saveListener?.invoke(ussdValue)
                dismiss()
            }

            btnNo.setOnClickListener {
                dismiss()
            }
        }
    }

    fun saveClickListener(f: SingleBlock<Int>) {
        saveListener = f
    }


}




