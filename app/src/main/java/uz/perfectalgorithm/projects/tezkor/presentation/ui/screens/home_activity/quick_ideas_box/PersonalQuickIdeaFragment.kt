package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.QuickIdeaData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.RatingIdeaData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentPersonalQuickIdeaBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.UndoneIdeaAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.quick_ideas.QuickItemMoreSheetDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.PersonalQuickIdeaViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.showAlertDialog
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject



@AndroidEntryPoint
class PersonalQuickIdeaFragment : Fragment() {

    private var _binding: FragmentPersonalQuickIdeaBinding? = null
    private val binding: FragmentPersonalQuickIdeaBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val undoneIdeasViewModel: PersonalQuickIdeaViewModel by viewModels()
    private lateinit var undoneIdeasAdapter: UndoneIdeaAdapter

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var quickItemMoreDialog: QuickItemMoreSheetDialogMaker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalQuickIdeaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationIcon()
        hideQuickButton()
        hideBottomMenu()
        showAppBar()

        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        undoneIdeasViewModel.getAllUndoneIdeas.observe(this, allRatingIdeasObserver)
        undoneIdeasViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        undoneIdeasViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        undoneIdeasViewModel.deleteIdeaLiveData.observe(this, deleteIdeaObserver)

    }

    private val deleteIdeaObserver = Observer<QuickIdeaData> { ideaData ->
        makeSuccessSnack("Muvaffaqqiyatli o'chirildi")
        val ls = undoneIdeasAdapter.currentList.toMutableList()
        val pos = ls.indexOfFirst { ideaData.id == it.id }
        ls.removeAt(pos)
        undoneIdeasAdapter.submitList(ls)
    }

    private val allRatingIdeasObserver = Observer<List<RatingIdeaData>> {
        if (it.isEmpty()) {
            binding.tvError.show()
        } else {
            binding.tvError.gone()
            undoneIdeasAdapter.submitList(it)
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
        val progressView = binding.personalUndoneIdeasProgress
        if (it) progressView.show()
        else progressView.gone()
    }

    private fun loadViews() {
        undoneIdeasAdapter = UndoneIdeaAdapter(1)
        binding.personaQuickIdeaList.adapter = undoneIdeasAdapter

        binding.apply {
            fabAddFolderIdea.setOnClickListener {
                if (addIdeaBtn.visibility == View.VISIBLE) {
                    addIdeaBtn.gone()
                    fabAddFolderIdea.rotation = 0F
                } else {
                    addIdeaBtn.visible()
                    fabAddFolderIdea.rotation = 45F
                }
            }

            addIdeaBtn.setOnClickListener {
                Log.d("TTT", " addIdeaBtn.setOnClickListener")
                findNavController().navigate(
                    PersonalQuickIdeaFragmentDirections.actionPersonalQuickIdeaFragmentToCreateQuickIdeaFragment()
                )
            }
        }

        undoneIdeasAdapter.setOnClickListener {
            storage.ideaId = it.id!!
            findNavController().navigate(
                PersonalQuickIdeaFragmentDirections.actionPersonalQuickIdeaFragmentToRatingFragment2(
                    it.id,
                    "personal"
                )
            )
        }

        undoneIdeasAdapter.setOnItemMoreClickListener { ratingData ->
            quickItemMoreDialog.showBottomSheetDialog(requireContext(),
                {
                    storage.ideaId = ratingData.id!!
                    findNavController().navigate(
                        PersonalQuickIdeaFragmentDirections.actionPersonalQuickIdeaFragmentToCreateQuickIdeaFragment(
                            ratingData.id
                        )
                    )
                },
                {
                    showAlertDialog("Ushbu ideani o'chirishni xohlaysizmi") {
                        undoneIdeasViewModel.deleteIdea(ratingData.id!!)
                    }
                })
        }

    }

    override fun onResume() {
        super.onResume()
        undoneIdeasViewModel.getUndoneIdeas(storage.ideaBoxId)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}