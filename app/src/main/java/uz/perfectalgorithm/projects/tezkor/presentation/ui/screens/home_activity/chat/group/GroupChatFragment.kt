package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.group

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.group.GroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.DeleteChatDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.group.GroupChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleCustomException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class GroupChatFragment : Fragment() {

    /**Guruh chatList oynasi:
     * tayyor:Chatlar listi, o'chirish ,yangi chat yaratishga o'tish, kelgan malumotni birdan korsatish
     * **/
    private var _binding: FragmentGroupChatBinding? = null
    private val binding: FragmentGroupChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: GroupChatViewModel by viewModels()

    private lateinit var adapter: GroupChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGroupChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    fun loadViews() {
        viewModel.getChatList()
        adapter = GroupChatAdapter(viewModel.getUserId())
        adapter.submitList(listOf())
        binding.rvGroupChatList.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnclickListener { groupChatData ->
            groupChatData.id?.let {
                viewModel.setChatId(groupChatData)
                findNavController().navigate(NavigationChatFragmentDirections.actionNavigationChatToGroupConversationFragment())
            }
        }

        adapter.setOnDeleteClickListener {
            val deleteDialog = DeleteChatDialog(requireActivity())
            deleteDialog.deleteClickListener {
                it.id?.let { it1 -> viewModel.deleteChat(it1) }
            }
            deleteDialog.show()
        }

        binding.rvGroupChatList.adapter = adapter

        binding.btnCreate.setOnClickListener {
            findNavController().navigate(NavigationChatFragmentDirections.actionNavigationChatToAddWorkersToGroup())
        }

    }

    private fun loadObservers() {
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.chatListLiveData.observe(viewLifecycleOwner, chatListObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.sendMessageLiveData.observe(viewLifecycleOwner, sendMessageObserver)
        viewModel.deleteChatLiveData.observe(viewLifecycleOwner, deleteChatObserver)

//        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
//        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)
    }

    private val sendMessageObserver =
        Observer<GroupChatSendMessageResponse.MessageDataItem> { sendMessage ->
            if (sendMessage != null) {
                viewModel.getChatList()
            }
        }

    private val deleteChatObserver = Observer<Int> { deletedChatId ->
        var delChatItem = ChatGroupListResponse.GroupChatDataItem()

        if (deletedChatId > 0) {
            viewModel.mChatList.forEach lit@{ a ->
                if (a.id == deletedChatId) {
                    delChatItem = a
                    return@lit
                }
            }
            viewModel.mChatList.remove(delChatItem)
            adapter.submitList(viewModel.mChatList)
        }
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

    private val chatListObserver =
        Observer<List<ChatGroupListResponse.GroupChatDataItem>> { chatList ->
            if (chatList.isEmpty()) {
                binding.txtEmptyResult.visible()
            } else {
                viewModel.mChatList.clear()
                viewModel.mChatList.addAll(chatList)
                binding.txtEmptyResult.gone()
                adapter.submitList(chatList)
            }
        }

    private val errorObserver = Observer<Throwable> { throwable ->
        if (throwable is Exception) {
            handleException(throwable)

        } else {
            makeErrorSnack(throwable.message.toString())
        }
    }

    private val notConnectionObserver = Observer<Unit> {
        makeErrorSnack()
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        connectSocket()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSocket()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun connectSocket() {
        binding.progressBar.progressLoader.visible()
        viewModel.getChatList()
        viewModel.connectAndComeResponses()
    }

}