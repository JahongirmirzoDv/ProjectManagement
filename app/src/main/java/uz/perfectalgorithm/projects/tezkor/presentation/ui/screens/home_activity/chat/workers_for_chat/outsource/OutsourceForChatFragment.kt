package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.outsource

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentOutsourceForChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_.TeamWorkersForChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.WorkersForChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.outsource.OutsourceForChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class OutsourceForChatFragment : Fragment() {

    private var _binding: FragmentOutsourceForChatBinding? = null

    private val binding: FragmentOutsourceForChatBinding get() = _binding!!

    private val viewModel: OutsourceForChatViewModel by viewModels()

    private lateinit var adapter: TeamWorkersForChatAdapter

    private var checkedWorker: AllWorkersResponse.DataItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutsourceForChatBinding.inflate(layoutInflater)
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
        viewModel.outsourceWorkersLiveData.observe(viewLifecycleOwner, outsourceWorkersObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)

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
        viewModel.getOutsourceWorkers()
    }

    override fun onDestroy() {
        viewModel.disconnectSocket()
        super.onDestroy()
    }

    private fun navigateToConversationFragment(newChat: PersonalChatListResponse.PersonalChatDataItem) {
        newChat.receiver?.let { viewModel.setChatId(newChat.id!!, it) }
        myLogD("Chat id : ${newChat.id}", "LLL1")
        Toast.makeText(requireContext(), "Chat response", Toast.LENGTH_SHORT).show()
        findNavController().navigate(
            WorkersForChatFragmentDirections.actionWorkerListForChatFragmentToPersonalConversationFragment(
            )
        )
    }


    private val outsourceWorkersObserver =
        Observer<List<AllWorkersResponse.DataItem>> { teamWorkers ->
            if (teamWorkers.isNotEmpty()) {
                adapter.submitList(teamWorkers)
                binding.txtEmptyResult.gone()
            } else {
                binding.txtEmptyResult.visible()
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
}