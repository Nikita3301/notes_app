package com.example.pmd2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.pmd2.databinding.FragmentModifyTaskBinding
import com.example.pmd2.firestore.Tasks
import com.example.pmd2.room.Database
import com.example.pmd2.room.TasksEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*


class ModifyTaskFragment(private val taskId: String, private val date: Date?, private val done: Boolean) : BottomSheetDialogFragment() {


    private var uid = Firebase.auth.currentUser?.uid

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
    var db =  Firebase.firestore



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentModifyTaskBinding.bind(view)


        val query = db.collection("users").document(uid.toString()).collection("tasks")
            .document(taskId)
        var listener: ListenerRegistration? = null
        listener = query.addSnapshotListener{ snapshots, e ->
            if (e != null) {
                Log.w("ContentValues", "Listen error", e)
            }
            snapshots?.reference?.get()
            val task = snapshots?.toObject(Tasks::class.java)
            binding.modifyTaskTitleInput.setText(task?.title.toString())
            binding.modifyTaskDescriptionInput.setText(task?.description.toString())
            binding.modifyDate.text = task?.taskDate.toString()
            Log.d("TAG", "DocumentSnapshot data: ${snapshots?.data}")
            listener?.remove()
        }


        binding.modifyTaskTitleInput.doAfterTextChanged {
            if (binding.modifyTaskTitleInput.text?.isEmpty() == true) {
                binding.titleLayout.error = "Enter title"
            } else {
                binding.titleLayout.error = null
            }
        }

        binding.modifyTaskActionOk.setOnClickListener {
            val title = binding.modifyTaskTitleInput.text.toString()
            val description = binding.modifyTaskDescriptionInput.text.toString()
            val task = Tasks(taskId, date, title, description, done)

                if (binding.modifyTaskTitleInput.text?.isEmpty() == true){
                    binding.titleLayout.error = "Enter title"
                }else{
                    listener = query.addSnapshotListener{ snapshots, e ->
                        if (e != null) {
                            Log.w("ContentValues", "Listen error", e)
                        }
                        snapshots?.reference?.set(task)
                        listener?.remove()
                        dialog?.dismiss()
                    }

                }
            }

        binding.modifyTaskActionCancel.setOnClickListener {
            dialog?.dismiss()

        }
    }

}

