package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.choose_company

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.CompanyErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalDatabase
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.CompaniesResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentChooseCompanyBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.entry.companies.CompaniesAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.entry.SaveCompanyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.choose_company.ChooseCompanyViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.HandledError
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject

/***
 * bu Kirishdagi kompaniya tanlash bo'limi
 */

@AndroidEntryPoint
class ChooseCompanyFragment : Fragment() {

    private var _binding: FragmentChooseCompanyBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: CompaniesAdapter
    private lateinit var companyData: CompaniesResponse.CompanyData

    private val viewModel: ChooseCompanyViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var localDatabase: LocalDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseCompanyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        loadViews()
        loadObservers()
        loadActions()
    }

    private fun loadActions() {
        binding.fabAdd.setOnClickListener {
            if (storage.role == RoleEnum.OWNER.text) {
                if (requireActivity() is HomeActivity) {
                    findNavController().navigate(R.id.action_swapCompanyFragment_to_createCompanyFragment)
                } else {
                    findNavController().navigate(R.id.action_chooseCompanyFragment_to_createCompanyFragment)
                }
            } else {
                makeErrorSnack(getString(R.string.only_owner_create))
            }
        }

        binding.materialAdd.setOnClickListener {
            if (storage.role == RoleEnum.OWNER.text) {
                if (requireActivity() is HomeActivity) {
                    findNavController().navigate(R.id.action_swapCompanyFragment_to_createCompanyFragment)
                } else {
                    findNavController().navigate(R.id.action_chooseCompanyFragment_to_createCompanyFragment)
                }
            } else {
                makeErrorSnack(getString(R.string.only_owner_create))
            }
        }
    }

    private fun loadViews() {

        binding.apply {
            GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.VERTICAL,
                false
            ).apply {
                rvCompanies.layoutManager = this
            }

            adapter = CompaniesAdapter()

            adapter.submitList(listOf())
            adapter.setOnClickListener { companyData, i ->
                rvCompanies.scrollToPosition(i)
                openDialog(companyData)
            }
            rvCompanies.adapter = adapter
        }
    }

    private fun openDialog(companyData: CompaniesResponse.CompanyData) {
        val dialog = SaveCompanyDialog(requireActivity())
        dialog.saveClickListener {
            this.companyData = companyData
            if (it) {
                viewModel.isChosenCompany = true
                viewModel.saveCompany(companyData)
            } else {
                viewModel.isChosenCompany = false
                viewModel.saveCompany(companyData)
            }
        }
        dialog.show()
    }

    private fun loadObservers() {
        viewModel.getCompaniesData()
//        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
//        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
//        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)
//        viewModel.companiesLiveData.observe(viewLifecycleOwner, companiesObserver)
//        viewModel.chooseCompanyLiveData.observe(viewLifecycleOwner, chooseCompanyObserver)
//        viewModel.saveCompanyToUserLiveData.observe(viewLifecycleOwner, saveCompanyToUserObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.companiesResponse.collect { dataWrapper ->
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
                            if (dataWrapper.data.isEmpty()) {
                                binding.apply {
                                    container.show()
                                    rvCompanies.gone()
                                    fabAdd.gone()
                                }
                            } else {
                                container.gone()
                                rvCompanies.show()
                                fabAdd.show()
                                adapter.submitList(dataWrapper.data)
                            }

                            progressLayout.progressLoader.gone()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveCompanyResponse.collect { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> {
                        binding.progressLayout.progressLoader.gone()
                        handleException(dataWrapper.error)
                    }
                    is DataWrapper.Loading -> {
                        binding.progressLayout.progressLoader.visible()
                    }
                    is DataWrapper.Success -> {
                        storage.selectedCompanyName = companyData.title?:""
                        if (viewModel.isChosenCompany) {
                            viewModel.chooseAndSaveCompany(dataWrapper.data.companyId!!)
                        } else {
                            viewModel.chooseNoSaveCompany(dataWrapper.data.companyId!!)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chooseCompanyId.collect { companyId ->
                companyId?.let {
                    if (requireActivity() is HomeActivity) {
                        findNavController().navigateUp()
                    } else {
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }
    }

//    private val progressObserver = Observer<Boolean> {
//        if (it) {
//            binding.progressBar.visible()
//        } else {
//            binding.progressBar.gone()
//        }
//    }
//
//    private val notConnectionObserver = Observer<Unit> {
//        showToast(resources.getString(R.string.not_connection))
//    }
//
//    private val errorObserver = Observer<String> {
//        showToast(it)
//    }

//    private val companiesObserver = EventObserver<ArrayList<CompaniesResponse.CompanyData>> {
//        adapter.submitList(it)
//    }

//    private val chooseCompanyObserver = Observer<Int> {
//        if (requireActivity() is HomeActivity) {
//            findNavController().navigateUp()
//        } else {
//            startActivity(Intent(requireContext(), HomeActivity::class.java))
//            requireActivity().finish()
//        }
//    }

//    private val saveCompanyToUserObserver =
//        Observer<UserDataWithCompanyResponse.UserWData> { userDataWithCompany ->
//            if (viewModel.isChosenCompany) {
//                viewModel.chooseAndSaveCompany(userDataWithCompany.companyId!!)
//            } else {
//                viewModel.chooseNoSaveCompany(userDataWithCompany.companyId!!)
//            }
//        }
}