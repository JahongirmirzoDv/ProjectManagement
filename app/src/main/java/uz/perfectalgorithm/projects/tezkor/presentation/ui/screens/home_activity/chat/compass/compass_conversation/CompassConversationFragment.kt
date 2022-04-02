package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.compass.compass_conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompassConversationBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.compass.compass_conversion.CompassConversationViewModel
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu

@AndroidEntryPoint
class CompassConversationFragment : Fragment() {

    private var _binding: FragmentCompassConversationBinding? = null
    private val binding: FragmentCompassConversationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    private val viewModel: CompassConversationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompassConversationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideBottomMenu()
        hideAppBar()
    }
}