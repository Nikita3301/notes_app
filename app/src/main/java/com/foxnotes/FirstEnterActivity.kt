package com.foxnotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.foxnotes.databinding.ActivityFirstEnterBinding
import com.foxnotes.profile.LoginActivity
import com.foxnotes.profile.RegistrationActivity
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
            replaceActivity(LoginActivity())
        }

        binding.registerButton.setOnClickListener {
            replaceActivity(RegistrationActivity())
        }

        binding.anonButton.setOnClickListener {
            signInAnonymously()
        }

    }

    private fun replaceActivity(activity: AppCompatActivity) {
        val i = Intent(this, activity::class.java)
        startActivity(i)
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
