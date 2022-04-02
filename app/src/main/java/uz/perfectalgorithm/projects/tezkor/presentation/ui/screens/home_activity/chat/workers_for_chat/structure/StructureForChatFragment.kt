package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.structure

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentStructureForChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_.structure_.StructureSectionForChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.WorkersForChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.structure.StructureForChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class StructureForChatFragment : Fragment() {

    private var _binding: FragmentStructureForChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StructureForChatViewModel by viewModels()
    private lateinit var adapter: StructureSectionForChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStructureForChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        adapter = StructureSectionForChatAdapter()
        binding.rvSections.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSections.adapter = adapter
        adapter.setOnClickListener { workerData ->
            workerData.id?.let {
                viewModel.createNewChatItem(it)
                viewModel.isNewChatOpen = true
            }
        }
    }

    private fun loadObservers() {

        connectAndGetData()

        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModel.structureLiveData.observe(viewLifecycleOwner, structureObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val structureObserver = Observer<List<StructureResponse.DataItem>> { structureData ->
        if (structureData != null) {
            if (structureData.isNotEmpty()) {
                binding.rvSections.adapter = adapter
                adapter.submitList(structureData)
                adapter.notifyDataSetChanged()
            } else {
                binding.txtEmptyResult.visible()
            }
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

    private val createNewChatObserver =
        Observer<PersonalChatListResponse.PersonalChatDataItem> { newChat ->
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
        viewModel.getStructureData()
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