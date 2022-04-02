package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.create_idea

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.IdeasBoxData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.QuickIdeaData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.UpdateIdeaItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingIdea
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateQuickIdeaBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.create_idea.CreateQuickIdeaViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.helper.FileHelper
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.showAlertDialog
import java.io.File

/**
 * Kurganbayev Jasurbek
 * CreateQuickIderFragment tezlor idealar yaratish oynasi oynasi
 * to'liq holda tayyor qilingan, design bilan ko'rib qo'yish kerak
 **/

@AndroidEntryPoint
class CreateQuickIdeaFragment : Fragment() {

    private var _binding: FragmentCreateQuickIdeaBinding? = null
    private val binding: FragmentCreateQuickIdeaBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: CreateQuickIdeaFragmentArgs by navArgs()


    private val createQuickIdeaViewModel: CreateQuickIdeaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateQuickIdeaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()

        if (args.ideaId > 0) {
            binding.apply {
                title.text = "Idea o'zgartirish"
                btnIdeaUpload.text = "O'zgartirish"
                createQuickIdeaBlueButton.setBackgroundResource(R.drawable.ic_edit_ic)
                createQuickIdeaViewModel.getQuickIdea(args.ideaId)
                constPassToIdeaBox.show()
            }
        }

        if (args.screen == "category_screen") {
            binding.apply {
                linkingTaskToTaskFolderLayout.gone()
            }
        }


        loadViews()
        loadObservers()
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {

//
        createQuickIdeaViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        createQuickIdeaViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        createQuickIdeaViewModel.allIdeaBoxLiveData.observe(this, allIdeaBoxesObserver)
        createQuickIdeaViewModel.createQuickIdeaLiveData.observe(this, createQuickIdeaObserver)
        createQuickIdeaViewModel.getQuickIdeaLiveData.observe(this, getQuickIdeaObserver)
        createQuickIdeaViewModel.updateQuickLiveData.observe(this, updateQuickIdeaObserver)
    }

    private val updateQuickIdeaObserver = Observer<UpdateIdeaItem> {
        makeSuccessSnack("Muvaffaqqiyatli yangilandi")
        findNavController().popBackStack()
    }

    private val getQuickIdeaObserver = Observer<RatingIdea> { quickIdea ->
        binding.apply {
            etIdeaName.setText(quickIdea.title)
            etIdeaDescription.setText(quickIdea.description)
            createQuickIdeaViewModel.ideaBox = quickIdea.folder
            fileCount.text = quickIdea.files?.size.toString()

        }
    }

    private val createQuickIdeaObserver = Observer<QuickIdeaData> {
        findNavController().navigateUp()
    }

    private val allIdeaBoxesObserver = Observer<List<IdeasBoxData>> {
        createQuickIdeaViewModel.ideaBoxesList = ArrayList()
        val spinnerArrayAdapter =
            ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_item,
                it.map { it.title }
            )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        createQuickIdeaViewModel.ideaBoxesList?.addAll(it)
        binding.spinnerIdeaBox.adapter = spinnerArrayAdapter
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.createQuickIdeaProgressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private fun loadViews() {

        if (createQuickIdeaViewModel.ideaBoxesList != null) {
            val spinnerArrayAdapter =
                ArrayAdapter(
                    this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    createQuickIdeaViewModel.ideaBoxesList?.toList()!!.map { it.title }
                )
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerIdeaBox.adapter = spinnerArrayAdapter
        }

        binding.apply {
            etIdeaName.addTextChangedListener {
                createQuickIdeaViewModel.title = it.toString()
            }

            etIdeaDescription.addTextChangedListener {
                createQuickIdeaViewModel.description = it.toString()
            }

            imgBackButton.setOnClickListener {
                findNavController().popBackStack()
            }

            constPassToIdeaBox.setOnClickListener {

                showAlertDialog("Ushbu ideani idealar qutisiga o'tkazishni xohlaysizmi?") {
                    createQuickIdeaViewModel.updateQuickIdea(
                        args.ideaId,
                        createQuickIdeaViewModel.title!!,
                        createQuickIdeaViewModel.description!!,
                        createQuickIdeaViewModel.files,
                        createQuickIdeaViewModel.ideaBox!!,
                        true
                    )
                }

            }

            createQuickIdeaBlueButton.setOnClickListener {
                createQuickIdea()
            }

            fileAdd.setOnClickListener {
                Permissions.check(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    null,
                    object :
                        PermissionHandler() {
                        override fun onGranted() {
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "*/*"
                            resultLauncher.launch(intent)
                        }

                        override fun onDenied(
                            context: Context?,
                            deniedPermissions: ArrayList<String>?
                        ) {
                        }
                    })
            }


            btnIdeaUpload.setOnClickListener {
                createQuickIdea()
            }
            spinnerIdeaBox.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener,
                    AdapterView.OnItemClickListener {

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        createQuickIdeaViewModel.ideaBox =
                            createQuickIdeaViewModel.ideaBoxesList?.get(position)?.id
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemClick(
                        p0: AdapterView<*>?,
                        p1: View?,
                        p2: Int,
                        p3: Long
                    ) {

                    }
                }

        }
    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.fileCount.text =
                    (binding.fileCount.text.toString().toInt() + 1).toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val data: Intent? = result.data
                    val uri = data?.data!!
                    val compressedImageFile = getCompressorFile(requireContext(), uri)
                    withContext(Dispatchers.Main) {
                        compressedImageFile?.let {
                            createQuickIdeaViewModel.files.add(it)
                        }
                    }
                }
            }
        }

    private suspend fun getCompressorFile(context: Context, uri: Uri): File? {
        return try {
            val imageFile = FileHelper.fromFile(context, uri)
            Compressor.compress(
                context.applicationContext!!,
                imageFile.absoluteFile,
                Dispatchers.IO
            ) {
                quality(90)
            }
        } catch (e: Exception) {
            try {
                FileHelper.fromFile(requireContext(), uri)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun createQuickIdea() {
        binding.apply {
            when {
                etIdeaName.text.isNullOrEmpty() -> {
                    etIdeaName.error = getString(R.string.input_idea_name)
                }
                else -> {
                    if (args.ideaId > 0) {
                        createQuickIdeaViewModel.updateQuickIdea(
                            args.ideaId,
                            createQuickIdeaViewModel.title!!,
                            createQuickIdeaViewModel.description!!,
                            createQuickIdeaViewModel.files,
                            createQuickIdeaViewModel.ideaBox!!,
                            false
                        )
                    } else {
                        if (args.screen == "category_screen") {
                            createQuickIdeaViewModel.createQuickIdeaWithinBox(
                                createQuickIdeaViewModel.title!!,
                                createQuickIdeaViewModel.description,
                                createQuickIdeaViewModel.files,
                                true
                            )
                        } else {
                            if (createQuickIdeaViewModel.ideaBoxesList?.isEmpty() == true) {
                                makeErrorSnack(getString(R.string.empty_list_folder))
                            } else {
                                createQuickIdeaViewModel.createQuickIdea(
                                    createQuickIdeaViewModel.title!!,
                                    createQuickIdeaViewModel.description,
                                    createQuickIdeaViewModel.files,
                                    createQuickIdeaViewModel.ideaBox!!,
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}