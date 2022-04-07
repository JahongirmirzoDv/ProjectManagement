package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.company

import android.app.Activity
import android.os.Bundle
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.CompanyErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.DirectionResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateCompanyBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.entry.companies.SpinnerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.createCompany.CreateCompanyViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.io.File

/***
 * bu Kirishdagi kompaniya yaratish bo'limi
 */

@AndroidEntryPoint
class CreateCompanyFragment : Fragment() {

    private val TAG = "CreateCompanyFragment"

    private var _binding: FragmentCreateCompanyBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))
    private var file: File? = null
    private val viewModel: CreateCompanyViewModel by viewModels()
    private lateinit var adapterSpinner: SpinnerAdapter
    private lateinit var adapterSpinner2: SpinnerAdapter
    private lateinit var listDirections: List<DirectionResponse.Direction>
    private lateinit var listDirections2: List<DirectionResponse.Direction>
    private val handler by lazy { android.os.Handler(Looper.getMainLooper()) }

//    private val progressObserver = EventObserver<Boolean> {
//        if (it) {
//            binding.progressBar.visible()
//            binding.btnBack.isEnabled = false
//            binding.btnSave.isEnabled = false
//        } else {
//            binding.btnBack.isEnabled = true
//            binding.btnSave.isEnabled = true
//            binding.progressBar.gone()
//        }
//    }

    private fun loadObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createCompanyResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnBack.isEnabled = true
                        binding.btnSave.isEnabled = true
                        handleCustomException(dataWrapper.error) { error ->
                            when {
                                error.errors?.map { it.error }
                                    ?.contains(CompanyErrorsEnum.COMPANY_NAME_EXIST.message) == true -> binding.etNameCompany.error =
                                    "Bu kompaniya nomi band. Boshqa kompaniya nomini yozib ko'ring"
                                error.errors?.map { it.error }
                                    ?.contains(CompanyErrorsEnum.WRONG_EMAIL.message) == true -> binding.etEmailCompany.error =
                                    "Haqiqiy email kiriting"
                                else -> handleException(dataWrapper.error)
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                        binding.btnBack.isEnabled = false
                        binding.btnSave.isEnabled = false
                    }
                    is DataWrapper.Success -> {
                        binding.progressLayout.progressLoader.gone()
                        binding.btnBack.isEnabled = true
                        binding.btnSave.isEnabled = true
                        makeSuccessSnack("Saqlandi...")
                        findNavController().navigateUp()
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.directionResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        handleCustomException(dataWrapper.error) { error ->
                            if (error.message == CompanyErrorsEnum.WRONG_COMPANY.message) {
                                findNavController().navigate(
                                    R.id.handledErrorDialogFragment,
                                    bundleOf(
                                        "error" to HandledError.CustomError(
                                            "Noto'g'ri kompaniya tanlandi",
                                            "Boshqa kompaniyani tanlab, qayta urinib ko'ring"
                                        )
                                    )
                                )
                            } else {
                                handleException(dataWrapper.error)
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                    }
                    is DataWrapper.Success -> {
                        binding.apply {
                            if (dataWrapper.data.isNotEmpty()) {
                                listDirections = ArrayList(dataWrapper.data)
                                adapterSpinner = SpinnerAdapter(listDirections)
                                algorithm1.adapter = adapterSpinner
//                                viewModel.getCompaniesData2(dataWrapper.data[0].id)
                            }
//                            if (dataWrapper.data.isEmpty()) {
//                                binding.apply {
//                                    container.show()
//                                    rvCompanies.gone()
//                                    fabAdd.gone()
//                                }
//                            } else {
//                                container.gone()
//                                rvCompanies.show()
//                                fabAdd.show()
//                                adapter.submitList(dataWrapper.data)
//                            }

                            progressLayout.progressLoader.gone()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.directionResponse2.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        handleCustomException(dataWrapper.error) { error ->
                            if (error.message == CompanyErrorsEnum.WRONG_COMPANY.message) {
                                findNavController().navigate(
                                    R.id.handledErrorDialogFragment,
                                    bundleOf(
                                        "error" to HandledError.CustomError(
                                            "Noto'g'ri kompaniya tanlandi",
                                            "Boshqa kompaniyani tanlab, qayta urinib ko'ring"
                                        )
                                    )
                                )
                            } else {
                                handleException(dataWrapper.error)
                            }
                        }
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                    }
                    is DataWrapper.Success -> {
                        binding.apply {
                            if (dataWrapper.data.isNotEmpty()) {
                                listDirections2 = ArrayList(dataWrapper.data)
                                adapterSpinner2 = SpinnerAdapter(listDirections2)
                                algorithm2.adapter = adapterSpinner2
                            }
//                            if (dataWrapper.data.isEmpty()) {
//                                binding.apply {
//                                    container.show()
//                                    rvCompanies.gone()
//                                    fabAdd.gone()
//                                }
//                            } else {
//                                container.gone()
//                                rvCompanies.show()
//                                fabAdd.show()
//                                adapter.submitList(dataWrapper.data)
//                            }

                            progressLayout.progressLoader.gone()
                        }
                    }
                }
            }
        }
//        viewModel.createCompanyLiveData.observe(viewLifecycleOwner, createCompanyObserver)
//        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
//        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
//        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)
    }

//    private val createCompanyObserver = EventObserver<Unit> {
//        showToast("Saqlandi...")
//        findNavController().navigateUp()
//    }

//    private val notConnectionObserver = EventObserver<Unit> {
//        showToast(resources.getString(R.string.not_connection))
//    }

//    private val errorObserver = EventObserver<String> {
//        showToast(it)
//        binding.progressBar.gone()
//        binding.btnBack.isEnabled = true
//        binding.btnSave.isEnabled = true
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCompanyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAction()
        loadObserver()
        viewModel.getCompaniesData()
        binding.apply {

            algorithm1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.getCompaniesData2(listDirections[position].id)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        }
    }

    private fun loadAction() {
        binding.toolbar.title = getString(R.string.create_company)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            if (binding.etNameCompany.text.isNullOrEmpty()) {
                makeErrorSnack(resources.getString(R.string.enter_name_company))
                return@setOnClickListener
            }
//            if (binding.etShortUsername.text.isNullOrEmpty()) {
//                makeErrorSnack(resources.getString(R.string.enter_username_company))
//                return@setOnClickListener
//            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmailCompany.text.toString()).matches()) {
                binding.etEmailCompany.error = resources.getString(R.string.wrong_email)
                return@setOnClickListener
            }

            viewModel.createCompany(
                binding.etNameCompany.text.toString(),
                file,
                listDirections2[binding.algorithm2.selectedItemPosition].title,
                binding.etNameCompany.text.toString(),
                binding.etEmailCompany.text.toString(),
            )

        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnImage.setOnClickListener {
            selectImage()
        }
        binding.ivCompany.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
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
                        binding.apply {
                            ivCompany.visible()
                            btnImage.gone()
                        }
                        file = ImagePicker.getFile(data)!!
                        binding.ivCompany.setImageURI(data?.data!!)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}