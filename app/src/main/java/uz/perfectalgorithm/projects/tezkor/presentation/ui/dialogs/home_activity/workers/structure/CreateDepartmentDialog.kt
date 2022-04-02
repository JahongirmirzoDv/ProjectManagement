package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure


/**
 * Craeted by Davronbek Raximjanov on 30.06.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ArrayAdapter
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.MDepartmentModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogCreateDepartmentBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.adding.showTooltip
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

class CreateDepartmentDialog(
    private val activity: Activity,
    private val list: List<DepartmentListResponse.DepartmentDataItem>
) : AlertDialog(activity) {

    private var _binding: DialogCreateDepartmentBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var createListener: DoubleBlock<String, Int>? = null

    init {
        _binding = DialogCreateDepartmentBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {

        setUpSpinnerDepartments()

        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                progressVisibility(true)

                var isInputCompleted = true

                if (etName.text.isNullOrBlank()) {
                    etName.error = context.getString(R.string.error_department_name)
                    isInputCompleted = false
                }
                if (spinnerDepartments.selectedItem == null) {
                    txtTitleAcceptDepartment.showTooltip(context.getString(R.string.error_department_parent))
                    isInputCompleted = false
                }

                if (isInputCompleted) {
                    createListener?.invoke(
                        etName.text.toString().trim(),
                        (spinnerDepartments.selectedItem as MDepartmentModel).getId()
                    )
                }
            }
            btnNo.setOnClickListener {
                dismiss()
            }
        }
    }

    fun saveClickListener(f: DoubleBlock<String, Int>) {
        createListener = f
    }

    private fun setUpSpinnerDepartments() {
        val departmentMs = ArrayList<MDepartmentModel>()
        for (department in list) {
            departmentMs.add(MDepartmentModel(title = department.title!!, id = department.id!!))
        }
        departmentMs.add(MDepartmentModel(title = "Boshlang'ich", id = 0))

        var arrayAdapter =
            ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, departmentMs)
        binding.spinnerDepartments.adapter = arrayAdapter
    }

    fun progressVisibility(boolean: Boolean) {
        if (boolean) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }
}




