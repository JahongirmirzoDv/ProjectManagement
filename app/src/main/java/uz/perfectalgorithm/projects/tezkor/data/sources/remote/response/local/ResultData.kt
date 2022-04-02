package uz.star.domvet.utils.models.response.local

import android.content.Context
import androidx.annotation.StringRes
import uz.star.mardex.model.results.local.MessageData

/**
 * Created by Sherzodbek Muhammadiev on 20.01.2020
 */

sealed class ResultData<T> {
    internal class Data<T> internal constructor(val data: T) : ResultData<T>()
    internal class Message<T> internal constructor(val data: MessageData) : ResultData<T>()

    fun isMessageData(): Boolean = this is Message

    fun getMessageOrNull(): String? = (this as? Message)?.data?.getMessageOrNull()
    fun getMessageDataOrNull(): MessageData? = (this as? Message)?.data
    fun getResourceOrNull(): Int? = (this as? Message)?.data?.getResourceOrNull()
    fun getFailureOrNull(): Throwable? = (this as? Message)?.data?.getFailureOrNull()
    fun getDataOrNull() = (this as? Data<T>)?.data

    inline fun onMessage(f: (String) -> Unit): ResultData<T> {
        getMessageOrNull()?.let { f(it) }
        return this
    }

    inline fun onResource(f: (Int) -> Unit): ResultData<T> {
        getResourceOrNull()?.let { f(it) }
        return this
    }

    inline fun onFailure(f: (Throwable) -> Unit): ResultData<T> {
        getFailureOrNull()?.let { f(it) }
        return this
    }

    inline fun onData(f: (T) -> Unit): ResultData<T> {
        getDataOrNull()?.let { f(it) }
        return this
    }

    inline fun onMessageData(f: (MessageData) -> Unit): ResultData<T> {
        if (isMessageData()) {
            getMessageDataOrNull()?.let { f(it) }
        }
        return this
    }

    inline fun onResult(f: (Int?, String?, Throwable?, T?) -> Unit): ResultData<T> =
        onMessage { f(null, it, null, null) }.onResource { f(it, null, null, null) }
            .onFailure { f(null, null, it, null) }
            .onData { f(null, null, null, it) }

    fun onResultMessage(context: Context, f: (String) -> Unit): ResultData<T> =
        onMessage { f(it) }.onResource { f(context.getString(it)) }
            .onFailure { f(it.message.toString()) }

    companion object {
        fun <T> message(message: String): ResultData<T> = Message(MessageData.message(message))
        fun <T> messageData(data: MessageData): ResultData<T> = Message(data)
        fun <T> resource(@StringRes res: Int): ResultData<T> = Message(MessageData.resource(res))
        fun <T> failure(exception: Throwable): ResultData<T> =
            Message(MessageData.failure(exception))

        fun <T> data(data: T): ResultData<T> = Data(data)
    }

    override fun toString(): String {
        /* val text = when {
             isData() -> getDataOrNull().toString()
             isFailure() -> getFailureOrNull().toString()
             isMessage() -> getMessageOrNull().toString()
             isResource() -> getResourceOrNull().toString()
             else -> "Unknown"
         }*/
        return "text"
    }
}