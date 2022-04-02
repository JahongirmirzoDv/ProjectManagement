package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentErrorBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar

@AndroidEntryPoint
class ErrorFragment : Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding: FragmentErrorBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentErrorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}