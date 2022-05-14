package com.example.pmd2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pmd2.databinding.FragmentTasksBinding
import com.example.pmd2.room.Database
import com.example.pmd2.room.RecyclerClickListener
import com.example.pmd2.room.TaskAdapter
import com.example.pmd2.room.TasksEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class TasksFragment : Fragment() {

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var taskArrayList: ArrayList<Tasks>
//    private lateinit var tasksAdapter: TasksAdapter
//    private lateinit var db: FirebaseFirestore

    private lateinit var auth: FirebaseAuth
    private var uid = Firebase.auth.currentUser?.uid

    private val tasksDatabase by lazy { Database.getDatabase(requireContext()).tasksDao() }
    private lateinit var binding: FragmentTasksBinding
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Page", "Task")
        binding = FragmentTasksBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()

        setRoomFunctions()
        observeRoomTasks()


        binding.newTaskButton.setOnClickListener {
            Log.d("tasks", "new task")
            val dialog = AddTaskFragment()
            dialog.show(parentFragmentManager, "new task dialog")
        }

    }


    private fun setToolbar() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.setupDrawerToggle()
        (activity as MainActivity?)!!.navItemChecker()
    }


    private fun observeRoomTasks() {
        lifecycleScope.launch {
            tasksDatabase.allTasks().collect { tasksList ->
                if (tasksList.isNotEmpty()) {
                    adapter.submitList(tasksList)
                }
            }
        }
    }

    private fun setRoomFunctions() {
        binding.itemsList.layoutManager = LinearLayoutManager(requireContext())
        binding.itemsList.setHasFixedSize(true)
        adapter = TaskAdapter()
        adapter.setItemListener(object : RecyclerClickListener {

            override fun onItemLongRemoveClick(position: Int) {
                MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                    .setTitle(resources.getString(R.string.delete_dialog_title))
                    .setNeutralButton(resources.getString(R.string.delete_dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.delete_dialog_accept)) { _, _ ->
                        val notesList = adapter.currentList.toMutableList()
                        val noteText = notesList[position].taskId
                        val removeNote = TasksEntity(noteText)
                        lifecycleScope.launch {
                            tasksDatabase.deleteTask(removeNote)
                        }
                    }.show()

            }

            override fun onItemClick(position: Int) {
                val notesList = adapter.currentList.toMutableList()
                val id = notesList[position].taskId
                Log.d("task", "edit $id")
                val dialog = ModifyTaskFragment(notesList, position)
                dialog.show(parentFragmentManager, "custom dialog")
            }

            override fun onDoneClick(position: Int) {
                val notesList = adapter.currentList.toMutableList()
                if (notesList[position].done) {
                    Log.d("check", "true")
                    lifecycleScope.launch {
                        tasksDatabase.updateTaskDone(false, notesList[position].taskId!!)
                    }

                } else if (!notesList[position].done) {
                    Log.d("check", "false")
                    lifecycleScope.launch {
                        tasksDatabase.updateTaskDone(true, notesList[position].taskId!!)
                    }
                }
            }
        })
        binding.itemsList.adapter = adapter
    }

}
