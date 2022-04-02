package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.idea_categories

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.RatingIdeaData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentRatingIdeasBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.RatingIdeasAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box.QuickIdeasCategoriesFragmentDirections
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.create_idea.idea_categories.RatingIdeasViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import javax.inject.Inject

@AndroidEntryPoint
class RatingIdeasFragment : Fragment() {
    private var _binding: FragmentRatingIdeasBinding? = null
    private val binding: FragmentRatingIdeasBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val ratingIdeasViewModel: RatingIdeasViewModel by viewModels()
    private lateinit var ratingIdeasAdapter: RatingIdeasAdapter

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingIdeasBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        ratingIdeasViewModel.getAllRatingIdeas.observe(this, allRatingIdeasObserver)
        ratingIdeasViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        ratingIdeasViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
    }

    private val allRatingIdeasObserver = Observer<List<RatingIdeaData>> {
        if (it.isEmpty()) {
            binding.tvError.show()
        } else {
            binding.tvError.gone()
            ratingIdeasAdapter.submitList(it)
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
        val progressView = binding.ratingIdeasProgress
        if (it) progressView.show()
        else progressView.gone()
    }


    private fun loadViews() {
        ratingIdeasAdapter = RatingIdeasAdapter()
        binding.ratingIdeasList.adapter = ratingIdeasAdapter

        ratingIdeasAdapter.setOnItemClickListener {
            findNavController().navigate(
                QuickIdeasCategoriesFragmentDirections.actionQuickIdeasCategoriesFragmentToRatingFragment2(
                    it.id!!
                )
            )
        }

    }

    override fun onResume() {
        super.onResume()
//        ratingIdeasViewModel.getRatingIdeas(storage.ideaBoxId)
        if (storage.isGeneralIdeaBox) {
            Log.d("SSSS", "RatingIdeasViewModel IF piece")
            ratingIdeasViewModel.getGeneralRatingIdeas()
        } else {
            Log.d("SSSS", "RatingIdeasViewModel ELSE piece")

            ratingIdeasViewModel.getRatingIdeas(storage.ideaBoxId)
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


}