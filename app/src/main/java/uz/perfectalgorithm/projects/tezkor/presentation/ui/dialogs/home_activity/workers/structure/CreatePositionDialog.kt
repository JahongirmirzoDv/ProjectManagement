package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure


/**
 * Craeted by Davronbek Raximjanov on 01.07.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ArrayAdapter
import com.google.android.material.chip.Chip
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.MDepartmentModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreatePositionRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.PermissionsListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.DialogCreatePositionBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.adding.showTooltip
import uz.perfectalgorithm.projects.tezkor.utils.getPermissionsTitles
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

class CreatePositionDialog(
    private val activity: Activity,
    private val list: List<DepartmentListResponse.DepartmentDataItem>,
    private val userPermissionsList: List<PermissionsListResponse.PermissionData>
) : AlertDialog(activity) {

    private var _binding: DialogCreatePositionBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var createListener: SingleBlock<CreatePositionRequest>? = null

    init {
        _binding = DialogCreatePositionBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {

        setUpSpinnerHierarchy()
        setUpSpinnerDepartments()
        setChipGroup(userPermissionsList)

        binding.apply {

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnYes.setOnClickListener {
                var isInputCompleted = true

                if (etName.text.isNullOrBlank()) {
                    etName.error = context.getString(R.string.error_position_name)
                    isInputCompleted = false
                }
                if (spinnerDepartments.selectedItem == null) {
                    txtTitleAcceptDepartment.showTooltip(context.getString(R.string.error_position_department))
                    isInputCompleted = false
                }
                if (spinnerHierarchy.selectedItem == null) {
                    txtTitleHierarchy.showTooltip(context.getString(R.string.error_position_ierarchy))
                    isInputCompleted = false
                }

                if (isInputCompleted) {
                    progressVisibility(true)
                    createListener?.invoke(
                        CreatePositionRequest(
                            title = etName.text.toString(),
                            department = (spinnerDepartments.selectedItem as MDepartmentModel).getId(),
                            hierarchy = spinnerHierarchy.selectedItem.toString(),
                            company = 0,
                            permissions = binding.chipGroupPermissions.checkedChipIds
                        )
                    )
                }
            }
            btnNo.setOnClickListener {
                dismiss()
            }
        }
    }

    fun saveClickListener(f: SingleBlock<CreatePositionRequest>) {
        createListener = f
    }

    private fun setUpSpinnerDepartments() {
        val departmentMs = ArrayList<MDepartmentModel>()
        for (department in list) {
            departmentMs.add(MDepartmentModel(title = department.title!!, id = department.id!!))
        }

        val arrayAdapter =
            ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, departmentMs)
        binding.spinnerDepartments.adapter = arrayAdapter
    }

    private fun progressVisibility(boolean: Boolean) {
        if (boolean) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    private fun setUpSpinnerHierarchy() {
        ArrayAdapter.createFromResource(
            activity,
            R.array.group_hierarchy,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHierarchy.adapter = adapter
        }
    }

    private fun setChipGroup(permissions: List<PermissionsListResponse.PermissionData>) {
        binding.chipGroupPermissions.removeAllViews()
        for (a in permissions) {
            val chip = Chip(activity)
            chip.id = a.id

//          chip.text = a.titleUz

            chip.text = a.id.getPermissionsTitles(activity)
            chip.isCheckable = true
            binding.chipGroupPermissions.addView(chip)
        }
    }
}




