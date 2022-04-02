package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.offers


import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.databinding.DialogLoadingAskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class LoadingAskDialog(
    private val contextFragment: Context
) : AlertDialog(contextFragment) {

    private var _binding: DialogLoadingAskBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var saveListener: SingleBlock<Boolean>? = null

    init {
        _binding = DialogLoadingAskBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        setCancelable(false)
        binding.btnWait.setOnClickListener {
            dismiss()
        }
        binding.btnStop.setOnClickListener {
            saveListener?.invoke(true)
        }
    }

    fun stopClickListener(f: SingleBlock<Boolean>) {
        saveListener = f
    }
}
