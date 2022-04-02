package uz.perfectalgorithm.projects.tezkor.utils.constants_chat

const val SOCKET_RESPONSE_TAG = "SCR1"
const val SOCKET_STATUS_TAG = "SCS1"
const val NORMAL_CLOSURE_STATUS = 1000


/** CHAT URL's **/
const val BASE_CHAT_URL = "ws://165.232.70.187:8002"      //original dm
//const val BASE_CHAT_URL = "ws://188.166.215.20:8080"      //original dm
//const val BASE_CHAT_URL = "ws://167.172.175.96:8080"      //original dm
//const val BASE_CHAT_URL = "ws://128.199.192.187:8080"       // dm for test
//const val BASE_CHAT_URL = "ws://algopm.algopharm.uz:8080"       // dm for test domain


/**NAMES**/
const val SOCKET = "Socket"
const val SOCKET_ALL = "Socket all"
const val SOCKET_CLIENT = "Socket_CLIENT"
const val SOCKET_LISTENER = "Socket LISTENER"

const val MESSAGE_TYPE_SERVICE = "service_message"
const val MESSAGE_TYPE_TEXT_WITH_MEDIA = "text_with_media"

/**Personal chat requests **/
const val CREATE_PERSONAL_CHAT_ACTION_REQUEST = "chat.personal.create_chat"
const val DELETE_PERSONAL_CHAT_ACTION_REQUEST = "chat.personal.delete_chat"
const val SEND_MESSAGE_TO_PERSONAL_CHAT_ACTION_REQUEST = "chat.personal.send_message"
const val UPDATE_MESSAGE_IN_PERSONAL_CHAT_ACTION_REQUEST = "chat.personal.edit_message"
const val DELETE_MESSAGES_FROM_PERSONAL_CHAT_ACTION_REQUEST = "chat.personal.delete_messages"
const val READ_MESSAGE_IN_PERSONAL_CHAT_ACTION_REQUEST = "chat.personal.read_messages"

/**Personal chat responses **/
const val CREATE_PERSONAL_CHAT_ACTION_RESPONSE = "chat.personal.create_chat.successfully"
const val CREATE_PERSONAL_CHAT_EXISTS_ACTION_RESPONSE = "chat.personal.create_chat.exists"
const val DELETE_PERSONAL_CHAT_ACTION_RESPONSE = "chat.personal.delete_chat.successfully"
const val SEND_MESSAGE_TO_PERSONAL_CHAT_ACTION_RESPONSE = "chat.personal.send_message.successfully"
const val UPDATE_MESSAGE_IN_PERSONAL_CHAT_ACTION_RESPONSE = "chat.personal.edit_message.successfully"
const val DELETE_MESSAGES_FROM_PERSONAL_CHAT_ACTION_RESPONSE = "chat.personal.delete_messages.successfully"
const val READ_MESSAGE_IN_PERSONAL_CHAT_ACTION_RESPONSE = "chat.personal.read_messages.successfully"



/**Group chat requests **/
const val CREATE_GROUP_CHAT_ACTION_REQUEST = "chat.group.create_chat"
const val DELETE_GROUP_CHAT_ACTION_REQUEST = "chat.group.delete_chat"
const val SEND_MESSAGE_TO_GROUP_CHAT_ACTION_REQUEST = "chat.group.send_message"
const val UPDATE_MESSAGE_IN_GROUP_CHAT_ACTION_REQUEST = "chat.group.edit_message"
const val DELETE_MESSAGES_FROM_GROUP_CHAT_ACTION_REQUEST = "chat.group.delete_messages"
const val ADD_MEMBERS_TO_GROUP_CHAT_ACTION_REQUEST = "chat.group.add_member"
const val REMOVE_MEMBERS_FROM_GROUP_CHAT_ACTION_REQUEST = "chat.group.delete_member"


/**Group chat responses **/
const val CREATE_GROUP_CHAT_ACTION_RESPONSE = "chat.group.create_chat.successfully"
const val SEND_MESSAGE_TO_GROUP_CHAT_ACTION_RESPONSE = "chat.group.send_message.successfully"
const val DELETE_GROUP_CHAT_ACTION_RESPONSE = "chat.group.delete_chat.successfully"
const val UPDATE_MESSAGE_IN_GROUP_CHAT_ACTION_RESPONSE = "chat.group.edit_message.successfully"
const val DELETE_MESSAGES_FROM_GROUP_CHAT_ACTION_RESPONSE = "chat.group.delete_messages.successfully"

const val ADD_MEMBER_TO_GROUP_CHAT_ACTION_RESPONSE = "chat.group.add_member"
const val REMOVE_MEMBER_FROM_GROUP_CHAT_ACTION_RESPONSE = "chat.group.delete_member"

/**Error chat responses **/




