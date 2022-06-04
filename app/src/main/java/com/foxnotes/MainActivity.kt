package com.foxnotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.foxnotes.databinding.ActivityMainBinding
import com.foxnotes.notes.NotesFragment
import com.foxnotes.profile.LoginActivity
import com.foxnotes.profile.ProfileFragment
import com.foxnotes.tasks.TasksFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    var menuToChoose: Int = R.menu.main_menu
    lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStoreSettings()
        chooseStartActivity()

        setToolbar()

        setupDrawerToggle()
        navItemChecker()
        checkCurrentUser()
    }

    private fun fireStoreSettings() {
        auth = Firebase.auth
        firestoreSettings {
            isPersistenceEnabled = true
        }
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        db.firestoreSettings = settings
    }


    private fun chooseStartActivity() {
        if (intent.getStringExtra("activity") == "profile") {
            replaceFragment(ProfileFragment())
        } else {
            setDefaultFragment()
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setupDrawerToggle() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    fun navItemChecker() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_notes -> {
                    replaceFragment(NotesFragment())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_tasks -> {
                    replaceFragment(TasksFragment())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profile -> {
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        Log.d("user", "in system")
                        replaceFragment(ProfileFragment())
                    } else {
                        replaceActivity(LoginActivity())
                    }
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.sign_out -> {
                    MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_sign_out)
                        .setTitle("Are you really want to sign out?")
                        .setNeutralButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Yes") { _, _ ->
                            auth.signOut()
                            replaceActivity(FirstEnterActivity())
                        }.show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                else -> {
                    replaceFragment(NotesFragment())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    private fun setDefaultFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, NotesFragment())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menuToChoose, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()

    }

    private fun replaceActivity(activity: AppCompatActivity) {
        val i = Intent(this, activity::class.java)
        startActivity(i)
    }

    private fun checkCurrentUser() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            Log.d("user_nav", "in system")
            getUserProfile()
        } else {
            Log.d("user_nav", "not in system")
            replaceActivity(FirstEnterActivity())
        }
    }

    private fun getUserProfile() {
        val user = auth.currentUser
        user?.let {
            val headerView = binding.navView.getHeaderView(0)
            val username = headerView.findViewById<TextView>(R.id.nav_username)
            val email = headerView.findViewById<TextView>(R.id.nav_email)
            val img = headerView.findViewById<ShapeableImageView>(R.id.nav_img)

            if (user.displayName.isNullOrBlank()) {
                username.text = getString(R.string.unauthorized_username)
            } else {
                username.text = user.displayName
            }
            if (user.email.isNullOrBlank()) {
                email.isVisible = false
            } else {
                email.text = user.email
            }
            user.photoUrl?.let {
                Picasso.with(this).load(user.photoUrl.toString())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(img, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError() {
                            Picasso.with(this@MainActivity).load(user.photoUrl.toString()).into(img)
                        }

                    })
            }
        }
    }


}