package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.meeting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogMyStateBinding

/**
 *Created by farrukh_kh on 9/3/21 9:51 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.tasks.meeting
 **/
@AndroidEntryPoint
class MyStateDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogMyStateBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private val args by navArgs<MyStateDialogFragmentArgs>()

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
        _binding = FragmentDialogMyStateBinding.inflate(layoutInflater)
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
        when (args.member.state) {
            "waiting" -> {
                tvStateTitle.text = "Kutilmoqda"
                ivStateIcon.setImageResource(R.drawable.ic_member_waiting)
            }
            "accepted" -> {
                tvStateTitle.text = "Qatnashaman"
                ivStateIcon.setImageResource(R.drawable.ic_member_accepted)
            }
            "rejected" -> {
                tvStateTitle.text = "Qatnasha olmayman"
                ivStateIcon.setImageResource(R.drawable.ic_member_rejected)
            }
        }
        cvState.strokeColor = args.member.getStrokeColor()
        tvDescription.text = args.member.description
        btnEdit.setOnClickListener {
            findNavController().navigate(
                MyStateDialogFragmentDirections.toSelectMemberStateDialogFragment(
                    args.member.id!!,
                    args.title,
                    args.member.description?:"",
                    args.startTime,
                    args.endTime,
                    args.leader
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}