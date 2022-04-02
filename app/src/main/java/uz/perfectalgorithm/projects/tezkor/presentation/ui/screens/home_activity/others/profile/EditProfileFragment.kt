package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.profile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.UpdateUserDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UpdateUserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentEditProfileBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.RememberSaveDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.profile.EditProfileViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlAll
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.io.File
import java.util.*

/**
 * ProfileEdit  fragmenti to'liq holda tayyor qilingan
 **/
@AndroidEntryPoint
class EditProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var booleanImage = false
    private var booleanName = false
    private var booleanLastName = false
    private var booleanDateOfBirth = false

    private var booleanEdited = false

    private var mFile: File? = null
    private var isSend = false

    private var _binding: FragmentEditProfileBinding? = null
    private val binding: FragmentEditProfileBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.getUserData()
        viewModel.updateUserLiveData.observe(viewLifecycleOwner, updateUserDataObserver)
        viewModel.userResponseLiveData.observe(viewLifecycleOwner, userDataObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val userDataObserver = Observer<UserDataResponse.Data> { userData ->
        binding.apply {
            etName.setText(userData.firstName)
            etLastName.setText(userData.lastName)
            txtDateOfBirth.text = userData.birthDate
            viewModel.oldFirstName = userData.firstName.toString()
            viewModel.oldLastName = userData.lastName.toString()
            viewModel.oldBirthDate = userData.birthDate.toString()
            booleanName = false
            booleanLastName = false
            booleanDateOfBirth = false
            booleanImage = false
            btnUpdate.gone()
            userData.image?.let { binding.imgProfile.loadImageUrlUniversal(it, R.drawable.ic_user) }

        }
    }

    private val updateUserDataObserver = Observer<UpdateUserDataResponse.Data> {
        it.firstName?.let { it1 ->
            it.lastName?.let { it2 ->
                viewModel.setFullNameToLocal(
                    it1,
                    it2
                )
            }
        }
        makeSuccessSnack(getString(R.string.changed_data))
        if (isSend) {
            findNavController().navigateUp()
            isSend = false
        }
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private fun loadViews() {

        (requireActivity() as HomeActivity).onBackClickListener {
            checkBeforeOnBackPress()
        }

        binding.apply {
            btnBack.setOnClickListener {
                checkBeforeOnBackPress()
            }

            etName.addTextChangedListener {
                booleanName =
                    !(it.toString() == viewModel.oldFirstName && viewModel.oldFirstName.isNotEmpty())
                checkData()
            }
            etLastName.addTextChangedListener {
                booleanLastName =
                    !(it.toString() == viewModel.oldLastName && viewModel.oldLastName.isNotEmpty())
                checkData()
            }

            imgProfile.setOnClickListener {
                selectImage()
            }

            txtDateOfBirth.setOnClickListener {
                openDatePicker()
            }

            btnUpdate.setOnClickListener {
                sendUpdateData()
            }

        }
    }

    private fun selectImage() {
        if (mFile == null) {
            booleanImage = false
        }
        checkData()

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
                        mFile = ImagePicker.getFile(data)!!
                        binding.imgProfile.setImageURI(data?.data!!)
                        booleanImage = true
                        checkData()
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
        booleanDateOfBirth = date != viewModel.oldBirthDate
        checkData()
    }

    private fun checkData() {
        if (booleanLastName || booleanName || booleanDateOfBirth || booleanImage) {
            binding.btnUpdate.visible()
            booleanEdited = true
        } else {
            booleanEdited = false
            binding.btnUpdate.gone()
        }
    }

    private fun sendUpdateData() {
        isSend = true
        binding.apply {
            viewModel.updateUserData(
                updateUserDataRequest = UpdateUserDataRequest(
                    etName.text.toString(),
                    etLastName.text.toString(),
                    txtDateOfBirth.text.toString()
                ),
                mFile
            )
        }
    }

    private fun checkBeforeOnBackPress() {
        checkData()
        if (booleanEdited) {
            val rememberDialog = RememberSaveDialog(requireActivity())
            rememberDialog.saveClickListener {
                sendUpdateData()
            }
            rememberDialog.cancelClickListener {
                findNavController().navigateUp()
            }
            rememberDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

}