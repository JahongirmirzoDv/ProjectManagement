package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.databinding.ItemListLoadStateBinding

/**
 *Created by farrukh_kh on 8/21/21 5:19 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.others.paging
 **/
class ListLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ListLoadStateAdapter.ListLoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        ListLoadStateViewHolder(
            ItemListLoadStateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ListLoadStateViewHolder, loadState: LoadState) {
        holder.onBindLoadState(loadState)
    }

    inner class ListLoadStateViewHolder(private val binding: ItemListLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun onBindLoadState(loadState: LoadState) = with(binding) {
            pbLoading.isVisible = loadState is LoadState.Loading
            txvError.isVisible = loadState !is LoadState.Loading
            btnRetry.isVisible = loadState !is LoadState.Loading
            if (loadState is LoadState.Error) {
                txvError.text = loadState.error.message
            }
        }
    }
}