package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.pmd2.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider.getCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private var menuToChoose: Int = R.menu.notes_menu
    var user = Firebase.auth.currentUser

    lateinit var launcher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {

                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            }catch (e:ApiException){
                Log.d("TAG", "Api exception $e")
            }
        }

        Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = Firebase.auth


        passVisibility()
        checkInputFields()



        binding.signinButton.setOnClickListener {
            loginUser()
        }

        binding.gotoRegisterPage.setOnClickListener {
            openRegistrationPage()
        }

        getClient()

        binding.regGoogle.setOnClickListener {
            signInWithGoogle()
        }




    }

    private fun getClient(): GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle(){
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
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

    private fun passVisibility(){
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


    public override fun onStart() {
        super.onStart()
        checkCurrentUser()
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
        if (binding.loginEmailEditText.text.toString().isNotBlank() && binding.loginPasswordEditText.text.toString().isNotBlank()) {
            auth.signInWithEmailAndPassword(binding.loginEmailEditText.text.toString(), binding.loginPasswordEditText.text.toString())
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
                    .isEmpty()){
                binding.passwordTextField.error = "Password is required"
                binding.loginPasswordEditText.addTextChangedListener {
                    if (binding.loginPasswordEditText.text.toString()
                            .isEmpty())
                    {
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
        if (user != null) {
            openProfile()
        } else {
            openMainPage()
        }
    }
}

