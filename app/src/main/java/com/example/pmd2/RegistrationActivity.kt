package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var user = Firebase.auth.currentUser
    private val firestore = FirebaseFirestore.getInstance()




    private var menuToChoose: Int = R.menu.notes_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        Toast.makeText(this, "Registration", Toast.LENGTH_SHORT).show()

        val register = findViewById<Button>(R.id.reg_button)
        val checkbox = findViewById<CheckBox>(R.id.register_password_visibility);
        val password = findViewById<EditText>(R.id.reg_password_edit_text);


        checkbox.setOnClickListener {
            if (checkbox.isChecked) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance();
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance();
            }
        }



        auth = Firebase.auth


        val uid = user?.uid
        if (uid != null) {
            Log.d("user_reg", uid)
        }

        register.setOnClickListener {

            registerUser()
            Log.d("TAG", "onComplete:")

        }


    }


    private fun registerUser() {
        val email: String = findViewById<EditText>(R.id.email_edit_text).text.toString()
        val password: String = findViewById<EditText>(R.id.reg_password_edit_text).text.toString()
        val rePassword: String = findViewById<EditText>(R.id.re_password_edit_text).text.toString()

        val checkEmail = email.isBlank()
        val checkPass = password.isBlank()
        val checkRePass = rePassword.isBlank()

        if (!checkEmail && !checkPass && !checkRePass && password == rePassword) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this
                ) { task ->
                    Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                        sendEmailVerification()
                        updateUI(user)

                        val intent = Intent(this, ProfileEditActivity::class.java)
                        intent.putExtra("activity", "profile_edit")
                        startActivity(intent)


                    } else {
                        Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }

        } else {
            Toast.makeText(this, "check input fields", Toast.LENGTH_SHORT).show()
        }


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







    private fun openProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
        finish()
    }

}