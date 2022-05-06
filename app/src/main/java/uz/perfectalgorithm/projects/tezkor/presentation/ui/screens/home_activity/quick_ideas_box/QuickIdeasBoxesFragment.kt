package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.IdeasBoxData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.UpdateQuickIdeasBoxData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentQuickIdeasBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.QuickIdeasBoxAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.quick_ideas.QuickItemMoreSheetDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.create_goal_map.CreateMapDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.QuickIdeasBoxesViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideQuickButton
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showQuickButton
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject

/**
 * QuickIdea(TezkorIdealar) yaratish ekrani
 * to'liq holda tayyor
 */
@AndroidEntryPoint
class QuickIdeasBoxesFragment : Fragment() {
    private var _binding: FragmentQuickIdeasBinding? = null
    private val binding: FragmentQuickIdeasBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val boxesViewModel: QuickIdeasBoxesViewModel by viewModels()

    private lateinit var quickIdeasBoxAdapter: QuickIdeasBoxAdapter

    @Inject
    lateinit var goalMapDialog: CreateMapDialog

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var quickItemMoreDialog: QuickItemMoreSheetDialogMaker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickIdeasBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideQuickButton()
        showAppBar()

        storage.isGeneralIdeaBox = false
        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        boxesViewModel.createIdeasBoxLiveData.observe(this, createIdeasBoxObserver)
        boxesViewModel.getAllIdeasBoxesLiveData.observe(
            this,
            getAllIdeasBoxesObserver
        )
        boxesViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        boxesViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        boxesViewModel.updateIdeasBoxLiveData.observe(
            this,
            updateIdeasBoxObserver
        )
        boxesViewModel.deleteIdeasBoxLiveData.observe(
            this,
            deleteIdeasBoxObserver
        )


    }

    private fun loadViews() {
        quickIdeasBoxAdapter = QuickIdeasBoxAdapter()
        binding.quickIdeasBoxList.adapter = quickIdeasBoxAdapter


        binding.apply {
            fabAddFolderIdea.setOnClickListener {
                if (addIdeaBtn.visibility == View.VISIBLE) {
                    addIdeaBtn.gone()
                    btnAddFolder.gone()
                    fabAddFolderIdea.rotation = 0F
                } else {
                    addIdeaBtn.visible()
                    btnAddFolder.visible()
                    fabAddFolderIdea.rotation = 45F
                }
            }

            btnAddFolder.setOnClickListener {
                goalMapDialog.showSectionsBottomSheetDialog(
                    requireContext(),
                    "Idea qutisini yarating"

                ) {
                    addIdeaBtn.gone()
                    btnAddFolder.gone()
                    fabAddFolderIdea.rotation = 0F
                    boxesViewModel.createIdeasBox(it)
                }
            }

            mainBox.setOnClickListener {
                storage.isGeneralIdeaBox = true
                findNavController().navigate(R.id.action_quickIdeasBoxesFragment_to_quickIdeasCategoriesFragment)
            }

            addIdeaBtn.setOnClickListener {
                findNavController().navigate(QuickIdeasBoxesFragmentDirections.actionQuickIdeasFragmentToCreateQuickIdeaFragment())
            }

            quickIdeasBoxAdapter.setOnItemClickListener { ideaBox ->
                storage.ideaBoxId = ideaBox.id!!
                findNavController().navigate(R.id.action_quickIdeasBoxesFragment_to_personalQuickIdeaFragment)
            }

            quickIdeasBoxAdapter.setOnItemMoreButtonClickListener { ideaBoxData ->
                quickItemMoreDialog.showBottomSheetDialog(requireContext(),
                    {
                        goalMapDialog.showSectionsBottomSheetDialog(
                            requireContext(),
                            "Idea qutisini tahrirlang",
                            ideaBoxData.title

                        ) {
                            val data = UpdateQuickIdeasBoxData(it)
                            boxesViewModel.updateIdeaBox(ideaBoxData.id!!, data)
                        }
                    },
                    {
                        boxesViewModel.deleteIdeaBox(ideaBoxData.id!!)
                    })
            }
        }
    }

    private val updateIdeasBoxObserver = Observer<IdeasBoxData> { ideaBoxData ->
        val ls = quickIdeasBoxAdapter.currentList.toMutableList()
        val pos = ls.indexOfFirst { ideaBoxData.id == it.id }
        ls[pos].title = ideaBoxData.title
        quickIdeasBoxAdapter.submitList(ls)
        quickIdeasBoxAdapter.notifyItemChanged(pos)
        makeSuccessSnack(getString(R.string.successfuly_updated))
    }

    private val deleteIdeasBoxObserver = Observer<IdeasBoxData> { ideaBoxData ->
        makeSuccessSnack(getString(R.string.successfuly_deleted))
        val ls = quickIdeasBoxAdapter.currentList.toMutableList()
        val pos = ls.indexOfFirst { ideaBoxData.id == it.id }
        ls.removeAt(pos)
        quickIdeasBoxAdapter.submitList(ls)
    }

    private val createIdeasBoxObserver = Observer<IdeasBoxData> {
        val ls = quickIdeasBoxAdapter.currentList.toMutableList()
        ls.add(it)
        quickIdeasBoxAdapter.submitList(ls)
        binding.quickIdeasBoxList.smoothScrollToPosition(ls.size - 1)
        binding.tvError.gone()
        makeSuccessSnack(getString(R.string.created_toast))

    }

    private val getAllIdeasBoxesObserver = Observer<List<IdeasBoxData>> {
        if (it.isEmpty()) {
            binding.tvError.show()
        } else {
            binding.tvError.gone()
            quickIdeasBoxAdapter.submitList(it)
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
        val progressView = binding.createQuickIdeaBoxProgressBar
        if (it) progressView.progressLoader.show()
        else progressView.progressLoader.gone()
    }

    override fun onResume() {
        super.onResume()
        boxesViewModel.getAllIdeasBoxes()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        showQuickButton()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}