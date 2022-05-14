package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    var menuToChoose: Int = R.menu.main_menu


    private lateinit var auth: FirebaseAuth
    var user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)



        if (intent.getStringExtra("activity") == "profile"){
            replaceFragment(ProfileFragment())
        }else{
            setDefaultFragment()

        }

        val toolbar = findViewById<Toolbar> (R.id.toolbar)
        setSupportActionBar(toolbar)
//        toolbar.inflateMenu(R.menu.edit_profile)
//        menuToChoose = R.menu.main_menu
//        invalidateOptionsMenu()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupDrawerToggle()
        navItemChecker()
        checkCurrentUser()



//        auth = Firebase.auth
//        signInAnonymously()

    }


    fun setupDrawerToggle(){
        toggle =  ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    fun navItemChecker(){
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_notes -> {
                    replaceFragment(NotesFragment())
//                    menuToChoose = R.menu.notes_menu
//                    invalidateOptionsMenu()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_tasks -> {
                    replaceFragment(TasksFragment())
//                    menuToChoose = R.menu.notes_menu
//                    invalidateOptionsMenu()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profile -> {
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        Log.d("user", "in system")
                        replaceFragment(ProfileFragment())
                    }else{
                        replaceActivity(LoginActivity())
                    }

//                    replaceActivity(ProfileActivity(), it.title.toString())
//                    menuToChoose = R.menu.new_note_menu
//                    invalidateOptionsMenu()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_friends -> {
                    replaceActivity(ProfileEditActivity())
//                    replaceFragment(NewNoteFragment())

//                    menuToChoose = R.menu.new_note_menu
//                    invalidateOptionsMenu()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_logout -> {
                    replaceFragment(NewNoteFragment())

//                    menuToChoose = R.menu.new_note_menu
//                    invalidateOptionsMenu()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                else ->{
                    replaceFragment(NotesFragment())
//                    menuToChoose = R.menu.notes_menu
                    drawerLayout.closeDrawer(GravityCompat.START)
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
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
//        Toast.makeText(this, "Replace", Toast.LENGTH_SHORT).show()
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
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            val headerView = navigationView.getHeaderView(0)
            val email = headerView.findViewById<TextView>(R.id.nav_email)
            email.text = getString(R.string.user_not_in_sys)


        }
    }
    private fun getUserProfile() {
        Log.d("user_nav", "in system")
        val user = Firebase.auth.currentUser
        user?.let {
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            val headerView = navigationView.getHeaderView(0)
            val username = headerView.findViewById<TextView>(R.id.nav_username)
            val email = headerView.findViewById<TextView>(R.id.nav_email)
            val img = headerView.findViewById<ShapeableImageView>(R.id.nav_img)
            if (user.displayName != null){
                username.text = user.displayName
            }else{
                username.text = getString(R.string.unauthorized_username)
            }
            email.text = user.email
            user.photoUrl?.let {  Picasso.with(this).load(user.photoUrl.toString()).into(img) }

        }
    }




//    private fun signInAnonymously() {
////        showProgressBar()
//        auth.signInAnonymously()
//            .addOnCompleteListener(MainActivity()) { task ->
//                if (task.isSuccessful) {
//                    Log.d("TAG", "signInAnonymously:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    Log.w("TAG", "signInAnonymously:failure", task.exception)
//                    Toast.makeText(this, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//
////                hideProgressBar()
//            }
//    }

    private fun updateUI(user: FirebaseUser?) {

    }

    public fun gotoLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    public fun gotoReg(view: View) {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }




//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_profile -> {
//                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
//            }
//            R.id.nav_messages -> {
//                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
//            }
//            R.id.nav_friends -> {
//                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
//            }
//            R.id.nav_update -> {
//                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
//            }
//            R.id.nav_logout -> {
//                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
//            }
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }
}