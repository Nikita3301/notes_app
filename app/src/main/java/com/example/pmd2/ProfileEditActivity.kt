package com.example.pmd2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pmd2.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user = Firebase.auth.currentUser
    lateinit var binding: ActivityProfileEditBinding
    private lateinit var url: String
    private lateinit var storageReference : StorageReference

    companion object{
        private const val PICK_IMAGE_CODE = 1000
        var clicked = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE){
            val uploadTask = storageReference.putFile(data!!.data!!)
            uploadTask.continueWithTask{
                task ->
                if (!task.isSuccessful){
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUri = task.result
                    clicked = true
                    url = downloadUri!!.toString().substring(0, downloadUri.toString().indexOf("&token"))
                    Log.d("link", url)
                    Picasso.with(this).load(url).into(binding.profileEditImg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this, "Profile Edit", Toast.LENGTH_SHORT).show()
        auth = Firebase.auth

        getUserProfile()
        val user = Firebase.auth.currentUser
        storageReference = FirebaseStorage.getInstance().getReference("/users/${user?.uid}/profileImg")


        binding.buttonLoadPicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_CODE)
        }

        binding.saveProfileButton.setOnClickListener {
            updateProfile()
        }

    }

    private fun getUserProfile() {
        val user = Firebase.auth.currentUser
        user?.let {
            binding.profileEditUsername.setText(user.displayName)
            binding.profileEditEmail.setText(user.email)
            Picasso.with(this).load(user.photoUrl).into(binding.profileEditImg)
        }
    }


    private fun updateProfile(){
        val profileUpdates = userProfileChangeRequest {
            displayName = binding.profileEditUsername.text.toString()
            if (clicked){
                photoUri = Uri.parse(url)
                Log.d("edit", photoUri.toString())
            }
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User profile updated.")
                }
                openProfile()
            }
    }


    private fun openProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
    }


}