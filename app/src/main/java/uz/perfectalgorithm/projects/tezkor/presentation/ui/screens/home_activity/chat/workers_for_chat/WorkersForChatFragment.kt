package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.workers_for_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
//import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.common.ChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWorkersForChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_.WorkerListForChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.page_adapter.chat.WorkersForChatPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.WorkersForChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.mardex.model.results.local.MessageData

@AndroidEntryPoint
class WorkersForChatFragment : Fragment() {
    /**
    * Ushbu bo'lim Yangi shaxsiy chat yaratish uchun hodimlarni 4 tartibda tanlash imkoniyati yaratilgan
    * */

    private var _binding: FragmentWorkersForChatBinding? = null
    private val binding: FragmentWorkersForChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private lateinit var viewPagerAdapter: WorkersForChatPagerAdapter

    private lateinit var searchWorkersAdapter: WorkerListForChatAdapter

    private val viewModel: WorkersForChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkersForChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
        initTabLayout()
    }

    private fun loadViews() {
        searchWorkersAdapter = WorkerListForChatAdapter()
        searchWorkersAdapter.submitList(listOf())
        binding.rvSearchWorkers.adapter = searchWorkersAdapter

        searchWorkersAdapter.setOnClickListener { workerData ->
            workerData.id?.let {
                viewModel.createNewChatItem(it)
                viewModel.isNewChatOpen = true
                hideKeyboard()
            }
        }

        binding.apply {
            btnSearch.setOnClickListener {
                openSearchbar()
            }
            btnCancelSearch.setOnClickListener {
                closeSearchBar()
            }
        }

        binding.etSearch.addTextChangedListener {
            searchWorkersAdapter.submitList(
                searchFilterRECYCLER(
                    it.toString(),
                    viewModel.mWorkerList
                )
            )
        }
    }

    private fun initTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        viewPagerAdapter = WorkersForChatPagerAdapter(childFragmentManager, 0, requireContext())
        binding.viewPager.adapter = viewPagerAdapter
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            binding.tabLayout.getTabAt(0)?.text = this.requireActivity().getString(R.string.command)
            binding.tabLayout.getTabAt(1)?.text = this.requireActivity().getString(R.string.list)
            binding.tabLayout.getTabAt(2)?.text =
                this.requireActivity().getString(R.string.structure)
            binding.tabLayout.getTabAt(3)?.text =
                this.requireActivity().getString(R.string.outsource)
        }
    }

    private fun loadObservers() {
        connectAndGetData()

        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.createNewChatLiveData.observe(viewLifecycleOwner, createNewChatObserver)
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val allWorkersObserver = Observer<List<AllWorkersResponse.DataItem>> { listWorkers ->

        var mList: List<AllWorkersResponse.DataItem> = listOf()
        if (listWorkers.isNotEmpty()) {
            mList = listWorkers.sortedBy {
                it.firstName!!.trim().toLowerCase()
            }
            binding.txtEmptyResult.gone()
        } else {

        }

        viewModel.mWorkerList = mList
        searchWorkersAdapter.submitList(mList)

    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private fun closeSearchBar() {
        binding.apply {
            etSearch.setText("")
            hideKeyboard()
            tabRelativeLl.visible()
            btnSearch.visible()
            viewPager.visible()
            llSearchBar.gone()
            rvSearchWorkers.gone()
        }
    }

    private fun openSearchbar() {
        binding.apply {
            tabRelativeLl.inVisible()
            btnSearch.gone()
            viewPager.gone()
            llSearchBar.visible()
            rvSearchWorkers.visible()
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
        viewModel.connectSocketAndComeResponses()
        viewModel.getAllWorkers()
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