package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.edit_contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.UpdateContactDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.UpdateContactDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentEditContactBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.RememberSaveDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.edit_contact.EditContactViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.showToast
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class EditContactFragment : Fragment() {
    /** Har bir hodim uchun uzi ishlayotgan kompaniyadagi boshqa
     *  hodimlarni contakt ma'lumotlarini o'zgartirish oynasi, yani bunda hodim
     *  boshqa hodimning asl malumotlarini
     *  emas balki uzi uchun korinadigan ism familiyasini o'zgartirishi mumkin**/

    private var _binding: FragmentEditContactBinding? = null
    private val binding get() = _binding!!

    private val args: EditContactFragmentArgs by navArgs()

    private val viewModel: EditContactViewModel by viewModels()

    private var booleanName = false
    private var booleanLastName = false
    private var booleanEdited = false
    private var isSend = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditContactBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadViews()
        viewModel.loadStaff(args.workerId)
    }

    private fun loadViews() {
        hideAppBar()
        hideBottomMenu()
        binding.apply {
//            etName.setText(args.workerDataEt.contact?.firstName)
//            etLastName.setText(args.workerDataEt.contact?.lastName)

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

            btnBack.setOnClickListener {
                checkData()
                hideKeyboard()
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

            btnUpdate.setOnClickListener {
                sendUpdateData()
                hideKeyboard()
            }

            btnAdvanced.setOnClickListener {
                showToast("adwawd")
                viewModel.staff.value.let {
                    if (it is DataWrapper.Success) {
                        findNavController().navigate(
                            EditContactFragmentDirections.ecfToEditWorkerFragment(it.data)
                        )
                    }
                }
            }
        }
    }

    private fun loadObservers() {
//        viewModel.oldFirstName = args.workerDataEt.contact!!.firstName!!
//        viewModel.oldLastName = args.workerDataEt.contact!!.lastName!!
        viewModel.updateContactLiveData.observe(viewLifecycleOwner, updateContactObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)

        viewModel.staff.simpleCollect(this, binding.progressLayout.progressLoader) { worker ->
            viewModel.oldFirstName = worker.contact!!.firstName!!
            viewModel.oldLastName = worker.contact.lastName!!
            binding.etName.setText(worker.contact.firstName)
            binding.etLastName.setText(worker.contact.lastName)
            binding.progressLayout.progressLoader.isVisible = false
        }
    }

    private val updateContactObserver = Observer<UpdateContactDataResponse.Data> { userData ->
        binding.apply {
            etName.setText(userData.firstName)
            etLastName.setText(userData.lastName)
            viewModel.oldFirstName = userData.firstName.toString()
            viewModel.oldLastName = userData.lastName.toString()
            booleanName = false
            booleanLastName = false
            btnUpdate.gone()
            if (isSend) {
                findNavController().navigateUp()
                isSend = false
            }
        }
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    private val notConnectionObserver = Observer<Unit> {
        makeErrorSnack()
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showAppBar()
        showBottomMenu()
    }

    private fun checkData() {
        if (booleanLastName || booleanName) {
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
            viewModel.updateContactData(
                UpdateContactDataRequest(
                    workerId = args.workerId,
                    firstName = etName.text.toString(),
                    lastName = etLastName.text.toString()
                )
            )
        }
    }
}