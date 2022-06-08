package com.foxnotes

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.foxnotes.databinding.ActivityFirstEnterBinding
import com.foxnotes.profile.LoginActivity
import com.foxnotes.profile.RegistrationActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FirstEnterActivity : AppCompatActivity() {

    lateinit var binding: ActivityFirstEnterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


        binding.signInButton.setOnClickListener {
            if (isOnline(this)) {
                replaceActivity(LoginActivity())
            } else {
                message()
            }
        }
        binding.registerButton.setOnClickListener {
            if (isOnline(this)) {
                replaceActivity(RegistrationActivity())
            } else {
                message()
            }
        }

        binding.anonButton.setOnClickListener {
            if (isOnline(this)) {
                signInAnonymously()
            } else {
                message()
            }
        }

    }

    private fun message() {
        MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_offline)
            .setTitle("You must to be online")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun replaceActivity(activity: AppCompatActivity) {
        val i = Intent(this, activity::class.java)
        startActivity(i)
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

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                    replaceActivity(MainActivity())
                } else {
                    Log.w("TAG", "signInAnonymously:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {

    }
}
