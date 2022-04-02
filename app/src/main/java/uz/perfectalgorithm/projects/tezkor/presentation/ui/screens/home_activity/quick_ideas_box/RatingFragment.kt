package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.FilesItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingDataBody
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingIdea
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentRatingBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.offers.FileItemClickListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.offers.FilesAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.RatingViewModel
import uz.perfectalgorithm.projects.tezkor.utils.BASE_URL
import uz.perfectalgorithm.projects.tezkor.utils.download.openFile
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Kurganbayev Jasurbek
 * RatingFragment oynasi ideani baholash oynasi
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/

@AndroidEntryPoint
class RatingFragment : Fragment(), FileItemClickListener {

    private var _binding: FragmentRatingBinding? = null
    private val binding: FragmentRatingBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private var downloadID: Long = 0


    private val args: RatingFragmentArgs by navArgs()

    private val allData = mutableListOf<FilesItem>()

    private val ratingViewModel: RatingViewModel by viewModels()

    private lateinit var filesAdapter: FilesAdapter

    private var rateValue = 0

    private var ideaData: RatingIdea? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()

        if (args.ideaId != 0) {
            ratingViewModel.getRatingIdea(args.ideaId)
        }

        if (args.screen.equals("personal")) {
            binding.apply {
                btnComment.gone()

            }
        }
        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        ratingViewModel.getRatingIdeaLiveData.observe(viewLifecycleOwner, getRatingIdeaObserver)
        ratingViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        ratingViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        ratingViewModel.rateIdeaLiveData.observe(this, rateIdeaObserver)
    }

    private val rateIdeaObserver = Observer<RatingDataBody> {
        binding.rateRatingBar.gone()
        binding.ratingBtn.gone()
        makeSuccessSnack("Siz ushbu ideani baholadingiz")
    }

    private val getRatingIdeaObserver = Observer<RatingIdea> { idea ->
        ideaData = idea
        binding.btnComment.show()
        if (idea.isRated == true || args.screen.equals("personal")) {
            binding.ratingLayout.gone()
            binding.ratingBtn.gone()
        } else {
            binding.ratingLayout.visible()
            binding.ratingBtn.visible()
        }

        binding.apply {
            toolbar.title = idea.title

            ivCreatorUsers.loadImageUrl(idea.creator?.image!!)
            tvCreatorUser.text = idea.creator.firstName + " " + idea.creator.lastName

            val time = idea.createdAt?.replace("-", ".")
            val resultDate = time?.split("T")?.get(0)
            tvDate.text = resultDate

            tvDescription.text = idea.description

            allData.clear()
            idea.files?.forEachIndexed { index, it ->
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
            filesAdapter.submitList(allData)
        }

    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.ratingProgressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private fun loadViews() {
        filesAdapter = FilesAdapter(requireContext(), this)
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)

        binding.rvFiles.apply {
            adapter = filesAdapter
            layoutManager = gridLayoutManager
        }

        binding.apply {

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            btnComment.setOnClickListener {
                findNavController().navigate(
                    RatingFragmentDirections.actionRatingFragment2ToCommentFragment(
                        ideaData
                    )
                )
            }


            rateRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
                rateValue = rating.toInt()
                if (rateValue <= 0) {
                    ratingBtn.disable()
                    ratingBtn.setBackgroundResource(R.drawable.disabled_button_bg)
                } else {
                    ratingBtn.enable()
                    ratingBtn.setBackgroundResource(R.drawable.back_button_blue)

                }
            }

            ratingBtn.setOnClickListener {
                if (rateValue <= 0) {
                    makeErrorSnack("Dastlab ideani baholang!!")
                } else {
                    binding.ratingLayout.gone()
                    binding.ratingBtn.gone()
                    val rateData = RatingDataBody(rateValue)
                    ratingViewModel.rateIdea(args.ideaId, rateData)

                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    override fun itemClickImage(imageUrl: String) {

    }

    private fun beginDownload(filesItem: FilesItem) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                filesAdapter.setDownloading(filesItem, true)
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
                                filesAdapter.setDownloading(filesItem, false)
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
                                    filesAdapter.setProgress(filesItem, progress)
                                }
                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100
                            finishDownload = true
                            withContext(Dispatchers.Main) {
                                filesAdapter.setDownloading(filesItem, false)
                                makeSuccessSnack("Download Completed")
                            }
                        }
                    }
                }
            }
        }
    }

}