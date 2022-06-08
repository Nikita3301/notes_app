package com.foxnotes.profile

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.foxnotes.MainActivity
import com.foxnotes.R
import com.foxnotes.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user = Firebase.auth.currentUser
    lateinit var binding: ActivityProfileEditBinding
    private lateinit var url: String
    private lateinit var storageReference: StorageReference

    companion object {
        private const val PICK_IMAGE_CODE = 1000
        var clicked = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        hideProgressbar()

        getUserProfile()
        val user = auth.currentUser

        storageReference = FirebaseStorage.getInstance().getReference("/users/${user?.uid}")
        binding.buttonLoadPicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_CODE
            )
        }

        binding.profileEditUsername.doAfterTextChanged {
            if (binding.profileEditUsername.text.toString().isEmpty()) {
                binding.usernameTextField.error = "Enter username"
            } else {
                binding.usernameTextField.error = null
            }
        }


        binding.saveProfileButton.setOnClickListener {
            updateProfile()
        }


    }

    private fun hideProgressbar() {
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
    }

    private fun getUserProfile() {
        val user = Firebase.auth.currentUser
        user?.let {
            if (user.isAnonymous) {
                binding.emailTextField.visibility = View.GONE
            } else {
                binding.profileEditEmail.setText(user.email)
            }
            binding.profileEditUsername.setText(user.displayName)

            if (user.photoUrl != null) {
                Picasso.with(this).invalidate(user.photoUrl)

                if (isOnline(this)) {
                    Picasso.with(this).invalidate(user.photoUrl)
                    Picasso.with(this@ProfileEditActivity).load(user.photoUrl.toString())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(binding.profileEditImg)
                } else {
                    Picasso.with(this).load(user.photoUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE).into(binding.profileEditImg)
                }
            }
        }
    }


    private fun updateProfile() {
        if (binding.profileEditUsername.text.toString().isEmpty()) {
            binding.usernameTextField.error = "Enter username"
        } else {
            binding.usernameTextField.error = null
            val profileUpdates = userProfileChangeRequest {
                displayName = binding.profileEditUsername.text.toString()
                if (clicked) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE) {
            binding.progressBar.visibility = View.VISIBLE
            binding.progressText.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val uploadTask = storageReference.putFile(data!!.data!!)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    clicked = true
                    url = downloadUri!!.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))
                    Log.d("link", url)

                    if (isOnline(this)) {
                        Picasso.with(this).invalidate(url)
                        Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE).into(binding.profileEditImg)
                    } else {
                        Picasso.with(this).load(url)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(binding.profileEditImg)
                    }
                    Toast.makeText(this, "Image successfully loaded", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun openProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        openProfile()
    }




    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

}