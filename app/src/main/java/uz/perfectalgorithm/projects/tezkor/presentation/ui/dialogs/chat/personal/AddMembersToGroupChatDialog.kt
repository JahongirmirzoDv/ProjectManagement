package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal


/**
 * Craeted by Davronbek Raximjanov on 12.08.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogAddMembersToGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.add_members.WorkerListForCreateGroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

class AddMembersToGroupChatDialog(
    private val activity: Activity,
    private val workers: List<AllWorkersShortDataResponse.WorkerShortDataItem>
) : AlertDialog(activity) {

    private val adapter = WorkerListForCreateGroupChatAdapter()

    private val listWorkers = ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>()

    private var _binding: DialogAddMembersToGroupChatBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var listenAddMembers: SingleBlock<List<Int>>? = null

    init {
        _binding = DialogAddMembersToGroupChatBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {

        listWorkers.clear()
        listWorkers.addAll(workers)

        binding.apply {
            btnOk.gone()

            rvWorkers.layoutManager = LinearLayoutManager(activity)
            rvWorkers.adapter = adapter
            adapter.submitList(listWorkers)
            adapter.setOnCheckListener {
                if (getCheckedWorkersIds().isNotEmpty()) {
                    btnOk.visible()
                } else {
                    btnOk.gone()
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnOk.setOnClickListener {
                listenAddMembers?.invoke(getCheckedWorkersIds())
                dismiss()
            }
        }
    }

    private fun getCheckedWorkersIds(): List<Int> {
        val listIds = ArrayList<Int>()
        listWorkers.forEach {
            if (it.isChecked) it.id?.let { it1 -> listIds.add(it1) }
        }
        return listIds
    }

    fun addMembersListener(f: SingleBlock<List<Int>>) {
        listenAddMembers = f
    }

}




