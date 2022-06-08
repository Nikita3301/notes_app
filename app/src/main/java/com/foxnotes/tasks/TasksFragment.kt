package com.foxnotes.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foxnotes.MainActivity
import com.foxnotes.R
import com.foxnotes.databinding.FragmentTasksBinding
import com.foxnotes.firestore_data.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TasksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskArrayList: ArrayList<Tasks>
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var binding: FragmentTasksBinding
    var db = Firebase.firestore

    private var uid = Firebase.auth.currentUser?.uid


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
        adapterInitialization()

        binding.newTaskButton.setOnClickListener {
            Log.d("tasks", "new task")
            val dialog = AddTaskFragment()
            dialog.show(parentFragmentManager, "new task dialog")
        }

        modifyTask()
        deleteTask()
        changeTaskState()


    }


    private fun adapterInitialization() {
        recyclerView = requireView().findViewById(R.id.task_items_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        taskArrayList = arrayListOf()
        eventChangeListener()
        tasksAdapter = TasksAdapter(taskArrayList)
        recyclerView.adapter = tasksAdapter
    }

    private fun modifyTask() {
        tasksAdapter.setOnItemClickListener(object : TasksAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("recycle", "Click $position -> ${taskArrayList[position].taskId}")

                val dialog = ModifyTaskFragment(
                    taskArrayList[position].taskId.toString(), taskArrayList[position].taskDate,
                    taskArrayList[position].done
                )
                dialog.show(parentFragmentManager, "update task dialog")
            }
        })
    }

    private fun changeTaskState() {

        tasksAdapter.setOnItemCheckedListener(object : TasksAdapter.OnItemCheckedListener {
            override fun onItemCheck(position: Int) {
                val query = db.collection("users").document(uid.toString()).collection("tasks")
                    .document(taskArrayList[position].taskId.toString())
                var listener: ListenerRegistration? = null
                if (taskArrayList[position].done) {
                    Log.d("check", "true")

                    listener = query.addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            Log.w("ContentValues", "Listen error", e)
                        }
                        snapshots?.reference?.update("done", false)
                        listener?.remove()
                    }

                } else if (!taskArrayList[position].done) {
                    Log.d("check", "false")

                    listener = query.addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            Log.w("ContentValues", "Listen error", e)
                        }
                        snapshots?.reference?.update("done", true)
                        listener?.remove()
                    }

                }
            }
        })
    }

    private fun deleteTask() {
        tasksAdapter.setOnItemLongClickListener(object : TasksAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                    .setTitle(resources.getString(R.string.delete_task_dialog_title))
                    .setNeutralButton(resources.getString(R.string.delete_dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.delete_dialog_accept)) { _, _ ->
                        val query =
                            db.collection("users").document(uid.toString()).collection("tasks")
                                .document(taskArrayList[position].taskId.toString())
                        var listener: ListenerRegistration? = null
                        listener = query.addSnapshotListener { snapshots, e ->
                            if (e != null) {
                                Log.w("ContentValues", "Listen error", e)
                            }
                            snapshots?.reference?.delete()
                            listener?.remove()
                        }
                    }.show()
            }
        })
    }

    private fun setToolbar() {
        (activity as MainActivity?)!!.menuToChoose = R.menu.main_menu
        (activity as MainActivity?)!!.invalidateOptionsMenu()
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.setupDrawerToggle()
        (activity as MainActivity?)!!.navItemChecker()
    }

    private fun eventChangeListener() {
        db.collection("users").document(uid.toString()).collection("tasks")
            .orderBy("done", Query.Direction.ASCENDING)
            .orderBy("taskDate", Query.Direction.DESCENDING)
            .addSnapshotListener() { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", e.message.toString())

                }
                for (dc: DocumentChange in snapshots?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        taskArrayList.add(dc.newIndex, dc.document.toObject(Tasks::class.java))
                        tasksAdapter.notifyItemInserted(dc.newIndex)
                        Log.d("event", "add ${dc.newIndex}")
                    }
                    if (dc.type == DocumentChange.Type.MODIFIED) {
                        if (dc.oldIndex == dc.newIndex) {
                            taskArrayList[dc.oldIndex] = dc.document.toObject(Tasks::class.java)
                            tasksAdapter.notifyItemChanged(dc.oldIndex)
                            Log.d("event", "mod ${dc.oldIndex}")
                        } else {
                            taskArrayList.removeAt(dc.oldIndex)
                            taskArrayList.add(
                                dc.newIndex,
                                dc.document.toObject(Tasks::class.java)
                            )
                            tasksAdapter.notifyItemMoved(dc.oldIndex, dc.newIndex)
                            Log.d("event", "mod ${dc.oldIndex} -> ${dc.newIndex}")
                        }
                    }

                    if (dc.type == DocumentChange.Type.REMOVED) {
                        taskArrayList.removeAt(dc.oldIndex)
                        tasksAdapter.notifyItemRemoved(dc.oldIndex)
                        Log.d("event", "del ${dc.oldIndex}")
                    }
                }
            }
    }


}



