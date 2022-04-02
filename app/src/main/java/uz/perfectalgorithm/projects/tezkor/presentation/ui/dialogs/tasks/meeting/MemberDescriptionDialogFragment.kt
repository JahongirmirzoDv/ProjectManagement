package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.meeting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogMemberDescriptionBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 *Created by farrukh_kh on 8/30/21 9:56 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.tasks.meeting
 **/
@AndroidEntryPoint
class MemberDescriptionDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogMemberDescriptionBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private val member by lazy { MemberDescriptionDialogFragmentArgs.fromBundle(requireArguments()).member }

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
        _binding = FragmentDialogMemberDescriptionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        member.user?.image?.let { ivProfile.loadImageUrl(it) }
        tvName.text = member.user?.fullName
        tvDescription.text = member.description
        btnOk.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}