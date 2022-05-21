package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.pmd2.databinding.ActivityLoginBinding
import com.example.pmd2.databinding.ActivityRegistrationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var user = Firebase.auth.currentUser
    private val firestore = FirebaseFirestore.getInstance()


//    lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityRegistrationBinding



    private var menuToChoose: Int = R.menu.notes_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "Registration", Toast.LENGTH_SHORT).show()




        auth = Firebase.auth


        val uid = user?.uid
        if (uid != null) {
            Log.d("user_reg", uid)
        }



       checkInputFields()


        binding.regButton.setOnClickListener {
            registerUser()
        }

        binding.gotoLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("activity", "login")
            startActivity(intent)
            finish()
        }

    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkInputFields() {
        binding.emailEditText.addTextChangedListener {
            if (binding.emailEditText.text.toString()
                    .isEmpty() || !isValidEmail(binding.emailEditText.text.toString())
            ) {
                binding.emailTextField.error = "Invalid email"
            } else {
                binding.emailTextField.error = null
            }
        }
        binding.regPasswordEditText.addTextChangedListener {
            if (binding.regPasswordEditText.text.toString()
                    .isEmpty())
            {
                binding.passwordTextField.error = "Password is required"
            } else {
                binding.passwordTextField.error = null
            }
        }

        binding.rePasswordEditText.addTextChangedListener {
            if (binding.rePasswordEditText.text.toString()
                    .isEmpty())
            {
                binding.rePasswordTextField.error = "Password is required"
            } else {
                binding.rePasswordEditText.error = null
            }
        }


    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString()
        val pass = binding.regPasswordEditText.text.toString()
        val repass = binding.rePasswordEditText.text.toString()

        if (email.isNotBlank() && pass.isNotBlank() && repass.isNotBlank() && pass == repass) {
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(
                    this
                ) { task ->
                    Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                        sendEmailVerification()
                        updateUI(user)
                        openProfile()
                        finish()

                    } else {
                        Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }

        } else {
            if (email.isEmpty()){
                binding.emailTextField.error = "Invalid email"
            }
            if (pass.isEmpty())
            {
                binding.passwordTextField.error = "Password is required"
            }else if (repass.isEmpty())
            {
                binding.rePasswordTextField.error = "Password is required"
            }else if (pass != repass)
            {
                binding.rePasswordTextField.error = "Password mismatch"
            } else {
                binding.rePasswordTextField.error = null
            }
            Toast.makeText(this, "check input fields", Toast.LENGTH_SHORT).show()
        }


    }

    private fun openProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
        finish()
    }

    private fun openProfileEdit() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
        finish()
    }

    private fun sendEmailVerification() {
        val user = Firebase.auth.currentUser

        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "Email sent.")
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {

    }








}