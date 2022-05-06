package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.create

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.GenderEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.UserErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.CreateWorkerResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.PermissionsListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateWorkerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.create_worker.ChosenPositionAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.ChoosePositionDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.CreatePositionDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.create_worker.CreateWorkerViewModel
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CreateWorkerFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentCreateWorkerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateWorkerViewModel by viewModels()
    private lateinit var myFile: File
    private var mChosenPositions: ArrayList<StructurePositionsResponse.PositionsItem> = ArrayList()

    private var structureDataList = ArrayList<StructurePositionsResponse.DataItem>()

    private lateinit var dialogPosition: CreatePositionDialog

    lateinit var chosenPositionAdapter: ChosenPositionAdapter

    private var booleanImage = false
    private var isOutsource = false
    private var imgDrop = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
        hideBottomMenu()
        hideAppBar()
    }

    private fun loadViews() {

        binding.apply {

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            imgDropDown.setOnClickListener {
                if (imgDrop){
                    imgDrop = false
                    rvChosenPositions.visibility = View.VISIBLE
                    imgDropDown.setImageResource(R.drawable.ic_chevron_down)
                }else {
                    imgDrop = true
                    rvChosenPositions.visibility = View.GONE
                    imgDropDown.setImageResource(R.drawable.ic_chevron_up)
                }
            }
            imgProfile.setOnClickListener {
                selectImage()
            }
            txtProfileImg.setOnClickListener {
                selectImage()
            }

            txtDateOfBirth.setOnClickListener {
                openDatePicker()
            }


            btnSendTop.setOnClickListener {
                sendData()
            }

            btnSend.setOnClickListener {
                sendData()
            }

            btnAdd.setOnClickListener {
                setUpChoosePositionsDialog()
            }

            btnCreatePosition.setOnClickListener {
                viewModel.getMDepartments()?.let { it1 ->
                    viewModel.getMPermissions()?.let { it2 ->
                        dialogPosition =
                            CreatePositionDialog(
                                requireActivity(),
                                it1,
                                it2
                            )
                        dialogPosition.show()

                        dialogPosition.saveClickListener { request ->
                            viewModel.createPosition(request)
                        }
                    }
                }
            }

            loadChosenPositionAdapter()

            btnCheckOutsource.setOnClickListener {
                isOutsource = !isOutsource
                if (isOutsource) {
                    imgIsChecked.setImageResource(R.drawable.ic_check_ellipse)
                } else {
                    imgIsChecked.setImageResource(R.drawable.ic_no_check_ellipse)
                }
            }
        }
    }

    private fun loadChosenPositionAdapter() {
        chosenPositionAdapter = ChosenPositionAdapter()
        chosenPositionAdapter.submitList(listOf())
        chosenPositionAdapter.clearOnClickListener {
            mChosenPositions.remove(it)
            chosenPositionAdapter.submitList(mChosenPositions)
            chosenPositionAdapter.notifyDataSetChanged()
        }
        binding.rvChosenPositions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChosenPositions.adapter = chosenPositionAdapter
    }

    private fun setUpChoosePositionsDialog() {

        val dialog: ChoosePositionDialog = if (structureDataList.isNotEmpty()) {
            ChoosePositionDialog(requireActivity(), structureDataList)
        } else {
            ChoosePositionDialog(requireActivity(), listOf())
        }

        dialog.saveClickListener { chosenPositions ->
            mChosenPositions.clear()
            mChosenPositions.addAll(chosenPositions)
            chosenPositionAdapter.submitList(mChosenPositions)
            chosenPositionAdapter.notifyDataSetChanged()
            binding.txtCount.text = ChoosePositionDialog.chosenPositions.size.toString()
        }
        dialog.show()
    }

    private fun sendData() {

        /**Malumotlarni junatish: hodim yaratish. **/

        binding.apply {
            val isInputCompleted = isInputCompleted(
                listOf(
                    Triple(
                        etName.text.isNullOrBlank(),
                        etName,
                        getString(R.string.error_worker_name)
                    ),
                    Triple(
                        etLastName.text.isNullOrBlank(),
                        etLastName,
                        getString(R.string.error_worker_surname)
                    ),
                    Triple(
                        etEmail.text.isNullOrBlank(),
                        etEmail,
                        getString(R.string.error_worker_email)
                    ),
                    Triple(
                        etPhone.text.isNullOrBlank(),
                        etPhone,
                        getString(R.string.error_worker_phone_number)
                    ),
                    Triple(
                        etPassword.text.isNullOrBlank(),
                        etPassword,
                        getString(R.string.error_worker_password)
                    ),
                    Triple(
                        mChosenPositions.isNullOrEmpty(),
                        txtAttachPosition,
                        getString(R.string.error_worker_position)
                    ),
                    Triple(
                        txtDateOfBirth.text.isNullOrBlank(),
                        txtDate,
                        getString(R.string.error_worker_birthday)
                    ),
                ), nsvRoot
            )

//            if (etName.text.isNullOrBlank()) {
//                etName.error = getString(R.string.error_worker_name)
//                isInputCompleted = false
//            }
//            if (etLastName.text.isNullOrBlank()) {
//                etLastName.error = getString(R.string.error_worker_surname)
//                isInputCompleted = false
//            }
//            if (etEmail.text.isNullOrBlank()) {
//                etEmail.error = getString(R.string.error_worker_email)
//                isInputCompleted = false
//            }
//            if (etPhone.text.isNullOrBlank()) {
//                etPhone.error = getString(R.string.error_worker_phone_number)
//                isInputCompleted = false
//            }
//            if (etPassword.text.isNullOrBlank()) {
//                etPassword.error = getString(R.string.error_worker_password)
//                isInputCompleted = false
//            }
//            if (mChosenPositions.isNullOrEmpty()) {
//                txtAttachPosition.showTooltip(getString(R.string.error_worker_position))
//                isInputCompleted = false
//            }
//            if (txtDateOfBirth.text.isNullOrBlank()) {
//                txtDate.showTooltip(getString(R.string.error_worker_birthday))
//                isInputCompleted = false
//            }

            if (isInputCompleted) {
                var chosenGender = GenderEnum.MALE.text
                if (rbFemale.isChecked) chosenGender = GenderEnum.FEMALE.text

                if (booleanImage) {
                    viewModel.createWorkerWithImage(
                        firstName = etName.text.toString().trim(),
                        lastName = etLastName.text.toString().trim(),
                        dateOfBirth = txtDateOfBirth.text.toString().trim(),
                        phone = etPhone.rawText.toString().trim(),
                        email = etEmail.text.toString().trim(),
                        password = etPassword.text.toString().trim(),
                        positions = mChosenPositions,
                        file = myFile,
                        gender = chosenGender,
                        isOutsource = isOutsource
                    )
                } else {
                    viewModel.createWorker(
                        firstName = etName.text.toString().trim(),
                        lastName = etLastName.text.toString().trim(),
                        dateOfBirth = txtDateOfBirth.text.toString().trim(),
                        phone = etPhone.rawText.toString().trim(),
                        email = etEmail.text.toString().trim(),
                        password = etPassword.text.toString().trim(),
                        positions = mChosenPositions,
                        gender = chosenGender,
                        isOutsource = isOutsource
                    )
                }
            }
        }
    }

    private fun loadObservers() {

        viewModel.getBelongsToUserPermissions()
        viewModel.getDepartmentListData()
        viewModel.getUserPermissionsList()

        viewModel.getStructureWithPositionsData()
        viewModel.createWorkerLiveData.observe(viewLifecycleOwner, createWorkerObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.structureWithPositionsLiveData.observe(viewLifecycleOwner, structureObserver)
        viewModel.permissionsLiveData.observe(viewLifecycleOwner, permissionListObserver)
        viewModel.belongsToUserPermissionsLiveData.observe(
            viewLifecycleOwner,
            belongsToUserPermissionsObserver
        )
        viewModel.departmentListLiveData.observe(viewLifecycleOwner, departmentsObserver)
        viewModel.createPositionLiveData.observe(viewLifecycleOwner, createPositionObserver)

    }



    private val belongsToUserPermissionsObserver = Observer<List<String>> { permissions ->
        if (permissions != null) {
            viewModel.setCorrectForUIPermissions(permissions)
            if (viewModel.canAddPosition) binding.btnCreatePosition.visible()
            else binding.btnCreatePosition.gone()
        }
    }

    private val structureObserver =
        Observer<List<StructurePositionsResponse.DataItem>> { structureData ->
            if (structureData != null) {
                structureDataList.clear()
                structureDataList.addAll(structureData)
            }
        }

    private val createWorkerObserver = Observer<CreateWorkerResponse.DataItem> {
        makeSuccessSnack(getString(R.string.create_staff_completed_message))
        findNavController().navigateUp()
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val errorObserver = Observer<Throwable> { throwable ->
        if (throwable is Exception) {
            handleCustomException(throwable) { error ->
                when {
                    error.errors?.map { it.error }
                        ?.contains(UserErrorsEnum.PHONE_NUMBER_EXISTS.message) == true ->
                        binding.etPhone.error = "Bu telefon raqam ro'yxatdan o'tkazilgan"
                    error.errors?.map { it.error }
                        ?.contains(UserErrorsEnum.INVALID_EMAIL.message) == true ->
                        binding.etEmail.error = "Haqiqiy elektron pochta kiriting"
                    else -> handleException(throwable)
                }
            }
        } else {
            makeErrorSnack(throwable.message.toString())
        }
    }

    private val createPositionObserver = Observer<CreatePositionResponse.PositionData> {
        makeSuccessSnack(getString(R.string.position_created_success))
        dialogPosition.dismiss()
        viewModel.getDepartmentListData()
        viewModel.getStructureWithPositionsData()
    }

    private val departmentsObserver =
        Observer<List<DepartmentListResponse.DepartmentDataItem>> { departments ->
            if (departments != null) {
                viewModel.setMDepartments(departments)
            }
        }

    private val permissionListObserver =
        Observer<List<PermissionsListResponse.PermissionData>> { permissions ->
            if (permissions != null) {
                viewModel.setMPermissions(permissions)
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        showBottomMenu()
        showAppBar()
    }

    private fun selectImage() {
        booleanImage = false
        ImagePicker.with(requireActivity())
            .saveDir(
                File(
                    requireActivity().getExternalFilesDir(null)?.absolutePath,
                    "ProjectManagement"
                )
            )
            .maxResultSize(1080, 1920)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        myFile = ImagePicker.getFile(data)!!
                        binding.imgProfile.setImageURI(data?.data!!)
                        booleanImage = true
                    }
                }
            }
    }

    private fun openDatePicker() {
        val now = Calendar.getInstance()

        val dialog = DatePickerDialog.newInstance(
            this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show(childFragmentManager, "DatePickerDialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, month: Int, day: Int) {
        //date ...

        var date = ""
        date += "$year-"

        val m = month + 1

        date += if (m < 10) "0$m-"
        else "$m-"

        date += if (day < 10) "0$day"
        else "$day"

        binding.txtDateOfBirth.text = date

    }


}