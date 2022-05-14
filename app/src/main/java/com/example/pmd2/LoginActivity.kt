package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private var menuToChoose: Int = R.menu.notes_menu
    var user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()

        val checkbox = findViewById<CheckBox>(R.id.login_password_visibility1);
        val password = findViewById<EditText>(R.id.login_password_edit_text);


        checkbox.setOnClickListener {
            if (checkbox.isChecked) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance();
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance();
            }
        }

        auth = Firebase.auth




        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            loginUser()
        }

        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            openRegistrationPage()
        }


    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val user = Firebase.auth.currentUser
                if (user != null) {
                    Log.d("user", "in system")
                    openProfile()
                }else{
                    openMainPage()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }


    public override fun onStart() {
        super.onStart()
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
//        val user = Firebase.auth.currentUser
        if (user != null) {
            Log.d("user", "in system")
            openProfile()
        } else {
            Log.d("user", "not in system")
        }
    }

    private fun loginUser() {
        val email: String = findViewById<EditText>(R.id.login_email_edit_text).text.toString()
        val password: String = findViewById<EditText>(R.id.login_password_edit_text).text.toString()

        val checkEmail = email.isBlank()
        val checkPass = password.isBlank()


        if (!checkEmail && !checkPass){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
//                        val user = auth.currentUser
                        openProfile()
                        updateUI(user)
                    } else {
//                    Toast.makeText(this, "Unable to login. Check your input or try again later", Toast.LENGTH_SHORT).show()
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }else{
            Toast.makeText(this, "check input fields", Toast.LENGTH_SHORT).show()
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
    private fun openRegistrationPage() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra("activity", "registration")
        startActivity(intent)
        finish()
    }

    private fun openMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "main")
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
//        val user = Firebase.auth.currentUser
        if (user != null) {
//            Log.d("user", "in system")
            openProfile()
        }else{
            openMainPage()
        }
    }
}

