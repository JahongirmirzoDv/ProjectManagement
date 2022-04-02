package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.edit_worker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.PositionsStateEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.EditWorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.positons.AllPositionsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentEditWorkerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure.RememberSaveDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.edit_worker.EditWorkerViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class EditWorkerFragment : Fragment() {

    /**
     * Bu oynada faqat ruxsatga ega bo'lgan hodimlar uchun (HR, Direktor ..) boshqa hodimlarni
     * asl malumotlarini o'zgartiriladi
     * **/

    private var booleanName = false
    private var booleanLastName = false
    private var booleanPosition = false

    private var _binding: FragmentEditWorkerBinding? = null
    private val binding: FragmentEditWorkerBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: EditWorkerFragmentArgs by navArgs()

    private val viewModel: EditWorkerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.workerData = args.workerData
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.getPositionList()
        viewModel.positionListLiveData.observe(viewLifecycleOwner, positionsObserver)
        viewModel.editWorkerLiveData.observe(viewLifecycleOwner, editWorkerDataObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val positionsObserver =
        Observer<List<AllPositionsResponse.PositionDataItem>> { posList ->
            viewModel.setAllPositionsStat(posList) { newPosList ->
                setChipGroupPositions(newPosList)
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
        findNavController().navigateUp()
    }

    private val editWorkerDataObserver = Observer<EditWorkerDataResponse.Data> {
        findNavController().navigateUp()
    }

    private fun loadViews() {
        binding.apply {
            btnBack.setOnClickListener {
                if (checkData()) {
                    val rememberDialog = RememberSaveDialog(requireActivity())
                    rememberDialog.saveClickListener {
                        viewModel.editWorkerData(
                            firstName = etName.text.toString(),
                            lastName = etLastName.text.toString()
                        )
                    }
                    rememberDialog.cancelClickListener {
                        findNavController().navigateUp()
                    }
                    rememberDialog.show()
                }else{
                    findNavController().navigateUp()
                }
            }

            viewModel.workerData.image?.let {
                imgProfile.loadImageUrlUniversal(it, R.drawable.ic_user)
            }
            etName.setText(viewModel.workerData.firstName)
            etLastName.setText(viewModel.workerData.lastName)

            etName.addTextChangedListener {
                booleanName = it.toString() != viewModel.workerData.firstName
                checkData()
            }

            etLastName.addTextChangedListener {
                booleanLastName = it.toString() != viewModel.workerData.lastName
                checkData()
            }

            btnUpdate.setOnClickListener {
                viewModel.editWorkerData(
                    firstName = etName.text.toString(),
                    lastName = etLastName.text.toString()
                )
            }
        }
    }

    private fun setChipGroupPositions(positions: List<AllPositionsResponse.PositionDataItem>) {
        binding.apply {
            chGOtherPositions.removeAllViews()
            chGAvailablePositions.removeAllViews()
            for (a in positions) {
                val chip = Chip(activity)
                chip.id = a.id!!
                chip.text = a.title

                chip.setOnClickListener {
                    viewModel.changePositionState(a)
                    booleanPosition = true
                    checkData()
                    setChipGroupPositions(viewModel.allStatPositions)
                }

                if (a.state == PositionsStateEnum.ADDED.text) {
                    chGAvailablePositions.addView(chip)
                } else {
                    chGOtherPositions.addView(chip)
                }
            }
        }
    }

    private fun checkData(): Boolean {
        return if (booleanLastName || booleanName || booleanPosition) {
            binding.btnUpdate.visible()
            true
        } else {
            binding.btnUpdate.gone()
            booleanName
        }
    }

}