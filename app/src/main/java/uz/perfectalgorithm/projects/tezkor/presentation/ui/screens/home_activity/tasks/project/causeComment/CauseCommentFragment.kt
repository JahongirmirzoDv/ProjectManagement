package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.project.causeComment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.CauseCommentsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCauseCommentBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional.CauseCommentAdapter
import uz.perfectalgorithm.projects.tezkor.utils.COMMENT_LIST

@AndroidEntryPoint
class CauseCommentFragment : Fragment() {

    private var _binding: FragmentCauseCommentBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val adapter by lazy { CauseCommentAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCauseCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list =
            arguments?.getParcelableArrayList<CauseCommentsResponse>(COMMENT_LIST) as ArrayList<CauseCommentsResponse>
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            rvComment.layoutManager = LinearLayoutManager(requireContext())
            rvComment.adapter = adapter
        }
        adapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}