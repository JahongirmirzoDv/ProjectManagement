package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentProjectChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.group.GroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.project.ProjectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal.DeleteChatDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.project.ProjectGroupChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.PROJECT_PAGE
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class ProjectChatFragment : Fragment() {

    /**
     * Yaratilgan projectlar uchun ochilgan guruh chatlar listi
     *
     * */
    private var _binding: FragmentProjectChatBinding? = null
    private val binding: FragmentProjectChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: ProjectGroupChatViewModel by viewModels()

    private lateinit var projectAdapter: GroupChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getChatList()
    }

    private fun loadViews() {
        projectAdapter = GroupChatAdapter(viewModel.getUserId())
        projectAdapter.setOnclickListener {groupChatData->
            groupChatData.id?.let {
                viewModel.setChatId(groupChatData)
                findNavController().navigate(NavigationChatFragmentDirections.actionNavigationChatToGroupConversationFragment())
            }
        }
        projectAdapter.setOnDeleteClickListener {
            val deleteDialog = DeleteChatDialog(requireActivity())
            deleteDialog.deleteClickListener {
                it.id?.let { it1 -> viewModel.deleteChat(it1) }
            }
            deleteDialog.show()
        }

//        projectAdapter.setOnClickListener { project ->
//            project.id?.let {
//                findNavController().navigate(
//                    NavigationChatFragmentDirections.toProjectDetailUpdateFragment(it)
//                )
//            }
//        }
        binding.recyclerProject.adapter = projectAdapter
    }

    private fun loadObservers() {
        viewModel.getChatList()
        viewModel.chatListLiveData.observe(viewLifecycleOwner, chatListObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
    }

    private val chatListObserver =
        Observer<List<ChatGroupListResponse.GroupChatDataItem>> { chatList ->
            if (chatList.isEmpty()) {
                 binding.txtEmptyResult.visible()
             } else {
                 viewModel.mChatList.clear()
                 viewModel.mChatList.addAll(chatList)
                 binding.txtEmptyResult.gone()
                 projectAdapter.submitList(viewModel.mChatList)
             }
        }


    private val errorObserver = Observer<Throwable> {
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}