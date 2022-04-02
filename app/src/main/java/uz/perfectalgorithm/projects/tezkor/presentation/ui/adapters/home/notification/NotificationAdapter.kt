package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.notification

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.*
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemNotificationBinding
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

class NotificationAdapter(
    context: Context,
    private val listener: NotificationItemClickListener
) :
    PagingDataAdapter<NotificationResponse.NotificationData, NotificationAdapter.VH>(
        diffUtil
    ) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemNotificationBinding.inflate(inflater, parent, false))


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = getItem(position)
        val dateTime = DateTime(getItem(position)?.time ?: "")
        var anatation = "kun"
        var date = Days.daysBetween(dateTime, DateTime.now()).days
        if (date == 0) {
            date = Hours.hoursBetween(dateTime, DateTime.now()).hours
            anatation = "soat"
        }
        if (date == 0) {
            date = Minutes.minutesBetween(dateTime, DateTime.now()).minutes
            anatation = "min"
        }
        if (date == 0) {
            date = Seconds.secondsBetween(dateTime, DateTime.now()).seconds
            anatation = "sekund"
        }
//        val date1 = DateTime(getItem(position)?.time ?: "")
//        val date2 = DateTime(getItem(position)?.time ?: "")
//        val editDate = DateTime(updateDate)
//        Log.d("CCCCCC", "$date1 $date2 $editDate")
//        if (editDate > date1 && date2 > editDate) {
//            holder.itemNotificationBinding.textNew.visible()
//        }

        when (itemData?.process) {
            "create" -> {
                holder.itemNotificationBinding.ivNotification.setImageResource(R.drawable.ic_new)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} yaratildi </b> \t $date $anatation",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} yaratildi </b>\t $date $anatation"
                    )
                }
            }
            "finish" -> {
                holder.itemNotificationBinding.ivNotification.setImageResource(R.drawable.ic_check_circle)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} tugadi </b>\t $date $anatation",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} tugadi </b>\t $date $anatation"
                    )
                }
            }
            "start" -> {
                holder.itemNotificationBinding.ivNotification.setImageResource(R.drawable.ic_play_circle)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} boshlandi </b>\t $date $anatation",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} boshlandi </b>\t $date $anatation"
                    )
                }
            }
            "update" -> {
                holder.itemNotificationBinding.ivNotification.setImageResource(R.drawable.ic_circle_edit)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} yangilandi </b> $date $anatation",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    holder.itemNotificationBinding.ivDescription.text = Html.fromHtml(
                        "${itemData.title} <font color=black> <b> ${changeTypeLang(itemData.type)} yangilandi </b> $date $anatation"
                    )
                }
            }
        }
        holder.itemNotificationBinding.mainContainer.setOnClickListener {
            listener.itemClick(itemData)
        }
    }

    private fun changeTypeLang(type: String): String {
        return when (type) {
            "task" -> "vazifa"
            "dating" -> "uchrashuv"
            "meeting" -> "majlis"
            "note" -> "eslatma"
            else -> ""
        }
    }

    class VH(val itemNotificationBinding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(itemNotificationBinding.root)
}

interface NotificationItemClickListener {
    fun itemClick(event: NotificationResponse.NotificationData?)
}

private val diffUtil = object : DiffUtil.ItemCallback<NotificationResponse.NotificationData>() {
    override fun areContentsTheSame(
        oldItem: NotificationResponse.NotificationData,
        newItem: NotificationResponse.NotificationData,
    ): Boolean = false

    override fun areItemsTheSame(
        oldItem: NotificationResponse.NotificationData,
        newItem: NotificationResponse.NotificationData,
    ): Boolean = false

}