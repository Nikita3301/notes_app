package com.foxnotes.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foxnotes.MainActivity
import com.foxnotes.R
import com.foxnotes.databinding.FragmentNotesBinding
import com.foxnotes.firestore_data.Notes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteArrayList: ArrayList<Notes>
    private lateinit var tempNoteArrayList: ArrayList<Notes>
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var binding: FragmentNotesBinding
    var db = Firebase.firestore

    private var uid = Firebase.auth.currentUser?.uid


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentNotesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        adapterInitialization()

        modifyNote()
        deleteNote()

        binding.addButton.setOnClickListener {
            val dialog = AddNoteFragment()
            dialog.show(parentFragmentManager, "add note dialog")
        }

    }


    private fun adapterInitialization(){
        recyclerView = requireView().findViewById(R.id.notes_items_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        noteArrayList = arrayListOf()
        tempNoteArrayList = arrayListOf()
        eventChangeListener()
        notesAdapter = NotesAdapter(noteArrayList)
        recyclerView.adapter = notesAdapter

    }

    private fun modifyNote(){
        notesAdapter.setOnItemClickListener(object : NotesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val dialog = ModifyNoteFragment(
                    noteArrayList[position].noteId.toString(), noteArrayList[position].noteDate
                )
                dialog.show(parentFragmentManager, "update task dialog")
            }

        })
    }

    private fun deleteNote(){
        notesAdapter.setOnItemLongClickListener(object : NotesAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                    .setTitle(resources.getString(R.string.delete_note_dialog_title))
                    .setNeutralButton(resources.getString(R.string.delete_dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.delete_dialog_accept)) { _, _ ->
                        val query =
                            db.collection("users").document(uid.toString()).collection("notes")
                                .document(noteArrayList[position].noteId.toString())
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
        (activity as MainActivity?)!!.menuToChoose = R.menu.notes_menu
        (activity as MainActivity?)!!.invalidateOptionsMenu()
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.setupDrawerToggle()
        (activity as MainActivity?)!!.navItemChecker()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.one_column -> {
                recyclerView.apply {
                    layoutManager = GridLayoutManager(activity, 1)
                }
                binding.toolbar.root.menu.findItem(R.id.one_column).isVisible = false
                binding.toolbar.root.menu.findItem(R.id.two_columns).isVisible = true
            }
            R.id.two_columns -> {
                recyclerView.apply {
                    layoutManager = GridLayoutManager(activity, 2)
                }
                binding.toolbar.root.menu.findItem(R.id.one_column).isVisible = true
                binding.toolbar.root.menu.findItem(R.id.two_columns).isVisible = false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun eventChangeListener() {
        db.collection("users").document(uid.toString()).collection("notes")
            .orderBy("noteDate", Query.Direction.DESCENDING)
            .addSnapshotListener() { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", e.message.toString())

                }
                for (dc: DocumentChange in snapshots?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        noteArrayList.add(dc.newIndex, dc.document.toObject(Notes::class.java))
                        notesAdapter.notifyItemInserted(dc.newIndex)
                        Log.d("event", "add ${dc.newIndex}")
                    }
                    if (dc.type == DocumentChange.Type.MODIFIED) {
                        if (dc.oldIndex == dc.newIndex) {
                            noteArrayList[dc.oldIndex] = dc.document.toObject(Notes::class.java)
                            notesAdapter.notifyItemChanged(dc.oldIndex)
                            Log.d("event", "mod ${dc.oldIndex}")
                        } else {
                            noteArrayList.removeAt(dc.oldIndex)
                            noteArrayList.add(
                                dc.newIndex,
                                dc.document.toObject(Notes::class.java)
                            )
                            notesAdapter.notifyItemMoved(dc.oldIndex, dc.newIndex)
                            Log.d("event", "mod ${dc.oldIndex} -> ${dc.newIndex}")
                        }
                    }

                    if (dc.type == DocumentChange.Type.REMOVED) {
                        noteArrayList.removeAt(dc.oldIndex)
                        notesAdapter.notifyItemRemoved(dc.oldIndex)
                        Log.d("event", "del ${dc.oldIndex}")
                    }
                }
            }

    }


}