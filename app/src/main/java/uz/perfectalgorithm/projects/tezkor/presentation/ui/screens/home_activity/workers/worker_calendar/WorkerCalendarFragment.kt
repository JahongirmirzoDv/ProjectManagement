package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.worker_calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWorkerCalendarBinding

class WorkerCalendarFragment : Fragment() {

    private var _binding: FragmentWorkerCalendarBinding? = null
    private val binding: FragmentWorkerCalendarBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(R.string.null_binding)
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


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