package uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response

data class MessageModel(
    val messageID: Int = 0,
    val messageText: String = "",
    val senderID: Int = 0,
    val receiverID: Int = 0,
    val sendDate: Long = 0L,
    val imageMessageURL: String = "",
    var isRead: Boolean = false
) {
    fun getIsSeen(): Boolean {
        return this.isRead
    }

    fun setIsSeen(
        isRead: Boolean
    ) {
        this.isRead = isRead
    }
}

