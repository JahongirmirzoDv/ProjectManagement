package uz.perfectalgorithm.projects.tezkor.data.sources.datasource.personal_conversation_messages

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ChatAllApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

/**
 * Created by Davronbek Raximjanov on 25.09.2021 21:10
 **/

class PersonalMessagesDataSource(
    private val chatAllApi: ChatAllApi,
    private val storage: LocalStorage,
) : PagingSource<Int, PersonalChatMessageListResponse.MessageDataItem>() {
    private var emptyListener: ((Boolean) -> Unit)? = null

    override fun getRefreshKey(state: PagingState<Int, PersonalChatMessageListResponse.MessageDataItem>): Int? {
        timberLog("getRefreshKey = ${state.anchorPosition}")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PersonalChatMessageListResponse.MessageDataItem> {
        return try {

            val nextPageNumber = params.key ?: 1
            val response = chatAllApi.getPersonalConversationMessageList(
                page = nextPageNumber,
                chatId = storage.chatId,
            )

            emptyListener?.invoke(response.body()?.results!!.isEmpty())

            LoadResult.Page(
                data = response.body()?.results!!,
                prevKey = if (nextPageNumber > 1) nextPageNumber - 1 else null,
                nextKey = if (response.body()?.next!=null)
                    nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(Exception(App.instance.resources.getString(R.string.connection_retry)))
        }
    }

    fun emptyFun(f: (Boolean) -> Unit) {
        emptyListener = f
    }
}