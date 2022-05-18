package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentSelectedFilesBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.SelectedFileAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.SelectedFileAdapter.Companion.VIEW_TYPE_IMAGE_NEW
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.SelectedFileAdapter.Companion.VIEW_TYPE_OTHER_NEW
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.SelectedFileAdapterListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapter
import uz.perfectalgorithm.projects.tezkor.utils.DELETED_FILES
import uz.perfectalgorithm.projects.tezkor.utils.FILES_FOR_ADAPTER
import uz.perfectalgorithm.projects.tezkor.utils.NEW_FILES
import uz.perfectalgorithm.projects.tezkor.utils.adding.getCompressorFile
import uz.perfectalgorithm.projects.tezkor.utils.adding.getFileFromDevice
import uz.perfectalgorithm.projects.tezkor.utils.download.isImageFile
import uz.perfectalgorithm.projects.tezkor.utils.download.openFile
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import java.io.File
import java.net.URI

/**
 *Created by farrukh_kh on 10/25/21 9:31 AM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update
 **/
/**
 * Tanlangan fayllar oynasi
 * Create, Detail va Edit screen larda foydalanish uchun
 */
@AndroidEntryPoint
class SelectedFilesFragment : Fragment(), SelectedFileAdapterListener {

    private var _binding: FragmentSelectedFilesBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val args by navArgs<SelectedFilesFragmentArgs>()
    private val selectedFileAdapter by lazy { SelectedFileAdapter(this, args.isEditorMode) }
    private var downloadID: Long = 0
    private val newFiles by lazy { mutableSetOf<File>() }
    private val deletedFiles by lazy { mutableSetOf<Int>() }
    private var filesForAdapter: MutableList<FilesItem>? = null
    private var isInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedFilesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()

        setupViews()
    }

    private fun setupViews() = with(binding) {
        fabAddFile.isVisible = args.isEditorMode
        ivSubmit.isVisible = args.isEditorMode
        rvSelectedFiles.adapter = selectedFileAdapter
        if (!isInitialized) {
            newFiles.addAll(args.selectedFiles.filter { it.viewType == VIEW_TYPE_IMAGE_NEW || it.viewType == VIEW_TYPE_OTHER_NEW }
                .mapNotNull { it.originalFile })
            isInitialized = true
        }
        if (filesForAdapter == null) {
            filesForAdapter = args.selectedFiles.toMutableList()
        }
        selectedFileAdapter.submitList(filesForAdapter?.toMutableList())
        tvEmpty.isVisible = filesForAdapter.isNullOrEmpty()

        fabAddFile.setOnClickListener {
            hideKeyboard()
            getFileFromDevice(fileSelectResultLauncher)
        }
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        ivSubmit.setOnClickListener {
//            setDeletedFilesResult(deletedFiles)
//            setNewFilesResult(newFiles)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                DELETED_FILES,
                deletedFiles.toList()
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                NEW_FILES,
                newFiles.toList()
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                FILES_FOR_ADAPTER,
                filesForAdapter
            )
            findNavController().navigateUp()
        }
    }

    override fun onLocalFileClick(item: FilesItem) {
        try {
            requireActivity().openFile(File(URI.create(item.path)))
        } catch (e: Exception) {
            makeErrorSnack(e.message.toString())
        }
    }

    override fun onRemoteFileClick(item: FilesItem) {
        try {
            when {
                item.isDownloading -> makeSuccessSnack("${R.string.downloading}...")
                item.file.exists() -> requireActivity().openFile(item.file)
                else -> try {
                    beginDownload(item)
                } catch (e: Exception) {
                    makeErrorSnack("${getString(R.string.xatolik)}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            makeErrorSnack("${getString(R.string.xatolik)}: ${e.message}")
        }
    }

    override fun onLocalImageClick(item: FilesItem) {
        findNavController().navigate(
            SelectedFilesFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onRemoteImageClick(item: FilesItem) {
        findNavController().navigate(
            SelectedFilesFragmentDirections.toImageDetailsFragment(
                item
            )
        )
    }

    override fun onLocalFileDelete(item: FilesItem) {
        filesForAdapter?.remove(item)
        newFiles.removeAll { it.path == item.path || it == item.file }
//        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        selectedFileAdapter.submitList(filesForAdapter?.toMutableList())
        binding.tvEmpty.isVisible = filesForAdapter.isNullOrEmpty()
    }

    override fun onRemoteFileDelete(item: FilesItem) {
        filesForAdapter?.remove(item)
//        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        newFiles.removeAll { it.path == item.path || it == item.file }
        deletedFiles.add(item.remoteId)
        selectedFileAdapter.submitList(filesForAdapter?.toMutableList())
        binding.tvEmpty.isVisible = filesForAdapter.isNullOrEmpty()
    }

    override fun onLocalImageDelete(item: FilesItem) {
        filesForAdapter?.remove(item)
        newFiles.removeAll { it.path == item.path || it == item.file }
//        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        selectedFileAdapter.submitList(filesForAdapter?.toMutableList())
        binding.tvEmpty.isVisible = filesForAdapter.isNullOrEmpty()
    }

    override fun onRemoteImageDelete(item: FilesItem) {
        filesForAdapter?.remove(item)
        deletedFiles.add(item.remoteId)
//        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
        newFiles.removeAll { it.path == item.path || it == item.file }
        selectedFileAdapter.submitList(filesForAdapter?.toMutableList())
        binding.tvEmpty.isVisible = filesForAdapter.isNullOrEmpty()
    }

    private fun beginDownload(filesItem: FilesItem) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                selectedFileAdapter.setDownloading(filesItem, true)
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
                downloadManager.enqueue(request)

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
                                selectedFileAdapter.setDownloading(filesItem, false)
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
                                    selectedFileAdapter.setProgress(filesItem, progress)
                                }
                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100
                            finishDownload = true
                            withContext(Dispatchers.Main) {
                                selectedFileAdapter.setDownloading(filesItem, false)
                                makeSuccessSnack(getString(R.string.file_uploaded))
                            }
                        }
                    }
                }
            }
        }
    }

    private val fileSelectResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    val uri = result.data?.data!!
                    val file = if (uri.isImageFile(requireContext())) {
                        requireContext().getCompressorFile(uri) ?: FileHelper.fromFile(
                            requireContext(),
                            uri
                        )
                    } else {
                        FileHelper.fromFile(requireContext(), uri)
                    }

                    withContext(Dispatchers.Main) {
                        val newFile = FilesItem(
                            id = selectedFileAdapter.currentList.size,
                            title = file.name,
                            path = file.path,
                            type = "",
                            viewType = if (file.name.isImageFile()) EditFileAdapter.VIEW_TYPE_IMAGE_NEW else EditFileAdapter.VIEW_TYPE_OTHER_NEW,
                            size = file.length(),
                            remoteId = -1
                        )

//                        if (dataHolder.files == null) {
//                            dataHolder.files = mutableListOf()
//                        }
//                        dataHolder.files?.add(file)
                        newFiles.add(file)
                        if (filesForAdapter == null) {
                            filesForAdapter = mutableListOf()
                        }
                        filesForAdapter?.add(newFile)
                        selectedFileAdapter.submitList(filesForAdapter?.toMutableList())
                        binding.tvEmpty.isVisible = filesForAdapter.isNullOrEmpty()
//                        filesAdapter.notifyDataSetChanged()
//                        tvFileCount.text = dataHolder.filesForAdapter?.size.toString()
                    }
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}