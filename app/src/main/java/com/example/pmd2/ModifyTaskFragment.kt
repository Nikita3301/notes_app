package com.example.pmd2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.pmd2.databinding.FragmentModifyTaskBinding
import com.example.pmd2.room.Database
import com.example.pmd2.room.TasksEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class ModifyTaskFragment(var taskList: MutableList<TasksEntity>, var position: Int) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify_task, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog)
    }

    private lateinit var binding: FragmentModifyTaskBinding


    private val tasksDatabase by lazy { Database.getDatabase(requireContext()).tasksDao() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentModifyTaskBinding.bind(view)



        binding.modifyTaskTitleInput.setText(taskList[position].title)
        binding.modifyTaskDescriptionInput.setText(taskList[position].description)


        binding.modifyTaskTitleInput.setOnFocusChangeListener { _, _ ->
            if (binding.modifyTaskTitleInput.text.isEmpty()) {
                binding.titleLayout.error = "Enter title"
            } else {
                binding.titleLayout.error = null
            }
        }


        binding.modifyTaskActionOk.setOnClickListener {
            val newTitle = binding.modifyTaskTitleInput.text.toString()
            val newDescription = binding.modifyTaskDescriptionInput.text.toString()
            val task = TasksEntity(taskList[position].taskId, taskList[position].taskDate, newTitle, newDescription, taskList[position].done)
            lifecycleScope.launch {
                tasksDatabase.update(task)
                Log.d("ok", "updated $task")
            }
            dialog?.dismiss()

        }


        binding.modifyTaskActionCancel.setOnClickListener {
            dialog?.dismiss()

        }
    }

}