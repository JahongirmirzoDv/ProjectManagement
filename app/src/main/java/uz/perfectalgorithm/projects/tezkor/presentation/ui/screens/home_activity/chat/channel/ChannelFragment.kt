package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.channel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentChannelBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.channel.ChannelAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.channel.ChannelViewModel

@AndroidEntryPoint
class ChannelFragment : Fragment() {

    /**
     * Companiya uchun kanal oynasi, hali tayyor emas..
     * **/

    private var _binding: FragmentChannelBinding? = null
    private val binding: FragmentChannelBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: ChannelViewModel by viewModels()

    private lateinit var channelAdapter: ChannelAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChannelBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        loadViews()
//        loadObservers()
    }

    /* private fun loadObservers() {
         viewModel.channelsLiveData.observe(viewLifecycleOwner, channelsObserver)
     }*/

/*
    private fun loadViews() {
        channelAdapter = ChannelAdapter()
        binding.recyclerChannel.adapter = channelAdapter
        viewModel.getChannels()
    }
*/


    private val channelsObserver = Observer<List<ChannelData>> {
        channelAdapter.submitList(it)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getChannels()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}