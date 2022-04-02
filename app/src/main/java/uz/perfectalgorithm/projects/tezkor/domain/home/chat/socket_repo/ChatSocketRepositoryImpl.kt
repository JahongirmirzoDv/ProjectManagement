package uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.Coroutines
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.CreateGroupChatResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.DeleteGroupChatResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.*
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.*
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.domvet.utils.models.response.local.ResultData
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Davronbek Raximjanov on 27.09.2021 16:24
 **/

/**
 * Ushbu Repo : Chat bo'limi uchun faqatgina websocket sorovlari va qabuli uchun yaratilgan
 * **/

class ChatSocketRepositoryImpl @Inject constructor(
    private val storage: LocalStorage,
    @Named(SOCKET) private val socketClient: OkHttpClient,
    @Named(SOCKET_ALL) private val socketRequest: Request,
    private val gson: Gson,
) : ChatSocketRepository {

    private var webSocket: WebSocket? = null

    override var isConnected: Boolean = false

    override fun createPersonalChat(receiverId: Int) {
        Coroutines.ioThenMain(
            {
                val sendData =
                    CreatePersonalChatRequest.RequestData(
                        data = CreatePersonalChatRequest.CreateData(
                            receiver = receiverId
                        ),
                        action = CREATE_PERSONAL_CHAT_ACTION_REQUEST
                    )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun deletePersonalChat(chatItemId: Int) {
        Coroutines.ioThenMain(
            {
                val mData = DeletePersonalChatRequest.RequestData(
                    action = DELETE_PERSONAL_CHAT_ACTION_REQUEST,
                    data = DeletePersonalChatRequest.Data(chatItemId)
                )

                val jsonData = gson.toJson(mData)
                webSocket?.send(jsonData)

            },
            {

            }
        )
    }

    override fun createGroupChat(title: String, members: List<Int>) {
        Coroutines.ioThenMain(
            {
                val sendData =
                    CreateGroupChatRequest.RequestData(
                        data = CreateGroupChatRequest.CreateData(
                            title = title,
                            members = members
                        ),
                        action = CREATE_GROUP_CHAT_ACTION_REQUEST
                    )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun sendMessage(chatId: Int, messageText: String, answerFor: Int?, files: List<Int>?) {
        Coroutines.ioThenMain(
            {
                val mData = SendChatMessageRequest.RequestData(
                    action = SEND_MESSAGE_TO_PERSONAL_CHAT_ACTION_REQUEST,
                    data = SendChatMessageRequest.Data(
                        chatId = chatId,
                        message = messageText,
                        messageType = MESSAGE_TYPE_TEXT_WITH_MEDIA,
                        answerFor = answerFor,
                        files = files
                    )
                )

                val jsonData = gson.toJson(mData)
                webSocket?.send(jsonData)

            },
            {

            }
        )
    }

    override fun sendMessageGroup(
        chatId: Int,
        messageText: String,
        answerFor: Int?,
        files: List<Int>?
    ) {
        Coroutines.ioThenMain(
            {
                val mData = SendChatMessageRequest.RequestData(
                    action = SEND_MESSAGE_TO_GROUP_CHAT_ACTION_REQUEST,
                    data = SendChatMessageRequest.Data(
                        chatId = storage.chatId,
                        message = messageText,
                        messageType = MESSAGE_TYPE_TEXT_WITH_MEDIA,
                        answerFor = answerFor,
                        files = files
                    )
                )

                val jsonData = gson.toJson(mData)
                webSocket?.send(jsonData)

            },
            {

            }
        )
    }

    override fun updateMessage(messageId: Int, newMessageText: String) {
        Coroutines.ioThenMain(
            {
                val sendData = UpdateChatMessageRequest.RequestData(
                    data = UpdateChatMessageRequest.Data(
                        messageId = messageId,
                        newMessage = newMessageText,
                    ),
                    action = UPDATE_MESSAGE_IN_PERSONAL_CHAT_ACTION_REQUEST
                )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun updateMessageGroup(messageId: Int, newMessageText: String) {
        Coroutines.ioThenMain(
            {
                val sendData = UpdateChatMessageGroupRequest.RequestData(
                    data = UpdateChatMessageGroupRequest.Data(
                        messageId = messageId,
                        message = newMessageText,
                    ),
                    action = UPDATE_MESSAGE_IN_GROUP_CHAT_ACTION_REQUEST
                )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun deleteMessages(messageIds: List<Int>) {
        Coroutines.ioThenMain(
            {
                val sendData = DeleteMessagesRequest.RequestData(
                    data = DeleteMessagesRequest.Data(
                        messageIds = messageIds
                    ),
                    action = DELETE_MESSAGES_FROM_PERSONAL_CHAT_ACTION_REQUEST
                )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {
            }
        )
    }

    override fun deleteGroupMessages(messageIds: List<Int>) {
        Coroutines.ioThenMain(
            {
                val sendData = DeleteMessagesGroupRequest.RequestData(
                    data = DeleteMessagesGroupRequest.Data(
                        messageIds = messageIds
                    ),
                    action = DELETE_MESSAGES_FROM_GROUP_CHAT_ACTION_REQUEST
                )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun deleteGroupChat(chatItemId: Int) {
        Coroutines.ioThenMain(
            {
                val mData = DeleteGroupChatRequest.RequestData(
                    action = DELETE_GROUP_CHAT_ACTION_REQUEST,
                    data = DeleteGroupChatRequest.Data(chatItemId)
                )

                val jsonData = gson.toJson(mData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun addMembersToGroup(chatId: Int, membersList: List<Int>) {
        Coroutines.ioThenMain(
            {
                val sendData = AddMembersToGroupChatRequest.RequestData(
                    data = AddMembersToGroupChatRequest.Data(
                        chatId = chatId,
                        membersList = membersList
                    ),
                    action = ADD_MEMBERS_TO_GROUP_CHAT_ACTION_REQUEST
                )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }

    override fun removeMembersFromGroup(chatId: Int, membersList: List<Int>) {
        Coroutines.ioThenMain(
            {
                val sendData = RemoveMembersFromGroupChatRequest.RequestData(
                    data = RemoveMembersFromGroupChatRequest.Data(
                        chatId = chatId,
                        membersList = membersList
                    ),
                    action = REMOVE_MEMBERS_FROM_GROUP_CHAT_ACTION_REQUEST
                )
                val jsonData = gson.toJson(sendData)
                webSocket?.send(jsonData)
            },
            {

            }
        )
    }


    /**  LIVEDATA **/
    private val onErrorResponseLiveData =
        MutableLiveData<ResultData<Throwable>>()

    private val onErrorStringResponseLiveData =
        MutableLiveData<ResultData<String>>()

    private val onCreatePersonalChatLiveData =
        MutableLiveData<ResultData<PersonalChatListResponse.PersonalChatDataItem>>()

    private val onDeletePersonalChatLiveData =
        MutableLiveData<ResultData<Int>>()

    private val onSendPersonalChatMessageLiveData =
        MutableLiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>>()

    private val onUpdatePersonalChatMessageLiveData =
        MutableLiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>>()

    private val onDeletePersonalChatMessagesLiveData =
        MutableLiveData<ResultData<List<Int>>>()

    /*GroupChat*/

    private val onCreateGroupChatLiveData =
        MutableLiveData<ResultData<ChatGroupListResponse.GroupChatDataItem>>()

    private val onDeleteGroupChatLiveData =
        MutableLiveData<ResultData<Int>>()

    private val onSendGroupChatMessageLiveData =
        MutableLiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>()

    private val onUpdateGroupChatMessageLiveData =
        MutableLiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>()

    private val onDeleteGroupChatMessagesLiveData =
        MutableLiveData<ResultData<List<Int>>>()


    /**  Responses Come LiveData **/
    override fun onErrorResponseCome(): LiveData<ResultData<Throwable>> =
        onErrorResponseLiveData

    override fun onCreatePersonalChatCome(): LiveData<ResultData<PersonalChatListResponse.PersonalChatDataItem>> =
        onCreatePersonalChatLiveData

    override fun onDeletePersonalChatCome(): LiveData<ResultData<Int>> =
        onDeletePersonalChatLiveData

    override fun onSendMessagePersonalChatCome(): LiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>> =
        onSendPersonalChatMessageLiveData

    override fun onUpdateMessagePersonalChatCome(): LiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>> =
        onUpdatePersonalChatMessageLiveData

    override fun onDeleteMessagesPersonalChatCome(): LiveData<ResultData<List<Int>>> =
        onDeletePersonalChatMessagesLiveData

    /*GroupChat*/
    override fun onSendMessageGroupChatCome(): LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>> =
        onSendGroupChatMessageLiveData

    override fun onUpdateMessageGroupChatCome(): LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>> =
        onUpdateGroupChatMessageLiveData

    override fun onCreateGroupChatCome(): LiveData<ResultData<ChatGroupListResponse.GroupChatDataItem>> =
        onCreateGroupChatLiveData

    override fun onDeleteGroupChatCome(): LiveData<ResultData<Int>> =
        onDeleteGroupChatLiveData

    override fun onDeleteMessagesGroupChatCome(): LiveData<ResultData<List<Int>>> =
        onDeleteGroupChatMessagesLiveData

    override fun connectSocket() {
        createWebSocket()
    }

    override fun disconnectSocket() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        webSocket = null
        isConnected = false
    }

    private fun createWebSocket() {
        try {
            webSocket = socketClient.newWebSocket(socketRequest, EchoWebSocketListener())
            myLogD("WEBSOCKET connected !!! :REPO", SOCKET_STATUS_TAG)
            isConnected = true

        } catch (e: Exception) {
            myLogD("ERROR with connect !!! :REPO", SOCKET_STATUS_TAG)
            myLogD("$e", SOCKET_STATUS_TAG)
        }
    }


    /**  Response Listener : -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= **/
    /**
     * Qabul qilish Classi - websocket da har qanday malumot shu yerga kelib tushadi va
     * uni action_type("huddi kalit so'z") orqali ajratib olamiz, shunga mos model class ga ozgartirib ive data larga berib yuboramiz
     * live datani esa viewmodelga yuboramiz
     * :
     *  **/

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            val responseJson = JSONObject(text)
            myLogD("Socket response : $text", SOCKET_RESPONSE_TAG)
            when (responseJson.getString("action")) {

                CREATE_PERSONAL_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(text, CreatePersonalChatResponse.ResponseData::class.java)
                    onCreatePersonalChatLiveData.postValue(ResultData.data(response.data))
                }

                CREATE_PERSONAL_CHAT_EXISTS_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(text, CreatePersonalChatResponse.ResponseData::class.java)
                    onCreatePersonalChatLiveData.postValue(ResultData.data(response.data))
                }

                DELETE_PERSONAL_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            DeletePersonalChatResponse.ResponseData::class.java
                        )
                    if (response.data?.deletedChatId != null) {
                        onDeletePersonalChatLiveData.postValue(ResultData.data(response.data.deletedChatId))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }

                SEND_MESSAGE_TO_PERSONAL_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            PersonalChatSendMessageResponse.ResponseData::class.java
                        )
                    if (response.data != null) {
                        onSendPersonalChatMessageLiveData.postValue(ResultData.data(response.data))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }
                UPDATE_MESSAGE_IN_PERSONAL_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            PersonalChatSendMessageResponse.ResponseData::class.java
                        )
                    if (response.data != null) {
                        onUpdatePersonalChatMessageLiveData.postValue(ResultData.data(response.data))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }
                DELETE_MESSAGES_FROM_PERSONAL_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            DeleteMessagesResponse.ResponseData::class.java
                        )
                    if (response.data != null) {
                        onDeletePersonalChatMessagesLiveData.postValue(ResultData.data(response.data))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }
                READ_MESSAGE_IN_PERSONAL_CHAT_ACTION_RESPONSE -> {

                }

                /** GroupChat*/


                CREATE_GROUP_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(text, CreateGroupChatResponse.ResponseData::class.java)
                    if (response.data != null) {
                        onCreateGroupChatLiveData.postValue(ResultData.data(response.data))
                    }
                }
                DELETE_GROUP_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            DeleteGroupChatResponse.ResponseData::class.java
                        )
                    if (response.data?.chatId != null) {
                        onDeleteGroupChatLiveData.postValue(ResultData.data(response.data.chatId))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }

                SEND_MESSAGE_TO_GROUP_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            GroupChatSendMessageResponse.ResponseData::class.java
                        )
                    myLogD(text, "TAG10")

                    if (response.data != null) {
                        onSendGroupChatMessageLiveData.postValue(ResultData.data(response.data))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }
                UPDATE_MESSAGE_IN_GROUP_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            GroupChatSendMessageResponse.ResponseData::class.java
                        )
                    if (response.data != null) {
                        onUpdateGroupChatMessageLiveData.postValue(ResultData.data(response.data))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }
                DELETE_MESSAGES_FROM_GROUP_CHAT_ACTION_RESPONSE -> {
                    val response =
                        gson.fromJson(
                            text,
                            DeleteMessagesResponse.ResponseData::class.java
                        )
                    if (response.data != null) {
                        onDeleteGroupChatMessagesLiveData.postValue(ResultData.data(response.data))
                    } else {
                        onErrorResponseLiveData.postValue(ResultData.data(Throwable()))
                    }
                }

                else -> {

                }
            }

            if (!responseJson.getBoolean("is_success")) {
                val response =
                    gson.fromJson(
                        text,
                        DeleteGroupChatResponse.ResponseData::class.java
                    )
                onErrorResponseLiveData.postValue(ResultData.data(Throwable(response.message?.ui)))
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            onErrorResponseLiveData.postValue(ResultData.data(t))
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
        }

    }

}


