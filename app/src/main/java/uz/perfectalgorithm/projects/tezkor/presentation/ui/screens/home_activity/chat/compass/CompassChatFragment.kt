package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.compass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCompassChatBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.compass.CompassChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu

@AndroidEntryPoint
class CompassChatFragment : Fragment() {

    private var _binding: FragmentCompassChatBinding? = null
    private val binding: FragmentCompassChatBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: CompassChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompassChatBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showAppBar()
        showBottomMenu()
    }
}