package com.example.pmd2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
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

//    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
//    { result: ActivityResult ->
//        if (result.resultCode == PICK_IMAGE_CODE){
//            val uploadTask = storageReference.putFile(result.data!!.data!!)
//            uploadTask.continueWithTask{
//                    task ->
//                if (!task.isSuccessful){
//                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//                }
//                storageReference.downloadUrl
//            }.addOnCompleteListener { task ->
//                if (task.isSuccessful){
//                    val downloadUri = task.result
//                    clicked = true
//                    url = downloadUri!!.toString().substring(0, downloadUri.toString().indexOf("&token"))
//                    Log.d("link", url)
//                    Picasso.with(this).load(url).into(binding.profileEditImg)
//                }
//            }
//        }
//    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this, "Profile Edit", Toast.LENGTH_SHORT).show()
        auth = Firebase.auth

        getUserProfile()
        val user = Firebase.auth.currentUser
        storageReference = FirebaseStorage.getInstance().getReference("/users/${user?.uid}")


        binding.buttonLoadPicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
//            getResult.launch(Intent.createChooser(intent, "Select Picture"))
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_CODE)
        }

        binding.profileEditUsername.doAfterTextChanged {
            if (binding.profileEditUsername.text.toString().isEmpty()){
                binding.usernameTextField.error = "Enter username"
            }else {
                binding.usernameTextField.error = null
            }
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
        if (binding.profileEditUsername.text.toString().isEmpty()){
            binding.usernameTextField.error = "Enter username"
        }else{
            binding.usernameTextField.error = null
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




    }


    private fun openProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
    }



//    fun uploadImage(context: Context, imageFileUri: Uri) {
//        mProgressDialog = ProgressDialog(context)
//        mProgressDialog.setMessage("Please wait, image being upload")
//        mProgressDialog.show()
//        val date = Date()
//        val uploadTask = mStorageRef.child("posts/${date}.png").putFile(imageFileUri)
//        uploadTask.addOnSuccessListener {
//            Log.e("Frebase", "Image Upload success")
//            mProgressDialog.dismiss()
//            val uploadedURL = mStorageRef.child("posts/${date}.png").downloadUrl
//            Log.e("Firebase", "Uploaded $uploadedURL")
//        }.addOnFailureListener {
//            Log.e("Frebase", "Image Upload fail")
//            mProgressDialog.dismiss()
//        }
//    }



//    private fun selectImage() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            if(data == null || data.data == null){
//                return
//            }
//
//            filePath = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                imagePreview.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun uploadImage(){
//        if(filePath != null){
//            val ref = storageReference?.child("profileImages/" + UUID.randomUUID().toString())
//            val uploadTask = ref?.putFile(filePath!!)
//
//        }else{
//            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun uploadImage() {
//        val progressDialog =  ProgressDialog(this)
//        progressDialog.setMessage("Uploading File ...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//        val formatter= SimpleDateFormat( "yyyy_MM_dd_HH_mm_5s", Locale.getDefault())
//        val now =Date()
//        val fileName=formatter.format(now)
//        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
//        storageReference.putFile(ImageUri).
//        addOnSuccessListener{
//            binding.profileEditImg.setImageURI(null)
//            Toast.makeText( this@ProfileEditActivity, "Successfuly uploaded", Toast.LENGTH_SHORT).show()
//            Log.d("img", "succ")
//            if (progressDialog.isShowing) progressDialog.dismiss()
//        }. addOnFailureListener{
//            if (progressDialog.isShowing) progressDialog.dismiss()
//            Log.d("img", "fail")
//            Toast.makeText( this@ProfileEditActivity, "Failed", Toast.LENGTH_SHORT).show()
//        }
//    }

//
//    private fun selectImage() {
//        val intent = Intent()
//        intent.type = "image/"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, 100)
//
//
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        uploadImage()
//        if (requestCode == 100 && resultCode == RESULT_OK){
//            ImageUri = data?.data!!
//            binding.profileEditImg.setImageURI(ImageUri)
//        }
//    }


//    fun getUsers(){
//
//        val uid = user?.uid
//        if (uid != null) {
//            fireStore.collection("users").document(uid)
//                .get()
//                .addOnSuccessListener {
//
//    //                    Log.d("user_pr", it.data.toString())
//
//    //                    Toast.makeText(
//    //                        activity,
//    //                        "Your first name is ${it.data?.get("username")}  and last name is ${
//    //                            it.data?.get("photo")
//    //                        }",
//    //                        Toast.LENGTH_SHORT
//    //                    ).show()
//                }
//                .addOnFailureListener {
//                    it.printStackTrace()
//                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }

}