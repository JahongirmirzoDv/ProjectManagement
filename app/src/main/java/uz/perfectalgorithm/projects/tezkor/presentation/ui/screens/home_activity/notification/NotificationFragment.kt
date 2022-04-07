package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentNotificationBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.notification.NotificationAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.notification.NotificationItemClickListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base.BaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.notification.NotificationViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.EVENT_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject


/***
 * Bu notification fragment tepadagi qo'ng'roq  bosilganda chiqadi
 * bu yerdagi ma'lumot pagination orqali olib pageAdapterga beriladi
 */

@AndroidEntryPoint
class NotificationFragment : BaseFragment(), NotificationItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentNotificationBinding? = null
    private val binding: FragmentNotificationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var pageAdapter: NotificationAdapter


    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        viewModel.getNotification()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        loadView()
        loadObservers()
    }

    private fun loadView() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        pageAdapter = NotificationAdapter(requireContext(), this)

        binding.rvNotification.adapter = pageAdapter
        binding.rvNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.swipeLayout.setOnRefreshListener(this)

//        binding.rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        })
    }

    private fun loadObservers() {
        viewModel.notificationLiveData.observe(viewLifecycleOwner, getData)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    private val getData =
        EventObserver<PagingData<NotificationResponse.NotificationData>> { data ->
            lifecycleScope.launch {
                pageAdapter.submitData(data)
                binding.swipeLayout.isRefreshing = false
                if (pageAdapter.itemCount < 1) {
                    binding.apply {
                        rvNotification.hide()
                        tvEmptyList.show()
                    }
                } else {
                    binding.apply {
                        rvNotification.show()
                        tvEmptyList.hide()
                    }
                }
            }

            binding.shimmerLayout.stopShimmerAnimation()
            binding.shimmerLayout.gone()
            binding.rvNotification.visible()
        }


    private val getProgressData = EventObserver<Boolean> { it ->
        if (it) {
            showLoadingDialog()
        } else {
            loadingDialog?.dismiss()
            loadingAskDialog?.dismiss()
        }
    }

    private val getError = EventObserver<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
        binding.progressBar.hide()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun itemClick(event: NotificationResponse.NotificationData?) {
        when (event?.type) {
            "note" -> {
                findNavController().navigate(
                    R.id.action_to_navigation_detail_note,
                    bundleOf(EVENT_ID to event.id)
                )
            }
            "task" -> {
                event.id.let {
                    findNavController().navigate(
                        NotificationFragmentDirections.toTaskDetailsFragment(
                            it
                        )
                    )
                }
            }
            "dating" -> {
                event.id.let {
                    findNavController().navigate(
                        NotificationFragmentDirections.toDatingDetailsFragment(
                            it
                        )
                    )
                }
            }
            "meeting" -> {
                event.id.let {
                    findNavController().navigate(
                        NotificationFragmentDirections.toMeetingDetailsFragment(
                            it
                        )
                    )
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.shimmerLayout.startShimmerAnimation()
    }

    override fun onPause() {
        binding.shimmerLayout.stopShimmerAnimation()
        super.onPause()
    }

    override fun onRefresh() {
        viewModel.getNotification()
    }
}