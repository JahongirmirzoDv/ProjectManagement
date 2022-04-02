package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalDatabase
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentOthersBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.LogOutDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.EntryActivity
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.OthersViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject


/**
 * Others fragmenti to'liq holda tayyor qilingan
 * Bu oyna navigationdagi bir bo'lim hisoblanadi. Undagi qilinishi kerak bo'lgan ishlar mavjud
 * 1. Vazifalar bo'limini
 * 2. Settings bo'limini
 * 3. Disk bo'limini
 **/
@AndroidEntryPoint
class OthersFragment : Fragment() {
    private var _binding: FragmentOthersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OthersViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var localDatabase: LocalDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOthersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomMenu()
        showAppBar()
        loadViews()
        loadObservers()
        viewModel.getUserData()

        (activity as HomeActivity).changeTitle()

        binding.relOfferAndComplaint.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_others_to_navigation_offers)
        }

//        binding.tasks.setOnClickListener {
//            findNavController().navigate(OthersFragmentDirections.actionNavigationOthersToOwnTaskFragment())
//        }

        binding.goalMapBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_others_to_createGoalMapFragment)
        }

        binding.rlDashboard.setOnClickListener {
            findNavController().navigate(OthersFragmentDirections.actionNavigationOthersToDashboardFragment())
        }

        binding.constPassToProfile.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_others_to_companyStatusFragment)
        }

        binding.relSupport.setOnClickListener {

            val url = "https://t.me/Algorithm_gateway_T"
            val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(sendIntent)
        }


//        binding.relDisc.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_others_to_errorFragment)
//        }

        binding.relSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_others_to_navigation_settings)
        }

//        binding.relEffect.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_others_to_errorFragment)
//        }

        /*binding.relSearch.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_others_to_errorFragment)
        }*/

    }


    private fun loadObservers() {
        viewModel.logoutLiveData.observe(viewLifecycleOwner, logoutUser)
        viewModel.userResponseLiveData.observe(viewLifecycleOwner, userDataObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    @SuppressLint("SetTextI18n")
    private val userDataObserver = Observer<UserDataResponse.Data> { userData ->
        binding.txtProfileName.text = userData.firstName + " " + userData.lastName
        binding.txtProfileEmail.text = userData.email
        userData.image?.let { binding.imgProfile.loadImageUrlUniversal(it, R.drawable.ic_user) }
    }

    private val logoutUser = EventObserver<Boolean> {
        val firebaseToken = storage.firebaseToken
        storage.pref.edit().clear().apply()
        storage.firebaseToken = firebaseToken
        startActivity(Intent(requireActivity(), EntryActivity::class.java))
        requireActivity().finish()
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressLayout.progressLoader.visible()
        } else {
            binding.progressLayout.progressLoader.gone()
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
            goalMap.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_others_to_createGoalMapFragment)
            }

            relOfferAndComplaint.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_others_to_navigation_offers)
            }
            txtPassToProfile.setOnClickListener {
                findNavController().navigate(OthersFragmentDirections.actionNavigationOthersToProfileFragment())
            }
            logout.setOnClickListener {
                openLogoutDialog()
            }
            swapCompany.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_others_to_swap_company)
            }
        }

        if (storage.role == RoleEnum.OWNER.text) {
            binding.swapCompany.visible()
            binding.constPassToProfile.show()
        }
    }

    private fun openLogoutDialog() {
        val dialog = LogOutDialog(requireActivity())
        dialog.saveClickListener {
            if (it) {
                viewModel.logout()
            } else {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}