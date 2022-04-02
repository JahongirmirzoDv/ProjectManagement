package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.worker_list

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
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWorkerListForChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_.WorkerListForChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.WorkersForChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat.WorkersForChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.worker_list_chat.WorkerListForChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class WorkerListForChatFragment : Fragment() {

    private var _binding: FragmentWorkerListForChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkerListForChatViewModel by viewModels()

    private lateinit var wLAdapter: WorkerListForChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkerListForChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        wLAdapter = WorkerListForChatAdapter()
        wLAdapter.submitList(listOf())
        binding.rvAllWorkers.layoutManager = LinearLayoutManager(requireContext())
        wLAdapter.setOnClickListener { workerData ->
            workerData.id?.let {
                Toast.makeText(requireContext(), "onclick ${it}", Toast.LENGTH_SHORT).show()
                viewModel.createNewChatItem(it)
                viewModel.isNewChatOpen = true
            }
        }
        binding.rvAllWorkers.adapter = wLAdapter
    }

    private fun loadObservers() {

        connectAndGetData()

        viewModel.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val allWorkersObserver = Observer<List<AllWorkersResponse.DataItem>> { listWorkers ->

        var mList: List<AllWorkersResponse.DataItem> = listOf()

        if (listWorkers.isNotEmpty()) {


            mList = listWorkers.sortedBy {
                it.firstName!!.trim().toLowerCase()
            }
            wLAdapter.submitList(mList)
            binding.txtEmptyResult.gone()
        } else {
            binding.txtTitleSaved.gone()
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

    private val createNewChatObserver =
        Observer<PersonalChatListResponse.PersonalChatDataItem> { newChat ->
            if (viewModel.isNewChatOpen) {
                navigateToConversationFragment(newChat)


                viewModel.isNewChatOpen = false
            }
        }

    private val messageObserver = Observer<MessageData> {

    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private fun navigateToConversationFragment(newChat: PersonalChatListResponse.PersonalChatDataItem) {
        newChat.receiver?.let { viewModel.setChatId(newChat.id!!, it) }
        myLogD("Chat id : ${newChat.id}", "LLL1")
        Toast.makeText(requireContext(), "Chat response", Toast.LENGTH_SHORT).show()
        findNavController().navigate(
            WorkersForChatFragmentDirections.actionWorkerListForChatFragmentToPersonalConversationFragment()
        )
    }


    private fun connectAndGetData() {
        binding.progressBar.visible()
        viewModel.connectSocketAndComeResponses()
        viewModel.getAllWorkers()
    }

    override fun onDestroy() {
        viewModel.disconnectSocket()
        super.onDestroy()
    }
}