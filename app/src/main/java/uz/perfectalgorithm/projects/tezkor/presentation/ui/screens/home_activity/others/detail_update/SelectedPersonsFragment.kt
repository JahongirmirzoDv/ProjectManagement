package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentSelectedPersonsBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.SelectedPersonAdapter
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu

/**
 *Created by farrukh_kh on 10/22/21 11:07 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update
 **/
/**
 * Tanlangan xodimlar oynasi
 * Create, Detail va Edit qismlarda ishlatish uchun
 */
@AndroidEntryPoint
class SelectedPersonsFragment : Fragment() {

    private var _binding: FragmentSelectedPersonsBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val selectedPersons by lazy { SelectedPersonsFragmentArgs.fromBundle(requireArguments()).selectedPersons }
    private val selectedPersonAdapter by lazy { SelectedPersonAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedPersonsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()

        setupViews()
    }

    private fun setupViews() = with(binding) {
        rvSelectedPersons.adapter = selectedPersonAdapter
        selectedPersonAdapter.submitList(selectedPersons.toMutableList())

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}