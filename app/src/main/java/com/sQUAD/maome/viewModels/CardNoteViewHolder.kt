package com.sQUAD.maome.viewModels

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sQUAD.maome.databinding.CardNoteItemBinding

class CardNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var binding: CardNoteItemBinding

    val NoteCardMemoryNumber: TextView = binding.NoteCardMemoryNumber
    val NoteCardCreatedAt: TextView = binding.NoteCardCreatedAt
}