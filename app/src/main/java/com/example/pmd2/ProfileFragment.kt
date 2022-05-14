package com.example.pmd2

import android.content.ClipData
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
//import com.mikhaellopez.circularimageview.CircularImageView
import java.io.File


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var user = Firebase.auth.currentUser
    private val fireStore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
//        view?.let { checkCurrentUser(it) }
        setHasOptionsMenu(true)
        Toast.makeText(activity, "Profile", Toast.LENGTH_SHORT).show()
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logout = view.findViewById<Button>(R.id.logout)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = Firebase.auth.currentUser
            val userId = user?.uid.toString()
            Log.d("user", "in system $userId")
            getUserProfile(view)

            logout.setOnClickListener {
                signOut()
//                checkCurrentUser(view)
                updateUI(user)
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("activity", "login")
                startActivity(intent)
            }
        } else {
            Log.d("user", "not in system")
            val intent = Intent(activity, LoginActivity::class.java)
            (activity as MainActivity?)!!.startActivity(intent)
        }


        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

//        toolbar.inflateMenu(R.menu.edit_profile)

        (activity as MainActivity?)!!.menuToChoose = R.menu.edit_profile
        (activity as MainActivity?)!!.invalidateOptionsMenu()


        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        (activity as MainActivity?)!!.setupDrawerToggle()
        (activity as MainActivity?)!!.navItemChecker()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile_menu_icon -> {
                val intent = Intent(activity, ProfileEditActivity::class.java)
                (activity as MainActivity?)!!.startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserProfile(view: View) {
        val username = view.findViewById<EditText>(R.id.profile_username)
        val email = view.findViewById<EditText>(R.id.profile_email)
        val img = view.findViewById<ShapeableImageView>(R.id.profile_img)
//        val storage = Firebase.storage
//        val imgName = "9b40b9980b9c91b36923da7122a6513f"
        Log.d("user1", "system")

        val user = Firebase.auth.currentUser
        user?.let {
//            user.displayName?.let { it1 -> Log.d("bb", it1) }
//            user.email?.let { it2 -> Log.d("bb", it2) }
            username.setText(user.displayName)
            email.setText(user.email)
            if (user.photoUrl != null){
                Picasso.with(requireContext()).load(user.photoUrl.toString()).into(img)
            }


        }
    }


    private fun signOut() {
        Firebase.auth.signOut()
    }


    private fun deleteUser() {
        val user = Firebase.auth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User account deleted.")
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {
    }


}