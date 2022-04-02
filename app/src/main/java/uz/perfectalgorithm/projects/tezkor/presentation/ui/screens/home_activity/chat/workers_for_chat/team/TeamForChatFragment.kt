package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentTeamForChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_.TeamWorkersForChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.WorkersForChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.team_chat.TeamForChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class TeamForChatFragment : Fragment() {

    private var _binding: FragmentTeamForChatBinding? = null
    private val binding: FragmentTeamForChatBinding get() = _binding!!

    private val viewModel: TeamForChatViewModel by viewModels()

    private lateinit var adapter: TeamWorkersForChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamForChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObserver()
    }

    private fun loadViews() {
        binding.apply {
            rvTeam.layoutManager = LinearLayoutManager(requireContext())
            adapter = TeamWorkersForChatAdapter()
            adapter.setOnClickListener { workerData ->
                workerData.id?.let {
                    viewModel.createNewChatItem(it)
                    viewModel.isNewChatOpen = true
                }
            }
            rvTeam.adapter = adapter
        }
    }

    private fun loadObserver() {
        connectAndGetData()

        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModel.teamWorkersLiveData.observe(viewLifecycleOwner, teamWorkersObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
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

    private val teamWorkersObserver = Observer<List<AllWorkersResponse.DataItem>> { teamWorkers ->
        if (teamWorkers.isNotEmpty()) {
            adapter.submitList(teamWorkers)
            binding.txtEmptyResult.gone()
        } else {
            adapter.submitList(listOf())
            binding.txtEmptyResult.visible()
        }
    }

    private val createNewChatObserver = Observer<PersonalChatListResponse.PersonalChatDataItem> { newChat ->
        if (viewModel.isNewChatOpen) {
            navigateToConversationFragment(newChat)
            viewModel.isNewChatOpen = false
        }
    }

    private val messageObserver = Observer<MessageData> {

    }

    private fun connectAndGetData() {
        binding.progressBar.visible()
        viewModel.connectSocketAndComeResponses()
        viewModel.getTeamWorkers()
    }

    override fun onDestroy() {
        viewModel.disconnectSocket()
        super.onDestroy()
    }

    private fun navigateToConversationFragment(newChat: PersonalChatListResponse.PersonalChatDataItem) {
        newChat.receiver?.let { viewModel.setChatId(newChat.id!!, it) }
        myLogD("Chat id : ${newChat.id}", "LLL1")
        findNavController().navigate(
            WorkersForChatFragmentDirections.actionWorkerListForChatFragmentToPersonalConversationFragment(
            )
        )
    }


}