package com.foxnotes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foxnotes.R
import com.foxnotes.firestore_data.Notes

class NotesAdapter(private val taskList: ArrayList<Notes>) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {


    private lateinit var mListener: OnItemClickListener
    private lateinit var dListener: OnItemLongClickListener

    interface OnItemClickListener {

        fun onItemClick(position: Int)

    }

    interface OnItemLongClickListener {

        fun onItemLongClick(position: Int)

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    fun setOnItemLongClickListener(listener1: OnItemLongClickListener) {
        dListener = listener1
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notes_item, parent, false)
        return NotesViewHolder(itemView, mListener, dListener)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val task: Notes = taskList[position]
        holder.title.text = task.title
        holder.note.text = task.noteText
    }


    override fun getItemCount(): Int {
        return taskList.size
    }

    class NotesViewHolder(
        itemView: View,
        listener: OnItemClickListener,
        listener1: OnItemLongClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val note: TextView = itemView.findViewById(R.id.noteText)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
            itemView.setOnLongClickListener {
                listener1.onItemLongClick(bindingAdapterPosition)
                return@setOnLongClickListener true
            }

        }

    }
}