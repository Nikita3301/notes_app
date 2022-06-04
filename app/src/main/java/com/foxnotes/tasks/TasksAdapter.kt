package com.foxnotes.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foxnotes.R
import com.foxnotes.firestore_data.Tasks

class TasksAdapter(private val taskList: ArrayList<Tasks>) :
    RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {


    private lateinit var mListener: OnItemClickListener
    private lateinit var dListener: OnItemLongClickListener
    private lateinit var cListener: OnItemCheckedListener

    interface OnItemClickListener {

        fun onItemClick(position: Int)

    }

    interface OnItemLongClickListener {

        fun onItemLongClick(position: Int)

    }

    interface OnItemCheckedListener {
        fun onItemCheck(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    fun setOnItemLongClickListener(listener1: OnItemLongClickListener) {
        dListener = listener1
    }

    fun setOnItemCheckedListener(listener2: OnItemCheckedListener) {
        cListener = listener2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tasks_item, parent, false)
        return TasksViewHolder(itemView, mListener, dListener, cListener)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task: Tasks = taskList[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.done.isChecked = task.done
//        Log.d("done", "Click $position -> ${task.done}")
    }


    override fun getItemCount(): Int {
        return taskList.size
    }

    class TasksViewHolder(
        itemView: View,
        listener: OnItemClickListener,
        listener1: OnItemLongClickListener,
        listener2: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val done: CheckBox = itemView.findViewById(R.id.done)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
            itemView.setOnLongClickListener {
                listener1.onItemLongClick(bindingAdapterPosition)
                return@setOnLongClickListener true
            }
            done.setOnClickListener {
                listener2.onItemCheck(bindingAdapterPosition)
            }


        }

    }
}