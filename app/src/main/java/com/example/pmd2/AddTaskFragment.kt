package com.example.pmd2

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.pmd2.databinding.FragmentAddTaskBinding
import com.example.pmd2.room.Database
import com.example.pmd2.room.TasksEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class AddTaskFragment : BottomSheetDialogFragment() {

//    private lateinit var auth: FirebaseAuth
//    private var uid = Firebase.auth.currentUser?.uid
//    var db = FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentAddTaskBinding
    private val tasksDatabase by lazy { Database.getDatabase(requireContext()).tasksDao() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddTaskBinding.bind(view)
        binding.addTaskTitleInput.setOnFocusChangeListener { _, _ ->
            if (binding.addTaskTitleInput.text.isEmpty()) {
                binding.titleLayout.error = "Enter title"
            } else {
                binding.titleLayout.error = null
            }
        }
        addTask()

        binding.addTaskActionCancel.setOnClickListener {
            dialog?.dismiss()

        }
    }


    private fun addTask() {
        binding.addTaskActionOk.setOnClickListener {
            val taskId = null
            val title = binding.addTaskTitleInput.text.toString()
            val taskDate = java.sql.Timestamp(System.currentTimeMillis()).toString()
            val description = binding.addTaskDescriptionInput.text.toString()
            val task = TasksEntity(taskId, taskDate,  title, description, false)

            if (binding.addTaskTitleInput.text.isEmpty()) {
                binding.titleLayout.error = "Enter title"
            } else {
                lifecycleScope.launch {
                    tasksDatabase.addTask(task)
                    dialog?.dismiss()
                }
            }

        }


    }
}