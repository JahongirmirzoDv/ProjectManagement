package uz.perfectalgorithm.projects.tezkor.utils.flow

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException

/**
 *Created by farrukh_kh on 8/13/21 1:06 PM
 *uz.rdo.projects.projectmanagement.utils.flow
 **/
fun <T : Any> Flow<DataWrapper<T>>.simpleCollect(
    fragment: Fragment,
    pbLoading: View,
    tvEmpty: TextView? = null,
    onSuccess: SingleBlock<T>
) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        collect { dataWrapper ->
            when (dataWrapper) {
                is DataWrapper.Empty -> Unit
                is DataWrapper.Error -> {
                    tvEmpty?.isVisible = true
                    pbLoading.isVisible = false
                    fragment.handleException(dataWrapper.error)
                }
                is DataWrapper.Loading -> {
                    tvEmpty?.isVisible = false
                    pbLoading.isVisible = true
                }
                is DataWrapper.Success -> onSuccess(dataWrapper.data)
            }
        }
    }
}

fun <T : Any> Flow<DataWrapper<T>>.simpleCollectWithRefresh(
    fragment: Fragment,
    srlRefresh: SwipeRefreshLayout?,
    tvError: TextView? = null,
    rvItems: View? = null,
    onError: SingleBlock<Exception>? = null,
    onSuccess: SingleBlock<T>,
) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        collect { dataWrapper ->
            when (dataWrapper) {
                is DataWrapper.Empty -> Unit
                is DataWrapper.Error -> {
                    onError?.invoke(dataWrapper.error)
                    tvError?.isVisible = true
                    rvItems?.isVisible = false
                    srlRefresh?.isRefreshing = false
                    fragment.handleException(dataWrapper.error)
                }
                is DataWrapper.Loading -> {
                    tvError?.isVisible = false
                    srlRefresh?.isRefreshing = true
                }
                is DataWrapper.Success -> {
                    rvItems?.isVisible = true
                    tvError?.isVisible = false
                    onSuccess(dataWrapper.data)
                }
            }
        }
    }
}