package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers


/**
 * Craeted by Davronbek Raximjanov on 30.06.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogChoosePositionBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure.structure_with_position.StructureSectionWithPositionAdapter
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class ChoosePositionDialog(
    private val activity: Activity,
    private val sections: List<StructurePositionsResponse.DataItem>
) : AlertDialog(activity) {

    companion object {
        var chosenPositions: ArrayList<StructurePositionsResponse.PositionsItem> =
            ArrayList()
    }

    private var _binding: DialogChoosePositionBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var saveListener: SingleBlock<List<StructurePositionsResponse.PositionsItem>>? = null

    private lateinit var adapter: StructureSectionWithPositionAdapter

    init {
        _binding = DialogChoosePositionBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        chosenPositions.clear()

        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }

            btnSave.setOnClickListener {
                saveListener?.invoke(chosenPositions)
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            rvSections.layoutManager = LinearLayoutManager(activity)
            adapter = StructureSectionWithPositionAdapter()
            adapter.submitList(sections)
            rvSections.adapter = adapter
        }
    }

    fun saveClickListener(f: SingleBlock<List<StructurePositionsResponse.PositionsItem>>) {
        saveListener = f
    }

}




