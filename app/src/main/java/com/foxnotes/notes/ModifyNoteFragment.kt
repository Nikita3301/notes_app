package com.foxnotes.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.foxnotes.firestore_data.Notes
import com.foxnotes.R
import com.foxnotes.databinding.FragmentModifyNoteBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import java.util.*

class ModifyNoteFragment(private val noteId: String, private val date: Date?) : DialogFragment() {

    private var uid = Firebase.auth.currentUser?.uid
    var db = FirebaseFirestore.getInstance()
    var listener: ListenerRegistration? = null
    private val query = db.collection("users").document(uid.toString()).collection("notes")
        .document(noteId)

    private lateinit var binding: FragmentModifyNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify_note, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialogSize()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentModifyNoteBinding.bind(view)

        getNoteData()
        saveIconVisibility()

        binding.toolbar.saveNote.setOnClickListener {
            modifyNote()
        }

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


    private fun getNoteData() {
        listener = query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("ContentValues", "Listen error", e)
            }
            snapshots?.reference?.get()
            val note = snapshots?.toObject(Notes::class.java)
            binding.modifyNoteTitle.setText(note?.title.toString())
            binding.modifyNoteText.setText(note?.noteText.toString())
            Log.d("TAG", "DocumentSnapshot data: ${snapshots?.data}")
            listener?.remove()
        }
    }

    private fun saveIconVisibility() {
        binding.toolbar.saveNote.visibility = View.GONE

        binding.modifyNoteTitle.doAfterTextChanged {
            if (binding.modifyNoteTitle.text?.isEmpty() == true) {
                binding.toolbar.saveNote.visibility = View.GONE
            } else {
                binding.toolbar.saveNote.visibility = View.VISIBLE

            }
        }
        binding.modifyNoteText.doAfterTextChanged {
            if (binding.modifyNoteText.text?.isEmpty() == true) {
                binding.toolbar.saveNote.visibility = View.GONE
            } else {
                binding.toolbar.saveNote.visibility = View.VISIBLE
            }
        }

    }

    private fun modifyNote() {
        val title = binding.modifyNoteTitle.text.toString()
        val noteText = binding.modifyNoteText.text.toString()
        val note = Notes(noteId, null, title, noteText)

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
