package com.foxnotes.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.foxnotes.R
import com.foxnotes.databinding.FragmentAddNoteBinding
import com.foxnotes.firestore_data.Notes
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

class AddNoteFragment : DialogFragment() {
    private var uid = Firebase.auth.currentUser?.uid
    var db = FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentAddNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialogSize()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddNoteBinding.bind(view)

        saveIconVisibility()
        addNote()

        binding.toolbar.back.setOnClickListener {
            dialog?.dismiss()

        }
    }

    private fun dialogSize() {
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    private fun saveIconVisibility() {
        binding.toolbar.saveNote.visibility = View.GONE

        binding.addNoteTitle.doAfterTextChanged {
            if (binding.addNoteTitle.text?.isEmpty() == true) {
                binding.toolbar.saveNote.visibility = View.GONE
            } else {
                binding.toolbar.saveNote.visibility = View.VISIBLE

            }
        }
        binding.addNoteText.doAfterTextChanged {
            if (binding.addNoteText.text?.isEmpty() == true) {
                binding.toolbar.saveNote.visibility = View.GONE
            } else {
                binding.toolbar.saveNote.visibility = View.VISIBLE

            }
        }
    }


    private fun addNote() {
        binding.toolbar.saveNote.setOnClickListener {
            val noteId =
                db.collection("users").document(uid.toString()).collection("notes").document().id
            val title = binding.addNoteTitle.text.toString()
            val noteText = binding.addNoteText.text.toString()
            val note = Notes(noteId, null, title, noteText)

            val query = db.collection("users").document(uid.toString()).collection("notes")
                .document(noteId)
            var listener: ListenerRegistration? = null
            listener = query.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ContentValues", "Listen error", e)
                }
                snapshots?.reference?.set(note)
                listener?.remove()
                dialog?.dismiss()
            }
        }
    }
}
