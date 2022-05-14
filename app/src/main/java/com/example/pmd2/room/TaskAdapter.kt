package com.example.pmd2.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pmd2.databinding.RawItemBinding


class TaskAdapter() : ListAdapter<TasksEntity, TaskAdapter.TaskHolder>(DiffCallback()) {

    class TaskHolder(var viewBinding: RawItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val binding =
            RawItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val task = TaskHolder(binding)

        task.viewBinding.task.setOnLongClickListener {
            listener.onItemLongRemoveClick(task.adapterPosition)
            return@setOnLongClickListener true
        }

        task.viewBinding.done.setOnClickListener {
            listener.onDoneClick(task.adapterPosition)
        }

        task.viewBinding.task.setOnClickListener {
            listener.onItemClick(task.adapterPosition)
        }
        return task
    }


    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val currentItem = getItem(position)
        holder.viewBinding.title.text = currentItem.title
        holder.viewBinding.description.text = currentItem.description
        holder.viewBinding.done.isChecked = currentItem.done
    }


    class DiffCallback : DiffUtil.ItemCallback<TasksEntity>() {
        override fun areItemsTheSame(oldItem: TasksEntity, newItem: TasksEntity) =
            oldItem.taskDate == newItem.taskDate

        override fun areContentsTheSame(oldItem: TasksEntity, newItem: TasksEntity) =
            oldItem == newItem

    }
}