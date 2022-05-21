package com.example.pmd2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore


class NewTaskFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.notes_bottom_menu, container, false)

    }


    var database = FirebaseFirestore.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button1 = view.findViewById<TextView>(R.id.add_task_action_ok)
        val button2 = view.findViewById<TextView>(R.id.add_task_action_cancel)


//        button1.setOnClickListener {
//            Log.d("gg", "ok")
//
//            dialog?.setContentView(R.layout.fragment_new_task)
////            database = FirebaseDatabase.getInstance().getReference("Tasks")
//            binding = FragmentNewTaskBinding.bind(view)
////            activity?.setContentView(R.layout.fragment_custom_dialog)
//
//            val taskId = null
//            val text = binding.input.text.toString()
//            val task = Tasks(text, false)
//
//
//            database.collection("tasks").add(task).addOnSuccessListener{ documentReference ->
//                Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
////                Toast.makeText(activity,"Successfully Saved",Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener { e ->
//                    Log.w("TAG", "Error adding document", e)
//                }
//
////            val taskId = database.push().key
////            val text = binding.input.text.toString()
////            val task = Tasks(text, false)
////
////            if (taskId != null) {
////                database.child(taskId).setValue(task).addOnSuccessListener {
////                    //                    binding.input.text.clear()
////                    Log.i("firebase", "Got value $taskId $task")
////                    //                        Toast.makeText(activity,"Successfully Saved",Toast.LENGTH_SHORT).show()
////                }.addOnFailureListener {
////                    Log.e("firebase", "Error getting data", it)
////                }
////            }
//
//
//            dialog?.dismiss()
//
//
//        }




//
//        button2.setOnClickListener {
//            Log.d("gg", "cencel")
//
//        }
    }

}