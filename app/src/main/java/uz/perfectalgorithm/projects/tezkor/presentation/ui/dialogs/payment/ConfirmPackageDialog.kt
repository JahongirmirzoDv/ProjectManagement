package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.payment


/**
 * Craeted by Davronbek Raximjanov on 26.06.2021
 * **/


import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.ConfirmPackageDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class ConfirmPackageDialog(
    private val activity: Activity,
    private val stuffCount: String,
    private val duration: String,
    private val cost: String,
    private val screenName: String
) : AlertDialog(activity) {

    private var _binding: ConfirmPackageDialogBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var saveListener: SingleBlock<String>? = null

    init {
        _binding = ConfirmPackageDialogBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {


        binding.apply {

            when (screenName) {
                "lifetime" -> {
                    txtDesc.text =
                        activity.getString(
                            R.string.month_confirm,
                            duration
                        )
                }
                "staff" -> {
                    txtDesc.text =
                        activity.getString(
                            R.string.staff_confirm,
                            stuffCount
                        )
                }
                else -> {
                    txtDesc.text =
                        activity.getString(
                            R.string.package_confirm, cost,
                            stuffCount, duration
                        )
                }
            }


            btnCancel.setOnClickListener {
                dismiss()
            }

            btnYes.setOnClickListener {
                saveListener?.invoke(screenName)
                dismiss()
            }

            btnNo.setOnClickListener {
                dismiss()
            }
        }
    }

    fun saveClickListener(f: SingleBlock<String>) {
        saveListener = f
    }


}




