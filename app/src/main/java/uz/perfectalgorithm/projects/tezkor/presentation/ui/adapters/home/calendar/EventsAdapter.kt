package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemCalendarEventBinding
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.hexColor

class EventsAdapter(
    context: Context,
    private val listener: EventItemClickListener
) :
    androidx.recyclerview.widget.ListAdapter<CalendarResponse.Event, EventsAdapter.VH>(
        diffUtil
    ) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemCalendarEventBinding.inflate(inflater, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = currentList[position]

        holder.itemEventsBinding.tvTitle.text = itemData.title
        holder.itemEventsBinding.tvDate.text =
            "${DateTime(itemData.startTime).toString(Formatter.SIMPLE_TIME_PATTERN)}-${
                DateTime(itemData.endTime).toString(Formatter.SIMPLE_TIME_PATTERN)
            }"
        holder.itemEventsBinding.mainContainer.apply {
            val drawable = this.background as GradientDrawable
            drawable.setColor(Color.parseColor(context.hexColor(itemData.type)))
        }

        holder.itemEventsBinding.mainContainer.setOnClickListener {
            listener.itemClick(itemData)
        }
    }

    class VH(val itemEventsBinding: ItemCalendarEventBinding) :
        RecyclerView.ViewHolder(itemEventsBinding.root)
}

interface EventItemClickListener {
    fun itemClick(event: CalendarResponse.Event)
}

private val diffUtil = object : DiffUtil.ItemCallback<CalendarResponse.Event>() {
    override fun areItemsTheSame(
        oldItem: CalendarResponse.Event,
        newItem: CalendarResponse.Event,
    ): Boolean = oldItem.idLocalBase == newItem.idLocalBase

    override fun areContentsTheSame(
        oldItem: CalendarResponse.Event,
        newItem: CalendarResponse.Event,
    ): Boolean = oldItem.hashCode() == newItem.hashCode()

}