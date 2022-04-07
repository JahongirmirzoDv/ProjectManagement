package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.offers

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOfferResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentOffersBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.others.offers.OffersDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base.BaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.offers.OffersViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.visible

/***
 * bu others qismi uchun taklif va shikoyatlar list uchun ui
 */

@AndroidEntryPoint
class OffersFragment : BaseFragment(), OfferItemClickListener {

    private var _binding: FragmentOffersBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: OffersViewModel by viewModels()
    private var adapter: OffersAdapter? = null

    private val allData = mutableListOf<BaseOfferResponse.DataResult>()

    private val getAllOffersAndComplaints =
        EventObserver<List<BaseOfferResponse.DataResult>> { it ->
            if (it.isEmpty()) {
                binding.mainListContainer.hide()
                binding.mainNoListContainer.show()
            } else {
                binding.mainNoListContainer.hide()
                binding.mainListContainer.show()
                allData.clear()
                allData.addAll(it.reversed())
                adapter!!.submitList(allData)
            }
        }
    private val getProgressData = EventObserver<Boolean> {
        val loader = binding.progressLayout.progressLoader

        if (it) {
//            showLoadingDialog()
            loader.show()
        } else {
            loader.gone()
//            loadingDialog?.dismiss()
//            loadingAskDialog?.dismiss()
        }
    }

    private val getError = EventObserver<Throwable> {
        loadingDialog?.dismiss()
        loadingAskDialog?.dismiss()
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOffersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAppBar()
        hideBottomMenu()
        loadObservers()
        loadView()
        loadAction()
    }

    private fun loadAction() {

        binding.toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { it ->
            val dataList = mutableListOf<BaseOfferResponse.DataResult>()
            when (it.itemId) {
                R.id.get_section -> {
                    OffersDialog.showOffersSectionsBottomSheetDialog(requireContext(), {
                        allData.filter { it.type == getString(R.string.offer) }.let { list ->
                            dataList.clear()
                            dataList.addAll(list)
                            adapter!!.submitList(dataList)
                            binding.rvOffersVsComplaints.scrollToPosition(0)
                            binding.toolbar.title = getString(R.string.offers)
                        }
                    }, {
                        allData.filter { it.type == getString(R.string.complaint_) }.let { list ->
                            dataList.clear()
                            dataList.addAll(list)
                            adapter!!.submitList(dataList)
                            binding.rvOffersVsComplaints.scrollToPosition(0)
                            binding.toolbar.title = getString(R.string.complaints)
                        }
                    }, {
                        allData.filter { it.status == getString(R.string.satisfied) }.let { list ->
                            dataList.clear()
                            dataList.addAll(list)
                            adapter!!.submitList(dataList)
                            binding.rvOffersVsComplaints.smoothScrollToPosition(0)
                            binding.toolbar.title = getString(R.string.satisfied)
                        }
                    }, {
                        allData.filter { it.type == getString(R.string.unsatisfied) }.let { list ->
                            dataList.clear()
                            dataList.addAll(list)
                            adapter!!.submitList(dataList)
                            binding.rvOffersVsComplaints.scrollToPosition(0)
                            binding.toolbar.title = getString(R.string.unsatisfied)
                        }
                    })
                }
            }
            return@OnMenuItemClickListener true
        })


        binding.functionsLayout.apply {
            btnAdd.setOnClickListener {
                visibilityAddViews(!btnAddOffers.isVisible)
            }
            btnAddOffers.setOnClickListener {
                visibilityAddViews(false)
                findNavController().navigate(
                    R.id.action_navigation_offers_to_navigation_add_offers,
                    bundleOf("type" to 1)
                )
            }
            btnAddComplaints.setOnClickListener {
                visibilityAddViews(false)
                findNavController().navigate(
                    R.id.action_navigation_offers_to_navigation_add_offers,
                    bundleOf("type" to 2)
                )
            }
        }

        binding.apply {

            btnSendComplaintsAndOffers.setOnClickListener {
                var type = -1
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                val lists = arrayOf(
                    "Taklif yuborish",
                    "Shikoyat yuborish",
                )

                builder.setSingleChoiceItems(
                    lists, -1
                ) { _, which ->
                    type = which + 1
                }

                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    if (type != -1)
                        findNavController().navigate(
                            R.id.action_navigation_offers_to_navigation_add_offers,
                            bundleOf("type" to type)
                        )
                }
                val dialog = builder.create()

                dialog.show()
            }
        }
    }

    private fun loadView() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.inflateMenu(R.menu.offers_menu)

        adapter = OffersAdapter(requireContext(), this)
        binding.rvOffersVsComplaints.adapter = adapter
        binding.rvOffersVsComplaints.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getOffersAndComplaints()
    }

    private fun loadObservers() {
        viewModel.getAllOffersAndComplaints.observe(viewLifecycleOwner, getAllOffersAndComplaints)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun itemClick(offerId: Int, type: String) {
        findNavController().navigate(
            R.id.action_navigation_offers_to_nav_detail_offer,
            bundleOf("offerId" to offerId, "type" to type)
        )
    }

    private fun visibilityAddViews(state: Boolean) {
        binding.functionsLayout.apply {
            if (state) {
                btnAddComplaints.visible()
                btnAddOffers.visible()
                btnAdd.rotation = 45F
            } else {
                btnAddComplaints.hide()
                btnAddOffers.hide()
                btnAdd.rotation = 0F
            }
        }
    }

}