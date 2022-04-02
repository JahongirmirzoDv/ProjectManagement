package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.create_new_group.create

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.GenderEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.add_members.AddedMembersForCreateGroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.add_members.WorkerListForCreateGroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.DeleteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.create_new_group.CreateGroupChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompletedWithoutScroll
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData
import java.io.File

@AndroidEntryPoint
class CreateGroupChatFragment : Fragment() {
    private var _binding: FragmentCreateGroupChatBinding? = null
    private val binding: FragmentCreateGroupChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: CreateGroupChatFragmentArgs by navArgs()
    lateinit var members: ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>
    lateinit var adapter: AddedMembersForCreateGroupChatAdapter

    private val viewModel: CreateGroupChatViewModel by viewModels()
    private var booleanImage = false
    private var myFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()
        members =
            args.members.workersShortData as ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.connectSocketAndComeResponses()
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModel.imageLiveData.observe(viewLifecycleOwner, _imageLiveData)
    }

    private val _imageLiveData = Observer<String> {
        if (it.isNotEmpty()) {
            findNavController().navigate(CreateGroupChatFragmentDirections.actionCreateGroupToGroupConversationFragment())
        }
    }

    private val createNewChatObserver =
        Observer<ChatGroupListResponse.GroupChatDataItem> { newGroupChat ->
            if (booleanImage) {
                newGroupChat.id?.let { id ->
                    myFile?.let { imageFile ->
                        viewModel.uploadFile(id = id, file = imageFile)
                    }
                }
            } else {
                findNavController().navigate(CreateGroupChatFragmentDirections.actionCreateGroupToGroupConversationFragment())
            }
            viewModel.setChatId(newGroupChat)
        }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val messageObserver = Observer<MessageData> {

    }

    private fun loadViews() {
        binding.apply {

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            adapter = AddedMembersForCreateGroupChatAdapter()
            rvMembers.layoutManager = LinearLayoutManager(requireContext())
            rvMembers.adapter = adapter
            adapter.submitList(members)
            txtCount.text = members.size.toString()

            adapter.setOnDeleteListener {
                members.remove(it)
                adapter.notifyDataSetChanged()
                txtCount.text = members.size.toString()
            }

            btnCreate.setOnClickListener {
                createGroupChat()
            }

            btnChooseImage.setOnClickListener {
                selectImage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSocket()
    }

    private fun createGroupChat() {
        binding.apply {
            val isInputCompleted = isInputCompletedWithoutScroll(
                listOf(
                    Triple(
                        etTitle.text.isNullOrBlank(),
                        etTitle,
                        getString(R.string.error_group_title)
                    )
                )
            )
            if (isInputCompleted) {
                val memberIds = ArrayList<Int>()
                members.forEach { members ->
                    members.id?.let { it1 -> memberIds.add(it1) }
                }
                viewModel.createGroupChat(
                    title = binding.etTitle.text.toString(),
                    members = memberIds
                )
            }
        }
    }

    private fun selectImage() {

        booleanImage = false
        binding.imgGroup.setImageResource(R.drawable.ic_camera_f_gr)
        myFile = null
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
                        binding.imgGroup.setImageURI(data?.data!!)
                        booleanImage = true
                    }
                }
            }
    }


}