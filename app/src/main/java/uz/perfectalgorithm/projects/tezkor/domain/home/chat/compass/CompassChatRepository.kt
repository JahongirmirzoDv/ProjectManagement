package uz.perfectalgorithm.projects.tezkor.domain.home.chat.compass

import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.ChatModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.MessageModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

interface CompassChatRepository {

    var chatId: Int
    var userId: Int
    var receiverUser: AllWorkersResponse.DataItem?

    fun testFirebase(string: String, testCallback: SingleBlock<String>)

    fun getAllChats(chatListCallback: SingleBlock<List<ChatModel>>)

    fun getAllMessages(messageListCallBack: SingleBlock<List<MessageModel>>)
}