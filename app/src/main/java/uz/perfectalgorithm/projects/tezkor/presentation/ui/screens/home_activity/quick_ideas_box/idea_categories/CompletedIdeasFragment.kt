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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.FinishedIdeaItem
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompletedIdeasBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.FinishedIdeasAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.QuickIdeasCategoriesFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.create_idea.idea_categories.CompletedIdeasViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import javax.inject.Inject

@AndroidEntryPoint
class CompletedIdeasFragment : Fragment() {
    private var _binding: FragmentCompletedIdeasBinding? = null
    private val binding: FragmentCompletedIdeasBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val completedIdeasViewModel: CompletedIdeasViewModel by viewModels()
    private lateinit var completedIdeasAdapter: FinishedIdeasAdapter


    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedIdeasBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        loadViews()
//        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        completedIdeasViewModel.getAllRatingIdeas.observe(
            this,
            allRatingIdeasObserver
        )
        completedIdeasViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        completedIdeasViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val allRatingIdeasObserver = Observer<List<FinishedIdeaItem>> {
        if (it.isEmpty()) {
//            binding.tvError.show()
        } else {
//            binding.tvError.gone()
            completedIdeasAdapter.submitList(it)
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
//        val progressView = binding.completedIdeasProgress
//        if (it) progressView.show()
//        else progressView.gone()
    }

    private fun loadViews() {
        completedIdeasAdapter = FinishedIdeasAdapter()
//        binding.completedIdeasList.adapter = completedIdeasAdapter

        completedIdeasAdapter.setOnClickListener {
            findNavController().navigate(
                QuickIdeasCategoriesFragmentDirections.actionQuickIdeasCategoriesFragmentToRatingFragment2(
                    it.id!!
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        completedIdeasViewModel.getGeneralFinishedIdeas()
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