package uz.perfectalgorithm.projects.tezkor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentFilesListBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.SharedViewModel

@AndroidEntryPoint
class FilesListFragment : Fragment() {
    private var _binding: FragmentFilesListBinding? = null
    private val binding: FragmentFilesListBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFilesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}