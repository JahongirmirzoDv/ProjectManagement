package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.chat_message

class ChatMessage(
    var userId: String? = null,
    var userName: String? = null,
    var message: String? = null,
    var messageType: Int? = null,
    var messageDate: String? = null,
    var messageTime: String? = null
)