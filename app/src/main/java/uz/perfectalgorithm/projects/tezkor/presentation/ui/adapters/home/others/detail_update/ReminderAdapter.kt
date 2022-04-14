package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.databinding.ItemReminderNoteBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock


class ReminderAdapter(
    private val onDeleteReminder: SingleBlock<NoteReminder>,
    private var isEditorMode: Boolean = false,
) : ListAdapter<NoteReminder, ReminderAdapter.VH>(DIFF_UTIL) {

    fun changeMode(isEditorMode: Boolean) {
        this.isEditorMode = isEditorMode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemReminderNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(val binding: ItemReminderNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: NoteReminder) = with(binding) {
            reminderDate.text = reminder.name
            ivDeleteReminder.visibility = if (isEditorMode) View.VISIBLE else View.INVISIBLE
            ivDeleteReminder.setOnClickListener {
                if (isEditorMode) {
                    onDeleteReminder(reminder)
                }
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<NoteReminder>() {
            override fun areItemsTheSame(
                oldItem: NoteReminder,
                newItem: NoteReminder,
            ) = newItem.minutesTime == oldItem.minutesTime

            override fun areContentsTheSame(
                oldItem: NoteReminder,
                newItem: NoteReminder,
            ) = oldItem.hashCode() == newItem.hashCode()
        }
    }
}