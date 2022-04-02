package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.manage_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user.ManageUsers
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentManageUserBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.manage_users.ManageUsersAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.manage_user.ManageUserViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver

@AndroidEntryPoint
class ManageUserFragment : Fragment() {

    private var _binding: FragmentManageUserBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(
            resources.getString(
                R.string.null_binding
            )
        )
    var adapter: ManageUsersAdapter? = null

    private val viewModel: ManageUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.manageUsers.observe(viewLifecycleOwner, getManageUsers)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    private val getProgressData = EventObserver<Boolean> { it ->
        if (it) {
            binding.apply {
                progressBar.show()
            }
        } else {
            binding.apply {
                progressBar.gone()
            }
        }
    }

    private val getError = EventObserver<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }
    private val getManageUsers = EventObserver<List<ManageUsers>> {
        adapter!!.submitList(it)
    }

    private fun loadView() {
        adapter = ManageUsersAdapter()
        binding.rvTeam.adapter = adapter
        binding.rvTeam.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getManageUsers()
    }


}