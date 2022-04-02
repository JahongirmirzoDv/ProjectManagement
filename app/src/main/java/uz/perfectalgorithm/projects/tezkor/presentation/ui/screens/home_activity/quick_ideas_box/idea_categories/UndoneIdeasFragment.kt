package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.idea_categories

import android.annotation.SuppressLint
import android.os.Bundle
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
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentUndoneIdeasBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.UndoneIdeaAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.QuickIdeasCategoriesFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.create_idea.idea_categories.UndoneIdeasViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import javax.inject.Inject

@AndroidEntryPoint
class UndoneIdeasFragment : Fragment() {
    private var _binding: FragmentUndoneIdeasBinding? = null
    private val binding: FragmentUndoneIdeasBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val undoneIdeasViewModel: UndoneIdeasViewModel by viewModels()
    private lateinit var undoneIdeasAdapter: UndoneIdeaAdapter

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUndoneIdeasBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        undoneIdeasAdapter = UndoneIdeaAdapter(0)
        binding.undoneIdeasList.adapter = undoneIdeasAdapter

        undoneIdeasAdapter.setOnClickListener {
            storage.ideaId = it.id!!
            findNavController().navigate(
                QuickIdeasCategoriesFragmentDirections.actionQuickIdeasCategoriesFragmentToRatingFragment2(
                    it.id,
                )
            )
        }


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
        val progressView = binding.undoneIdeasProgress
        if (it) progressView.show()
        else progressView.gone()
    }

    override fun onResume() {
        super.onResume()
        undoneIdeasViewModel.getGeneralUndoneIdeas()


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