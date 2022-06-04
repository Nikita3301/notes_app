package com.foxnotes.profile

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.foxnotes.MainActivity
import com.foxnotes.R
import com.foxnotes.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var user = Firebase.auth.currentUser

    lateinit var launcher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        googleLauncher()
        getClient()

        passVisibility()
        checkInputFields()

        binding.signInButton.setOnClickListener {
            loginUser()
        }

        binding.gotoRegisterPage.setOnClickListener {
            openRegistrationPage()
        }

        binding.loginForgotPassword.setOnClickListener {
            forgotPassword()
        }

        binding.regGoogle.setOnClickListener {
            signInWithGoogle()
        }


    }

    public override fun onStart() {
        super.onStart()
        checkCurrentUser()
    }

    private fun forgotPassword() {
        val md = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_reset_pass)
            .setCustomTitle(findViewById<TextView>(R.id.reset_text_view))
            .setView(R.layout.password_reset)
            .setPositiveButton("Send") { _, _ ->
            }
            .setNeutralButton(getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()


        md.setCanceledOnTouchOutside(false)
        val positiveButton = md.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val emailLayout = md.findViewById<TextInputLayout>(R.id.email_layout)
            val textInput = md.findViewById<TextInputEditText>(R.id.email_input)

            if (textInput?.text.toString()
                    .isEmpty() || !isValidEmail(textInput?.text.toString())
            ) {
                emailLayout?.error = "Invalid email"
            } else {
                emailLayout?.error = null
                md.dismiss()
                auth.sendPasswordResetEmail(textInput?.text.toString())
                    .addOnCompleteListener {
                        Log.d("emailReset", "text: ${textInput?.text.toString()}")
                        Toast.makeText(
                            this,
                            "Password reset email has been sent ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
        }
    }


    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }
    private fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }
    private fun googleLauncher() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("TAG", "Api exception $e")
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    openProfile()
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkInputFields() {
        binding.loginEmailEditText.addTextChangedListener {
            if (binding.loginEmailEditText.text.toString()
                    .isEmpty() || !isValidEmail(binding.loginEmailEditText.text.toString())
            ) {
                binding.emailTextField.error = "Invalid email"
            } else {
                binding.emailTextField.error = null
            }
        }

    }

    private fun passVisibility() {
        binding.loginPasswordVisibility1.setOnClickListener {
            if (binding.loginPasswordVisibility1.isChecked) {
                binding.loginPasswordEditText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance();
            } else {
                binding.loginPasswordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance();
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val user = Firebase.auth.currentUser
                if (user != null) {
                    Log.d("user", "in system")
                    openProfile()
                } else {
                    openMainPage()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun checkCurrentUser() {
        if (user != null) {
            Log.d("user", "in system")
            openProfile()
        } else {
            Log.d("user", "not in system")
        }
    }

    private fun loginUser() {
        if (binding.loginEmailEditText.text.toString()
                .isNotBlank() && binding.loginPasswordEditText.text.toString().isNotBlank()
        ) {
            auth.signInWithEmailAndPassword(
                binding.loginEmailEditText.text.toString(),
                binding.loginPasswordEditText.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        openProfile()
                        updateUI(user)
                    } else {
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Unable to login. Check your input",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        } else {
            if (binding.loginPasswordEditText.text.toString()
                    .isEmpty()
            ) {
                binding.passwordTextField.error = "Password is required"
                binding.loginPasswordEditText.addTextChangedListener {
                    if (binding.loginPasswordEditText.text.toString()
                            .isEmpty()
                    ) {
                        binding.passwordTextField.error = "Password is required"
                    } else {
                        binding.passwordTextField.error = null
                    }
                }
            }
            Toast.makeText(this, "Check input fields", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUI(user: FirebaseUser?) {

    }

    private fun openProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "profile")
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        finish()
    }

    private fun openRegistrationPage() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra("activity", "registration")
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        finish()
    }

    private fun openMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("activity", "main")
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (user != null) {
            openProfile()
        } else {
            openMainPage()
        }
    }
}

