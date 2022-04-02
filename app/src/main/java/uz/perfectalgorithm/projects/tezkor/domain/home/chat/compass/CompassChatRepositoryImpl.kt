package uz.perfectalgorithm.projects.tezkor.domain.home.chat.compass

import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.ChatModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.MessageModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import javax.inject.Inject

class CompassChatRepositoryImpl @Inject constructor(
    private val localStorage: LocalStorage
) : CompassChatRepository {

    override var chatId: Int = 9
    override var userId: Int = 0
    override var receiverUser: AllWorkersResponse.DataItem? = null

    override fun testFirebase(string: String, testCallback: SingleBlock<String>) {

        val testHashMap = HashMap<String, Any>()
        testHashMap["id"] = 1
        testHashMap["title"] = "test_name"

//        firebaseDatabase.reference.child("test").child(string).setValue(testHashMap)
//            .addOnCompleteListener { addTask ->
//                if (addTask.isSuccessful) {
//                    Log.d("TAG19", "Created ok successful")
//                }
//            }
    }

    override fun getAllChats(chatListCallback: SingleBlock<List<ChatModel>>) {
        val chatList = ArrayList<ChatModel>()

//        firebaseDatabase.reference.child(COMPASS_CHAT_KEY).child(localStorage.id)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    chatList.clear()
//
//                    for (pChatSnapshot in snapshot.children) {
//                        val pChat = pChatSnapshot.getValue(
//                            ChatModel::class.java
//                        )
//                        if (pChat != null) {
//                            chatList.add(pChat)
//                        }
//                    }
//                    chatListCallback.invoke(chatList)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
    }

    override fun getAllMessages(messageListCallBack: SingleBlock<List<MessageModel>>) {

    }
}