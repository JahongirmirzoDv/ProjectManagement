package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.create_new_group.workers

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.chat.MembersToGroupChatData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateGroupChatAddWorkerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.add_members.WorkerListForCreateGroupChatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.create_new_group.AddWorkersToGroupChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

@AndroidEntryPoint
class AddWorkersToGroupChatFragment : Fragment() {

    private var _binding: FragmentCreateGroupChatAddWorkerBinding? = null
    private val binding: FragmentCreateGroupChatAddWorkerBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    lateinit var adapter: WorkerListForCreateGroupChatAdapter

    private lateinit var myList: ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>

    private val viewModel: AddWorkersToGroupChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupChatAddWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myList = ArrayList()
        hideAppBar()
        hideBottomMenu()
        loadObservers()
        loadViews()
    }

    private fun loadObservers() {

        viewModel.getTeamWorkers()

        viewModel.teamWorkersLiveData.observe(viewLifecycleOwner, teamWorkersObserver)
        viewModel.allWorkersLiveData.observe(viewLifecycleOwner, allWorkersObserver)
        viewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val teamWorkersObserver =
        Observer<List<AllWorkersShortDataResponse.WorkerShortDataItem>> { teamWorkers ->

            if (teamWorkers.isNotEmpty()) {
                var mTeaWorkers =
                    teamWorkers as ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>

                var i = 0
                teamWorkers.forEach { ow ->
                    myList.forEach { mw ->
                        if (ow.id == mw.id) mTeaWorkers[i].isChecked = true
                    }
                    i++
                }
                adapter.submitList(mTeaWorkers)
            } else {
            }
        }

    private val allWorkersObserver =
        Observer<List<AllWorkersShortDataResponse.WorkerShortDataItem>> { allWorkers ->

            if (allWorkers.isNotEmpty()) {
                var mAllWorkers =
                    allWorkers as ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>

                var i = 0
                allWorkers.forEach { ow ->
                    myList.forEach { mw ->
                        if (ow.id == mw.id) mAllWorkers[i].isChecked = true
                    }
                    i++
                }
                adapter.submitList(mAllWorkers)
            } else {
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadViews() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            adapter = WorkerListForCreateGroupChatAdapter()
            rvTeamWorkers.layoutManager = LinearLayoutManager(requireContext())
            rvTeamWorkers.adapter = adapter
            adapter.setOnCheckListener {
                checkData(it)
            }

            binding.imgNext.disableNext()

            imgNext.setOnClickListener {
                navigateToCreateGroupFragment()
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            viewModel.getTeamWorkers()
                        }
                        1 -> {
                            viewModel.getAllWorkers()
                        }
                        2 -> {
                            // TODO: 10/7/2021
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkData(worker: AllWorkersShortDataResponse.WorkerShortDataItem?) {
        var position = -1

        var i = 0
        myList.forEach { mWorker ->
            if (worker?.id == mWorker.id) {
                position = i
            }
            i++
        }

        if (position == -1) {
            worker?.let { myList.add(it) }
        } else {
            myList.removeAt(position)
        }

        if (myList.size > 0) {
            binding.imgNext.enableNext()
        } else {
            binding.imgNext.disableNext()
        }

    }


    private fun navigateToCreateGroupFragment() {

        findNavController().navigate(
            AddWorkersToGroupChatFragmentDirections.toCreateGroupChatFragment(
                MembersToGroupChatData(myList)
            )
        )
    }

}