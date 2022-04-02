package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.company_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompanyGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.group.GroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.DeleteChatDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.company_group.CompanyGroupChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class CompanyGroupChatFragment : Fragment() {

    /**Company katta Guruh chatlar oynasi:
     *umumiy holatda tayyor,
     * **/

    companion object {
        const val ONLY_ADMIN = "ONLY_ADMIN"
        fun getInstance(onlyWriteAdmin: Boolean = false): CompanyGroupChatFragment {
            return CompanyGroupChatFragment().apply {
                arguments = bundleOf(ONLY_ADMIN to onlyWriteAdmin)
            }
        }
    }

    private var _binding: FragmentCompanyGroupChatBinding? = null
    private val binding: FragmentCompanyGroupChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: CompanyGroupChatViewModel by viewModels()

    private lateinit var adapter: GroupChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCompanyGroupChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            viewModel.isOnlyWriteAdmin = it.getBoolean(ONLY_ADMIN)
        }
        loadViews()
        loadObservers()
    }

    fun loadViews() {
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
        binding.btnCreate.gone()
    }

    private fun loadObservers() {
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.chatListLiveData.observe(viewLifecycleOwner, chatListObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.sendMessageLiveData.observe(viewLifecycleOwner, sendMessageObserver)
        viewModel.deleteChatLiveData.observe(viewLifecycleOwner, deleteChatObserver)

        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.notConnectionLiveData.observe(viewLifecycleOwner, notConnectionObserver)
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
        if (it?:false) {
            binding.progressBar.progressLoader.visible()
        } else {
            binding.progressBar.progressLoader.gone()
        }
    }

    private val messageObserver = Observer<MessageData> {

    }

    private val chatListObserver =
        Observer<List<ChatGroupListResponse.GroupChatDataItem>> { chatList ->
            binding.progressBar.progressLoader.gone()
            if (chatList.isEmpty()) {
                binding.txtEmptyResult.visible()
            } else {
                viewModel.mChatList.clear()
                viewModel.mChatList.addAll(chatList.filter { it.isChannel==viewModel.isOnlyWriteAdmin })
                binding.txtEmptyResult.gone()
                adapter.submitList(viewModel.mChatList)
            }

            /**
             * Chatlar listini qabul qilish comentga olib qoyilgan, sababi : Backend dan malumotlarni
             * universal holatga keltirish kerak(Response boshqa chatlar kabi kelishini
             * backend taminlashi kerak)
             * **/


        }

    private val errorObserver = Observer<Throwable> { throwable ->
        binding.progressBar.progressLoader.gone()
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
//        binding.progressBar.progressLoader.visible()
        viewModel.getChatList()
        viewModel.connectAndComeResponses()
    }

}