package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.offers.detail

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.FilesItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDetailOffersBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.offers.FileItemClickListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.offers.FilesAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.DeleteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.offers.ShowImageDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base.BaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.offers.detailOffers.DetailOffersViewModel
import uz.perfectalgorithm.projects.tezkor.utils.BASE_URL
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil
import uz.perfectalgorithm.projects.tezkor.utils.download.openFile
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

/***
 * Abduraxmonov Abdulla 11/09/2021
 * bu others qismi uchun taklif va shikoyatlar detail uchun ui
 */

@AndroidEntryPoint
class DetailOfferFragment : BaseFragment(), FileItemClickListener {

    private var _binding: FragmentDetailOffersBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: DetailOffersViewModel by viewModels()
    private var filesAdapter: FilesAdapter? = null
    var data: BaseOffersItemResponse.DataResult? = null

    private var downloadID: Long = 0

    private var type = ""
    private var detailId = 0

    @Inject
    lateinit var localStorage: LocalStorage

    private val allData = mutableListOf<FilesItem>()

    private val getOfferData =
        EventObserver<BaseOffersItemResponse.DataResult> { data ->
            this.data = data
            initView(data)
        }


    private val getComplaintData =
        EventObserver<BaseOffersItemResponse.DataResult> { data ->
            this.data = data
            initView(data)
        }

    private val getError = EventObserver<Throwable> {
        loadingDialog?.dismiss()
        loadingAskDialog?.dismiss()
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
//        binding.progressBar.hide()
    }

    private val getProgressData = EventObserver<Boolean> { it ->
        if (it) {
            showLoadingDialog()
        } else {
            loadingDialog?.dismiss()
            loadingAskDialog?.dismiss()
        }
    }

    private val getDeleteOffer = Observer<Boolean> {
        findNavController().navigateUp()
        makeSuccessSnack(getString(R.string.delete_successful))
    }

    private val getDeleteComplaint = Observer<Boolean> {
        findNavController().navigateUp()
        makeSuccessSnack(getString(R.string.delete_successful))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailOffersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailId = requireArguments().getInt("offerId")
        type = requireArguments().getString("type") ?: ""
        loadView()
        loadAction()
        loadObserver()
        if (type == "complaint") {
            viewModel.getComplaint(detailId)
        } else {
            viewModel.getOffer(detailId)
        }
    }

    private fun loadObserver() {
        viewModel.getComplaintLiveData.observe(viewLifecycleOwner, getComplaintData)
        viewModel.getOfferLiveData.observe(viewLifecycleOwner, getOfferData)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
        viewModel.deleteComplaint.observe(viewLifecycleOwner, getDeleteComplaint)
        viewModel.deleteOffer.observe(viewLifecycleOwner, getDeleteOffer)
    }

    private fun loadAction() {
        binding.btnSend.setOnClickListener {

            findNavController().navigate(
                DetailOfferFragmentDirections.actionNavDetailOfferToNavigationAddTask(taskTitle = data?.description)
            )
        }

        binding.btnDelete.setOnClickListener {
            val deleteDialog = DeleteDialog(requireContext())
            deleteDialog.show()
            deleteDialog.deleteClickListener {
                if (type == getString(R.string.offer)) {
                    viewModel.deleteOffer(detailId)
                } else {
                    viewModel.deleteComplaint(detailId)
                }
            }
        }
    }

    private fun loadView() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        filesAdapter = FilesAdapter(requireContext(), this)
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)

        binding.rvFiles.apply {
            adapter = filesAdapter
            layoutManager = gridLayoutManager
        }

        if (localStorage.role == RoleEnum.OWNER.text) {
            binding.btnSend.show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initView(data: BaseOffersItemResponse.DataResult) {
        binding.apply {
            timberLog(data.creator.id.toString())
            if (localStorage.userId == data.creator.id) {
                btnDelete.show()
            }

            tvDate.text = DateUtil.convertDate(data.createdAt)
            tvDescription.text = data.description
            tvId.text = "${data.type} #${data.id}"
            tvCreatorUser.text = "${data.creator.lastName} ${data.creator.firstName}"
            if (data.to.lastName.isNullOrEmpty()) {
                tvToUser.text = getString(R.string.over_company)
            } else {
                tvToUser.text = "${data.to.lastName} ${data.to.firstName}"
            }
            allData.clear()
            data.getFiles.forEachIndexed { index, it ->
                if (it.file.endsWith("jpeg") || it.file.endsWith("jpg") || it.file.endsWith("png") || it.file.endsWith(
                        "svg"
                    ) || it.file.endsWith(
                        "bmp"
                    )
                ) {
                    allData.add(
                        FilesItem(
                            index, it.file,
                            it.file, type = "", viewType = 1, it.size
                        )
                    )
                } else {
                    allData.add(
                        FilesItem(
                            index, it.file,
                            BASE_URL + it.file, type = "", viewType = 2, it.size
                        )
                    )
                }
            }
            filesAdapter!!.submitList(allData)
        }
    }

    override fun itemClickFile(itemData: FilesItem) {
        when {
            itemData.isDownloading -> {

            }
            itemData.file.exists() -> {
                requireActivity().openFile(itemData.file)
            }
            else -> {
                try {
                    beginDownload(itemData)
                } catch (e: Exception) {
                    makeErrorSnack("error ${e.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun beginDownload(filesItem: FilesItem) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                filesAdapter!!.setDownloading(filesItem, true)
            }
            val path = filesItem.path
            val request = DownloadManager.Request(Uri.parse(path))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(
                    Uri.fromFile(
                        filesItem.file
                    )
                )
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setDescription("Downloading")
            val downloadManager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request) // enqueue puts the download request in the queue.

            var finishDownload = false
            var progress: Int
            while (!finishDownload) {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_FAILED -> {
                            finishDownload = true
                            withContext(Dispatchers.Main) {
                                filesAdapter!!.setDownloading(filesItem, false)
                            }
                        }
                        DownloadManager.STATUS_PAUSED -> {
                        }
                        DownloadManager.STATUS_PENDING -> {
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            val total =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                val downloaded =
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                progress = (downloaded * 100L / total).toInt()
                                withContext(Dispatchers.Main) {
                                    filesAdapter!!.setProgress(filesItem, progress)
                                }
                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100
                            finishDownload = true
                            withContext(Dispatchers.Main) {
                                filesAdapter!!.setDownloading(filesItem, false)
                                makeSuccessSnack("Download Completed")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun itemClickImage(imageUrl: String) {
        val dialog = ShowImageDialog(imageUrl)
        dialog.show(
            (requireActivity() as HomeActivity).supportFragmentManager,
            "Show Image Dialog"
        )
    }
}