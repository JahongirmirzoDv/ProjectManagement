package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.group.group_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentGroupChatDetailBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.group.GroupChatMembersAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.AddMembersToGroupChatDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.WorkersForChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.group.group_chat_detail.GroupChatDetailViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.WorkersForChatViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.worker_list_chat.WorkerListForChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.showToast
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class GroupChatDetailFragment : Fragment() {

    /**
     * Obshiy(detail page oynasi: Bu yerga Guruhga oid message list oynasidan o'tiladi),
     * huddi telegram kabi
     * **/

    private var _binding: FragmentGroupChatDetailBinding? = null
    private val binding: FragmentGroupChatDetailBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: GroupChatDetailViewModel by viewModels()
    lateinit var adapter: GroupChatMembersAdapter
    private val viewModelChat: WorkerListForChatViewModel by viewModels()
    private var groupDetail: GroupChatDetailResponse.ResponseData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupChatDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        binding.apply {
            btnBack.setOnClickListener { findNavController().navigateUp() }
            btnAdd.gone()

            adapter = GroupChatMembersAdapter()
            rvMembers.layoutManager = LinearLayoutManager(requireContext())
            rvMembers.adapter = adapter

            adapter.setOnClickListener {
//                findNavController().popBackStack(R.id.navigation_chat,false)
                viewModelChat.createNewChatItem(it.userId?:0)
                viewModelChat.isNewChatOpen = true
            }

            btnAdd.setOnClickListener {
                val dialog = AddMembersToGroupChatDialog(
                    requireActivity(),
                    viewModel.getWorkersWithoutThisGroupChat()
                )
                dialog.addMembersListener { newMembers ->
                    showToast("New Members :${newMembers}")
                    viewModel.addMembers(newMembers)
                }
                dialog.show()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("title", groupDetail?.title?:"")
                bundle.putString("image", groupDetail?.image?:"")
                findNavController().navigate(R.id.groupDetailEditFragment, bundle)
            }
        }
    }

    private fun loadObservers() {
        viewModelChat.connectSocketAndComeResponses()
        viewModel.connectSocketAndComeResponses()
        viewModel.getDetailData()
        if (viewModel.mMembers.isEmpty()) {
            viewModelChat.getAllWorkers()
        }else {
            adapter.submitList(viewModel.mMembers)
        }
        viewModel.getAllWorkers()
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.detailLiveData.observe(viewLifecycleOwner, detailObserver)
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver)

        viewModelChat.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModelChat.message.observe(viewLifecycleOwner, messageObserver)
        viewModelChat.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver1)
        viewModelChat.progressLiveData.observe(viewLifecycleOwner, progressObserver1)
        viewModelChat.errorLiveData.observe(viewLifecycleOwner, errorObserver1)
    }

    private val allWorkersObserver1 = Observer<List<AllWorkersResponse.DataItem>> { listWorkers ->

        var mList: List<AllWorkersResponse.DataItem> = listOf()

        if (listWorkers.isNotEmpty()) {


            mList = listWorkers.sortedBy {
                it.firstName!!.trim().toLowerCase()
            }
//            wLAdapter.submitList(mList)
//            binding.txtEmptyResult.gone()
        } else {
//            binding.txtTitleSaved.gone()
//            binding.txtEmptyResult.visible()
        }
    }

    private val progressObserver1 = Observer<Boolean> {
        if (it) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val messageObserver = Observer<MessageData> {

    }

    private val errorObserver1 = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val createNewChatObserver =
        Observer<PersonalChatListResponse.PersonalChatDataItem> { newChat ->
            if (viewModelChat.isNewChatOpen) {
                navigateToConversationFragment(newChat)

                viewModelChat.isNewChatOpen = false
            }
        }

    private fun navigateToConversationFragment(newChat: PersonalChatListResponse.PersonalChatDataItem) {
        newChat.receiver?.let { viewModelChat.setChatId(newChat.id!!, it) }
        myLogD("Chat id : ${newChat.id}", "LLL1")
        findNavController().navigate(
            GroupChatDetailFragmentDirections.actionGroupChatDetailForChatFragmentToPersonalConversationFragment()
//            WorkersForChatFragmentDirections.actionWorkerListForChatFragmentToPersonalConversationFragment()
        )
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val detailObserver = Observer<GroupChatDetailResponse.ResponseData> {
        binding.txtGroupTitle.text = it.title
        groupDetail = it
        it.image?.let { it1 -> binding.imgGroup.loadImageUrlUniversal(it1, R.drawable.ic_users) }
        binding.txtSubscribersCount.text =
            "${it.members?.size} ${getString(R.string._count_members)}"
        viewModel.submitMembers(it.members)
        adapter.submitList(viewModel.mMembers)
    }

    private val allWorkersObserver =
        Observer<List<AllWorkersShortDataResponse.WorkerShortDataItem>> {
            viewModel.submitAllWorkers(it)
            binding.btnAdd.visible()
        }


    override fun onDestroy() {
        viewModelChat.disconnectSocket()
        super.onDestroy()
    }
}