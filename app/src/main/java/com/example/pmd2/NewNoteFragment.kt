package com.example.pmd2

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment


class NewNoteFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view: View = inflater.inflate(R.layout.fragment_new_notes, container, false)
//        (activity as MainActivity?)!!.onCreateNewNote()


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.inflateMenu(R.menu.login_menu)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        (activity as MainActivity?)!!.setupDrawerToggle()
        (activity as MainActivity?)!!.navItemChecker()
//        val button = view.findViewById(R.id.button_sheet) as Button
//        button.setOnClickListener{showDialog()}
    }

//    private fun showDialog(){
//        val intent = Intent (activity, ::class.java)
//        getActivity().startActivity(intent)
//
//    }


//    override fun onClick(p0: View?) {
//        when (p0?.id){
//            R.id.button_sheet -> {
//                val dialog = BottomSheetDialog(this)
//            }
//        }
//    }

//    private fun showBottomSheetDialog() {
//        val bottomSheetDialog = BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
//        val copy = bottomSheetDialog.findViewById<LinearLayout>(R.id.copyLinearLayout)
//        val share = bottomSheetDialog.findViewById<LinearLayout>(R.id.shareLinearLayout)
//        val upload = bottomSheetDialog.findViewById<LinearLayout>(R.id.uploadLinearLayout)
//        val download = bottomSheetDialog.findViewById<LinearLayout>(R.id.download)
//        val delete = bottomSheetDialog.findViewById<LinearLayout>(R.id.delete)
//        bottomSheetDialog.show()
//    }




}