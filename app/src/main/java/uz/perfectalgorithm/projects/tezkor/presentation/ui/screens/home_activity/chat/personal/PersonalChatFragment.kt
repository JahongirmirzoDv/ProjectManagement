package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPersonalChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.personal.PersonalChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.DeleteChatDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.personal.PersonalChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class PersonalChatFragment : Fragment() {
    /**Shaxsiy chatlar oynasi, tayyor: chatlar listi, chatni o'chirish,
     *  button orqali biror hodim bilan yangi chat yaratish
     *
     *  **/

    private var _binding: FragmentPersonalChatBinding? = null
    private val binding: FragmentPersonalChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: PersonalChatViewModel by viewModels()

    private lateinit var adapter: PersonalChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        hideKeyboard()
        adapter = PersonalChatAdapter(viewModel.getUserId())

        adapter.setOnClickListener {
            navigateToConversationFragment(it)
        }

        adapter.setOnDeleteClickListener {
            val deleteDialog = DeleteChatDialog(requireActivity())
            deleteDialog.deleteClickListener {
                it.id?.let { it1 -> viewModel.deleteChat(it1) }
            }
            deleteDialog.show()
        }

        binding.rvPersonalChats.adapter = adapter

        binding.btnAddChat.setOnClickListener {
            openWorkerList()
        }
    }

    private fun openWorkerList() {
        findNavController().navigate(NavigationChatFragmentDirections.actionNavigationChatToWorkerListForChatFragment())
    }

    private fun loadObservers() {

        viewModel.chatListLiveData.observe(viewLifecycleOwner, chatListObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModel.deleteChatLiveData.observe(viewLifecycleOwner, deleteChatObserver)
        viewModel.sendMessageLiveData.observe(viewLifecycleOwner, sendMessageObserver)
//        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
//        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)

    }

    private val createNewChatObserver = Observer<PersonalChatListResponse.PersonalChatDataItem> {
        viewModel.getChatList()
    }

    private val messageObserver = Observer<MessageData> {

    }

    private val sendMessageObserver =
        Observer<PersonalChatMessageListResponse.MessageDataItem> { sendMessage ->
            if (sendMessage != null) {
                viewModel.getChatList()
            }
        }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val chatListObserver =
        Observer<List<PersonalChatListResponse.PersonalChatDataItem>> { chatList ->
            if (chatList.isEmpty()) {
                binding.txtEmptyResult.visible()
            } else {
                viewModel.mChatList.clear()
                viewModel.mChatList.addAll(chatList)
                binding.txtEmptyResult.gone()
                adapter.submitList(viewModel.mChatList)
            }
        }

    private val notConnectionObserver = Observer<Unit> {
        makeErrorSnack()
    }

    private val deleteChatObserver = Observer<Int> { deletedChatId ->
        var delChatItem = PersonalChatListResponse.PersonalChatDataItem()

        if (deletedChatId > 0) {
            viewModel.mChatList.forEach lit@{ a ->
                if (a.id == deletedChatId) {
                    delChatItem = a
                    return@lit
                }
            }
            viewModel.mChatList.remove(delChatItem)
            adapter.submitList(viewModel.mChatList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun navigateToConversationFragment(newChat: PersonalChatListResponse.PersonalChatDataItem) {

        newChat.receiver?.let { viewModel.setChatId(newChat.id!!, it) }
        myLogD("Chat id : ${newChat.id}", "LLL1")

        findNavController().navigate(
            NavigationChatFragmentDirections.actionNavigationChatToPersonalConversationFragment(
            )
        )
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
        viewModel.disconnectSocket()
        _binding = null
    }

    private fun connectSocket() {
        binding.progressBar.progressLoader.visible()
        viewModel.connectAndComeResponses()
        viewModel.getChatList()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}