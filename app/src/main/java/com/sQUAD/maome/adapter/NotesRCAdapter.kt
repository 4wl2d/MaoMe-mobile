package com.sQUAD.maome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sQUAD.maome.R
import com.sQUAD.maome.databinding.CardNoteItemBinding
import com.sQUAD.maome.retrofit.main.Note

class NotesRCAdapter : ListAdapter<Note, NotesRCAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardNoteItemBinding.bind(view)

        fun bind(note: Note) = with(binding) {
            NoteCardCreatedAt.text = note.createdAt
            NoteCardMemoryNumber.text = note.id.toString()
        }
    }

    class Comparator : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_note_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}