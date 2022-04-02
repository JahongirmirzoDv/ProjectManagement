package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.projects

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogCauseStatusBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock


class CauseRejectDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogCauseStatusBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var sendCause: SingleBlock<String>? =
        null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogCauseStatusBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        binding.btnSend.setOnClickListener {
            sendCause?.invoke(binding.etCause.text.toString())
            dismiss()
        }
        binding.btnNegative.setOnClickListener {
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun sendClickListener(f: SingleBlock<String>) {
        sendCause = f
    }
}