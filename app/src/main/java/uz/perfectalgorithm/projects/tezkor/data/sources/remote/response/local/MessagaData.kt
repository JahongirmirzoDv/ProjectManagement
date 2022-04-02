package uz.star.mardex.model.results.local

import android.content.Context
import androidx.annotation.StringRes

/**
 * Created by Sherzodbek Muhammadiev on 20.01.2020
 */

sealed class MessageData {

    internal class Resource internal constructor(@StringRes val res: Int) : MessageData()
    internal class Message internal constructor(val message: String) : MessageData()
    internal class Failure internal constructor(val exception: Throwable) : MessageData()

    fun isMessage() = this is Message
    fun isResource() = this is Resource
    fun isFailure() = this is Failure

    fun getMessageOrNull(): String? = (this as? Message)?.message
    fun getResourceOrNull(): Int? = (this as? Resource)?.res
    fun getFailureOrNull(): Throwable? = (this as? Failure)?.exception

    inline fun onMessage(f: (String) -> Unit): MessageData {
        if (isMessage()) getMessageOrNull()?.let { f(it) }
        return this
    }

    inline fun onResource(f: (Int) -> Unit): MessageData {
        if (isResource()) getResourceOrNull()?.let { f(it) }
        return this
    }

    inline fun onFailure(f: (Throwable) -> Unit): MessageData {
        if (isFailure()) getFailureOrNull()?.let { f(it) }
        return this
    }

    inline fun onResult(f: (Int?, String?, Throwable?, Any?) -> Unit): MessageData =
        onMessage { f(null, it, null, null) }.onResource { f(it, null, null, null) }
            .onFailure { f(null, null, it, null) }

    inline fun onResultMessage(context: Context, f: (String) -> Unit): MessageData =
        onMessage { f(it) }.onResource { f(context.getString(it)) }
            .onFailure { f(it.message.toString()) }

    companion object {
        fun message(message: String): MessageData = Message(message)
        fun resource(@StringRes res: Int): MessageData = Resource(res)
        fun failure(exception: Throwable): MessageData = Failure(exception)
    }
}