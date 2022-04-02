package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.note

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.databinding.ItemReminderNoteBinding
import uz.perfectalgorithm.projects.tezkor.utils.inVisible
import uz.perfectalgorithm.projects.tezkor.utils.visible

class NoteReminderAdapter(
    context: Context,
    private val listener: EventDeleteClickListener
) :
    ListAdapter<NoteReminder, NoteReminderAdapter.VH>(
        diffUtil
    ) {

    private var isVisibleDelete = false

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemReminderNoteBinding.inflate(inflater, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = currentList[position]

        holder.itemReminderNoteBinding.reminderDate.text = itemData.name

        holder.itemReminderNoteBinding.ivDeleteReminder.setOnClickListener {
            listener.deleteItem(itemData)
        }
        if (isVisibleDelete) {
            holder.itemReminderNoteBinding.ivDeleteReminder.visible()
        } else {
            holder.itemReminderNoteBinding.ivDeleteReminder.inVisible()
        }
    }

    fun changeImage(changeImage: Boolean) {
        isVisibleDelete = changeImage
        notifyDataSetChanged()
    }

    inner class VH(val itemReminderNoteBinding: ItemReminderNoteBinding) :
        RecyclerView.ViewHolder(itemReminderNoteBinding.root)


}

interface EventDeleteClickListener {
    fun deleteItem(reminder: NoteReminder)
}


private val diffUtil = object : DiffUtil.ItemCallback<NoteReminder>() {
    override fun areItemsTheSame(
        oldItem: NoteReminder,
        newItem: NoteReminder,
    ): Boolean = newItem.minutesTime == oldItem.minutesTime

    override fun areContentsTheSame(
        oldItem: NoteReminder,
        newItem: NoteReminder,
    ): Boolean = oldItem.hashCode() == newItem.hashCode()

}