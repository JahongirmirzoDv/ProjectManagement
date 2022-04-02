package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.offers.addOffers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BasePostOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentAddOffersBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base.BaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.SharedViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.offers.addOffers.AddOffersViewModel
import uz.perfectalgorithm.projects.tezkor.utils.PARTICIPANTS
import uz.perfectalgorithm.projects.tezkor.utils.PERFORMER
import uz.perfectalgorithm.projects.tezkor.utils.adding.showSelectPersonFragment
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import java.io.File
import javax.inject.Inject

/***
 * Abduraxmonov Abdulla 11/09/2021
 * bu others qismi uchun taklif va shikoyatlar qo'shish uchun ui
 */

@AndroidEntryPoint
class AddOffersFragment : BaseFragment(), ComplaintItemClickListener {
    private var _binding: FragmentAddOffersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddOffersViewModel by viewModels()
    private var type = 1
    val listFile = ArrayList<File>()
    private var adapter: ComplaintUserAdapter? = null

    //    private var spinnerAdapter: ArrayAdapter<String>? = null
    private val userList = ArrayList<AllWorkersResponse.DataItem>()
    private val users = ArrayList<String>()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var localStorage: LocalStorage

    private val getProgressData = EventObserver<Boolean> { it ->
        if (it) {
            showLoadingDialog()
        } else {
            loadingDialog?.dismiss()
            loadingAskDialog?.dismiss()
        }
    }
    private val getError = EventObserver<Throwable> {
//        binding.progressBar.gone()
        loadingDialog?.dismiss()
        loadingAskDialog?.dismiss()
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }
    private val postOfferResponse = EventObserver<BasePostOfferResponse.DataResult> { it ->
        makeSuccessSnack("Saqlandi...")
        loadingDialog?.dismiss()
        loadingAskDialog?.dismiss()
        findNavController().navigateUp()
    }

    private val allWorkerData = EventObserver<List<AllWorkersResponse.DataItem>> { it ->
        userList.clear()
        userList.addAll(it)
        users.clear()
        userList.forEach {
            users.add(it.lastName + " " + it.firstName)
        }
//        userList.filter { it.role == RoleEnum.OWNER.text }.forEach {
//            spectators.add(it)
//        }
//        adapter!!.submitList(spectators)
    }


    private val postComplaintResponse = EventObserver<BasePostOfferResponse.DataResult> { it ->
        makeSuccessSnack("Saqlandi...")
        findNavController().navigateUp()
    }

    private fun List<AllWorkersResponse.DataItem>.getPositionIdList(): List<Int> {
        val ids = ArrayList<Int>()
        for (a in this) {
            a.id?.let { ids.add(it) }
        }
        return ids
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOffersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = requireArguments().getInt("type")
        viewModel.getAllWorkers()
        loadView(type)
        loadAction()
        loadSharedObservers()
        loadObservers()
        loadFrom()
    }

    private fun loadAction() {
        binding.apply {
            ivFileAdd.setOnClickListener {
                Permissions.check(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    null,
                    object :
                        PermissionHandler() {
                        override fun onGranted() {
                            makeSuccessSnack("Permission Granted")
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "*/*"
                            resultLauncher.launch(intent)
                        }

                        override fun onDenied(
                            context: Context?,
                            deniedPermissions: ArrayList<String>?
                        ) {
                            makeErrorSnack("Permission Dined")
                        }
                    })
            }
            rbToCompany.setOnClickListener {
                if (!rbToCompany.isSelected) {
                    rbToCompany.isChecked = true
                    rbToCompany.isSelected = true
                    mainOverComplaint.hide()
                } else {
                    mainOverComplaint.show()
                    rbToCompany.isChecked = false
                    rbToCompany.isSelected = false
                }

            }
            btnSend.setOnClickListener {
                if (!binding.etContentComplaint.text.isNullOrEmpty()) {
                    if (type == 1) {
                        viewModel.postOffer(
                            "${binding.etContentComplaint.text}",
                            listFile,
                            viewModel.participants,
                            if (!rbToCompany.isChecked) viewModel.whoFrom?.id else null,
                            binding.rbToCompany.isChecked
                        )
                    } else {
                        viewModel.postComplaint(
                            "${binding.etContentComplaint.text}",
                            listFile,
                            viewModel.participants,
                            if (!rbToCompany.isChecked) viewModel.whoFrom?.id else null,
                            binding.rbToCompany.isChecked
                        )
                    }
                } else {
                    etContentComplaint.error = getString(R.string.enter_complaint_content)
                }
            }

            mainOverComplaint.setOnClickListener {
                localStorage.participant = PERFORMER
                if (sharedViewModel.performer.value == null) {
                    localStorage.persons = emptySet()
                } else {
                    localStorage.persons = setOf(sharedViewModel.performer.value!!.id.toString())
                }
                showSelectPersonFragment("Xodimni tanlang")
            }

            spectatorsAdd.setOnClickListener {
                localStorage.participant = PARTICIPANTS
                if (sharedViewModel.participants.value.isNullOrEmpty()) {
                    localStorage.persons = emptySet()
                } else {
                    localStorage.persons =
                        sharedViewModel.participants.value?.map { it.id.toString() }?.toSet()
                            ?: emptySet()
                }
                showSelectPersonFragment("Xodimlarni tanlang")
            }
//            mainOverComplaint.setOnClickListener {
//                localStorage.participant = PERFORMER
//                findNavController().navigate(R.id.action_createNoteFragment_to_performerSelectedFragment)
//            }
//
//            binding.spectatorsAdd.setOnClickListener {
//                localStorage.participant = PARTICIPANTS
//                findNavController().navigate(R.id.action_createNoteFragment_to_performerSelectedFragment)
//            }
        }
    }

    private fun loadView(type: Int) {
        binding.toolbar.setNavigationIcon(R.drawable.ic_clear)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        if (type == 1) {
            binding.apply {
                toolbar.title = getString(R.string.title_add_offer)
                tvContentComplaintName.text = getString(R.string.content_offers)
                etContentComplaint.hint = getString(R.string.content_offer_enter)
                btnSend.text = getString(R.string.send_offer)
                tvOffersDescriptionName.text = getString(R.string.whose_offer)
            }
        } else {
            binding.apply {
                toolbar.title = getString(R.string.title_add_complaint)
                tvContentComplaintName.text = getString(R.string.content_complaint)
                etContentComplaint.hint = getString(R.string.content_complaint_enter)
                btnSend.text = getString(R.string.send_complaint)
                tvOffersDescriptionName.text = getString(R.string.whose_complaint)
            }
        }
        adapter = ComplaintUserAdapter(requireContext(), this)
        binding.rvComplaintUser.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComplaintUser.adapter = adapter
        adapter!!.submitList(viewModel.spectators)
//        spinnerAdapter =
//            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, users)
//        popupMenu = ListPopupWindow(requireContext())
    }

    @SuppressLint("SetTextI18n")
    fun loadSharedObservers() {
        sharedViewModel.performer
            .observe(viewLifecycleOwner, { performer ->
                viewModel.whoFrom = performer
                loadFrom()
            })

        sharedViewModel.participants
            .observe(viewLifecycleOwner, { participantList ->
                if (!participantList.isNullOrEmpty()) {
                    viewModel.participants = participantList.map { it.id }.toMutableList()
                    viewModel.spectators = participantList
                    adapter!!.submitList(viewModel.spectators.toMutableList())
                }
            })
//        sharedViewModel.getObjectPerfomer()
//            .observe(viewLifecycleOwner, { performer ->
//                if (performer.firstName != null) {
//                    viewModel.whoFrom = performer
//                    loadFrom()
//                }
//            })
//
//        sharedViewModel.getAllParticipant().observe(viewLifecycleOwner, { participantList ->
//            if (participantList.isNotEmpty()) {
//                viewModel.participants = participantList.getPositionIdList() as ArrayList<Int>
//                viewModel.spectators.addAll(participantList)
//                adapter!!.submitList(viewModel.spectators)
//            }
//        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadFrom() {
        if (viewModel.whoFrom != null) {
            binding.ivComplaintUser.loadImageUrl(viewModel.whoFrom?.image.toString())
            binding.tvOverComplaint.text = viewModel.whoFrom!!.fullName
//                "${viewModel.whoFrom?.lastName} ${viewModel.whoFrom?.firstName}"
        } else {
            binding.ivComplaintUser.setImageResource(R.drawable.ic_user)
            binding.tvOverComplaint.text = "Hech kim tanlanmagan"
        }
        binding.fileCount.text = viewModel.filesCount.toString()
    }

//    private fun showList(view: View?, type: Int) {
//        popupMenu.setAdapter(
//            spinnerAdapter
//        )
//        popupMenu.anchorView = view
//        popupMenu.width = 700
//
//
//        popupMenu.setOnItemClickListener { _, _, position, _ ->
//            if (type == 1) {
//                userTo = userList[position].id
//                binding.tvOverComplaint.text =
//                    "${userList[position].lastName} ${userList[position].firstName}"
//                binding.ivComplaintUser.loadImageUrl("${userList[position].image}")
//            } else {
//                if (!usersId.contains(userList[position].id!!)) {
//                    usersId.add(userList[position].id!!)
//                    adapter!!.addItem(userList[position])
//                }
//            }
//
//            popupMenu.dismiss()
//        }
//        popupMenu.show()
//    }

    private fun loadObservers() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<PersonData?>(PERFORMER)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.performer.value = it
                }

            this?.getLiveData<List<PersonData>?>(PARTICIPANTS)
                ?.observe(viewLifecycleOwner) {
                    sharedViewModel.participants.value = it?.toMutableList()
                }
        }
        viewModel.postOffer.observe(viewLifecycleOwner, postOfferResponse)
        viewModel.postComplaint.observe(viewLifecycleOwner, postComplaintResponse)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkerData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.clear()
        _binding = null
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.fileCount.text =
                    (binding.fileCount.text.toString().toInt() + 1).toString()
                viewModel.filesCount++

                CoroutineScope(Dispatchers.IO).launch {
                    val data: Intent? = result.data
                    val uri = data?.data!!
                    val compressedImageFile = getCompressorFile(requireContext(), uri)
                    withContext(Dispatchers.Main) {
                        compressedImageFile?.let {
                            listFile.add(it)
                        }
                    }
                }
            }
        }

    private suspend fun getCompressorFile(context: Context, uri: Uri): File? {
        return try {
            val imageFile = FileHelper.fromFile(context, uri)
            Compressor.compress(
                context.applicationContext!!,
                imageFile.absoluteFile,
                Dispatchers.IO
            ) {
                quality(90)
            }
        } catch (e: Exception) {
            try {
                FileHelper.fromFile(requireContext(), uri)
            } catch (e: Exception) {
                null
            }
        }
    }


    override fun itemRemoveClick(dataItem: PersonData) {
        val newList = adapter?.currentList?.toMutableList()
        newList?.remove(dataItem)
        sharedViewModel.participants.value = newList
//        adapter!!.removeItem(dataItem)
    }
}