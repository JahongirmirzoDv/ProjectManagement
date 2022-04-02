package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.worker_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.GenderEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.ImageStatusEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.UserErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWorkerDetailBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.ShowAvatarImageDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.worker_detail.WorkerDetailViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class WorkerDetailFragment : Fragment() {

    private var _binding: FragmentWorkerDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkerDetailViewModel by viewModels()
    private val args: WorkerDetailFragmentArgs by navArgs()
    lateinit var worker: AllWorkersResponse.DataItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        worker = args.workerData
        hideAppBar()
        hideBottomMenu()
        loadButtons()
        loadViews()
        loadObservers()
        viewModel.loadStaff(args.staffId)
    }

    private fun loadObservers() {
        viewModel.addUserToFavouritesLiveData.observe(viewLifecycleOwner, addToFavouritesObserver)
        viewModel.removeFromFavouritesLiveData.observe(
            viewLifecycleOwner,
            removeFromFavouritesObserver
        )
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)

        viewModel.staff.simpleCollect(this, binding.progressLayout.progressLoader) { worker ->
            setWorkerToView(worker)
            this.worker = worker
            binding.progressLayout.progressLoader.isVisible = false
        }
    }

    private val addToFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            worker.isFavourite = true
            binding.imgSavedContact.setImageResource(R.drawable.ic_saved_contact)
        }
    }

    private val removeFromFavouritesObserver = Observer<WorkerDataResponse.WorkerData> {
        if (it != null) {
            worker.isFavourite = false
            binding.imgSavedContact.setImageResource(R.drawable.ic_star_grey_sd)
        }
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    private val errorObserver = Observer<Throwable> { throwable ->
        if (throwable is Exception) {
            handleCustomException(throwable) { error ->
                when {
                    error.errors?.map { it.error }
                        ?.contains(UserErrorsEnum.USER_ID_REQUIRED.message) == true ||
                            error.errors?.map { it.error }
                                ?.contains(UserErrorsEnum.INCORRECT_USER.message) == true ->
                        makeErrorSnack("Xodim mavjud emas. Boshqa xodim tanlab ko'ring")
                    error.errors?.map { it.error }
                        ?.contains(UserErrorsEnum.INCORRECT_COMPANY.message) == true ->
                        makeErrorSnack("Bu xodim sizning kompaniyaga tegishli emas")
                }
            }
        } else {
            makeErrorSnack(throwable.message.toString())
        }
    }

    private fun loadViews() {
        binding.apply {
//            if (args.workerData != null) {
//                setWorkerToView(args.workerData)
//            }
//            worker.let { worker ->
//                worker.image?.let { image ->
//                    imgProfile.loadImageUrlWithPlaceholder(image, R.drawable.ic_user)
//                }
//                if (worker.isFavourite == true) {
//                    imgSavedContact.setImageResource(R.drawable.ic_saved_contact)
//                } else {
//                    imgSavedContact.setImageResource(R.drawable.ic_star_grey_sd)
//                }
//
//                txtFullName.text = worker.firstName + " " + worker.lastName
//                txtPosition.text = worker.role ?: getString(R.string.staff)
//                txtPhoneNumber.text = "+${worker.phoneNumber}"
//                txtEmail.text = worker.email ?: ""
//                txtDateOfBirth.text = worker.birthDate ?: ""
//
//                try {
//                    if (worker.leader?.isNotEmpty() == true)
//                        txtLeaderName.text = worker.leader?.get(0)?.fullName ?: ""
//                    worker.leader?.get(0)?.image?.let {
//                        imgLeader.loadImageUrlWithPlaceholder(
//                            it,
//                            R.drawable.ic_user
//                        )
//                    }
//                } catch (e: Exception) {
//                    line3.gone()
//                    llLeaderData.gone()
//                    txtLeaderTitle.gone()
//                }
//
//                when (worker.sex) {
//                    GenderEnum.MALE.text -> {
//                        txtGender.text = getString(R.string.male_gender)
//                    }
//                    GenderEnum.FEMALE.text -> {
//                        txtGender.text = getString(R.string.female_gender)
//                    }
//                    else -> {
//                        txtGender.text = getString(R.string.no_detected)
//                    }
//
//                }
//                txtCompletedTasksCount.text = worker.tasks?.done.toString()
//                txtNoCompletedTasksCount.text = worker.tasks?.undone.toString()
//                txtAllTasksCount.text = worker.tasks?.all.toString()
//                txtReportPeriod.text = worker.reportPeriod.toString()
//            }

            imgProfile.setOnClickListener {
                openAvatarImageDialog()
            }

        }
    }

    private fun setWorkerToView(worker: AllWorkersResponse.DataItem) = with(binding) {
        worker.image?.let { image ->
            imgProfile.loadImageUrlWithPlaceholder(image, R.drawable.ic_user)
        }
        if (worker.isFavourite == true) {
            imgSavedContact.setImageResource(R.drawable.ic_saved_contact)
        } else {
            imgSavedContact.setImageResource(R.drawable.ic_star_grey_sd)
        }

        txtFullName.text = worker.firstName + " " + worker.lastName
        txtPosition.text = worker.role ?: getString(R.string.staff)
        txtPhoneNumber.text = "+${worker.phoneNumber}"
        txtEmail.text = worker.email ?: ""
        txtDateOfBirth.text = worker.birthDate ?: ""

        try {
            if (worker.leader?.isNotEmpty() == true)
                txtLeaderName.text = worker.leader[0].fullName ?: ""
            worker.leader?.get(0)?.image?.let {
                imgLeader.loadImageUrlWithPlaceholder(
                    it,
                    R.drawable.ic_user
                )
            }
        } catch (e: Exception) {
            line3.gone()
            llLeaderData.gone()
            txtLeaderTitle.gone()
        }

        when (worker.sex) {
            GenderEnum.MALE.text -> {
                txtGender.text = getString(R.string.male_gender)
            }
            GenderEnum.FEMALE.text -> {
                txtGender.text = getString(R.string.female_gender)
            }
            else -> {
                txtGender.text = getString(R.string.no_detected)
            }

        }
        txtCompletedTasksCount.text = worker.tasks?.done.toString()
        txtNoCompletedTasksCount.text = worker.tasks?.undone.toString()
        txtAllTasksCount.text = worker.tasks?.all.toString()
//        txtReportPeriod.text = worker.reportPeriod.toString()
    }

    private fun openAvatarImageDialog() {
        worker.image?.let {
            val showAvatarDialog =
                ShowAvatarImageDialog(
                    avatarImageUrl = it,
                    workerFullName = worker.firstName + " " + worker.lastName,
                    pictureStatus = ImageStatusEnum.WORKER_AVATAR.text
                )
            showAvatarDialog.show(
                (requireActivity() as HomeActivity).supportFragmentManager,
                "Show Image Dialog"
            )
        }
    }


    private fun loadButtons() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnCall.setOnClickListener {
                worker.phoneNumber?.let { phone ->
                    phoneCall(binding.root, phone)
                }
            }

            btnAddFavourites.setOnClickListener {
                if (worker.isFavourite!!) {
                    viewModel.removeFromFavourites(worker.id!!)
                } else {
                    viewModel.addToFavourite(worker.id!!)
                }
            }

            btnEdit.setOnClickListener {
                findNavController().navigate(
                    WorkerDetailFragmentDirections.toEditWorkerFragment(
                        worker
                    )
                )
            }
        }
    }
}