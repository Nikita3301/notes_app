package com.example.pmd2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmd2.databinding.FragmentTasksBinding
import com.example.pmd2.firestore.Tasks
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
    var db =  Firebase.firestore

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
        binding.newTaskButton.setOnClickListener {
            Log.d("tasks", "new task")
            val dialog = AddTaskFragment()
            dialog.show(parentFragmentManager, "new task dialog")
        }

        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db.firestoreSettings = settings


        recyclerView = view.findViewById(R.id.items_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        taskArrayList = arrayListOf()
        eventChangeListener()
        tasksAdapter = TasksAdapter(taskArrayList)
        recyclerView.adapter = tasksAdapter



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

        tasksAdapter.setOnItemLongClickListener(object : TasksAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                Log.d("nyyy", "Click $position -> ${taskArrayList[position].taskId}")
                MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                    .setTitle(resources.getString(R.string.delete_dialog_title))
//                    .setMessage(resources.getString(R.string.supporting_text))
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

        tasksAdapter.setOnItemCheckedListener(object : TasksAdapter.OnItemCheckedListener {
            override fun onItemCheck(position: Int) {
                val query = db.collection("users").document(uid.toString()).collection("tasks")
                    .document(taskArrayList[position].taskId.toString())
                var listener: ListenerRegistration? = null
                if (taskArrayList[position].done) {
                    Log.d("check", "true")

                    listener = query.addSnapshotListener{ snapshots, e ->
                        if (e != null) {
                            Log.w("ContentValues", "Listen error", e)
                        }
                        snapshots?.reference?.update("done", false)
                        listener?.remove()
                    }

                } else if (!taskArrayList[position].done) {
                    Log.d("check", "false")

                    listener = query.addSnapshotListener{ snapshots, e ->
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


    private fun setToolbar() {
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



