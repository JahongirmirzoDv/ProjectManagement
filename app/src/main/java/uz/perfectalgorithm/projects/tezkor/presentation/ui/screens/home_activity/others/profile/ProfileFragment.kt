package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.GenderEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.ImageStatusEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentProfileBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.ShowAvatarImageDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.profile.ProfileViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Profile fragmenti to'liq holda tayyor qilingan
 **/
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.getUserData()
        viewModel.userResponseLiveData.observe(viewLifecycleOwner, userDataObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    @SuppressLint("SetTextI18n")
    private val userDataObserver = Observer<UserDataResponse.Data> { userData ->
        timberLog("User fullName : ${userData.firstName} ${userData.firstName}")
        timberLog("User Id : ${userData.id}")
        timberLog("User email : ${userData.email}")
        binding.apply {
            txtEmail.text = userData.email
            userData.image?.let { binding.imgProfile.loadImageUrlUniversal(it, R.drawable.ic_user) }
            txtFullName.text = "${userData.firstName} ${userData.lastName}"
            txtAllTasksCount.text = userData.getTasksCount?.all.toString()
            txtCompletedTasksCount.text = userData.getTasksCount?.done.toString()
            txtNoCompletedTasksCount.text = userData.getTasksCount?.undone.toString()
            txtPhoneNumber.text = userData.phoneNumber
            txtDateOfBirth.text = userData.birthDate
            txtPosition.text = ""
            viewModel.setUserDataToVm(userData)
            try {
                if (userData.leader?.isNotEmpty() == true) {
                    txtLeaderName.text = userData.leader?.get(0)?.fullName ?: ""
                    userData.leader?.get(0)?.image?.let {
                        imgLeader.loadImageUrlUniversal(
                            it,
                            R.drawable.ic_user
                        )
                    }
                } else {
                    line3.gone()
                    llLeaderData.gone()
                    txtLeaderTitle.gone()
                }
            } catch (e: Exception) {
                line3.gone()
                llLeaderData.gone()
                txtLeaderTitle.gone()
            }

            when (userData.sex) {
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

            imgProfile.setOnClickListener {
                openAvatarImageDialog()
            }
        }
    }

    private fun openAvatarImageDialog() {
        val userData = viewModel.getUserDataFromVm()
        userData?.image?.let {
            val showAvatarDialog =
                ShowAvatarImageDialog(
                    it,
                    userData.firstName + " " + userData.lastName,
                    ImageStatusEnum.USER_AVATAR.text
                )
            showAvatarDialog.show(
                (requireActivity() as HomeActivity).supportFragmentManager,
                "Show Image Dialog"
            )
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
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnEditProfile.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            }

        }
    }
}