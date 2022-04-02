package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.offers


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.DialogLoadingBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

class LoadingDialog : DialogFragment() {

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var closeClick: EmptyBlock? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        _binding = DialogLoadingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.MDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                closeClick?.invoke()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainDialog.setOnClickListener {
            closeClick?.invoke()
        }
    }

    fun stopClickListener(f: EmptyBlock) {
        closeClick = f
    }
}




