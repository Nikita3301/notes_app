package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.pmd2.databinding.ActivityMainBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(){

    private lateinit var toggle: ActionBarDrawerToggle
    var menuToChoose: Int = R.menu.main_menu
    lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth




        if (intent.getStringExtra("activity") == "profile"){
            replaceFragment(ProfileFragment())
        }else{
            setDefaultFragment()
        }

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupDrawerToggle()
        navItemChecker()
        checkCurrentUser()

    }


    fun setupDrawerToggle(){
        toggle =  ActionBarDrawerToggle(
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

    fun navItemChecker(){
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
                    }else{
                        replaceActivity(LoginActivity())
                    }
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_friends -> {
//                    replaceActivity(ProfileEditActivity())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                else ->{
                    replaceFragment(NotesFragment())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

            }

            true
        }
    }



//    fun onCreateNewNote() {
//        val dialog = BottomSheetDialog(this)
//
//        // on below line we are inflating a layout file which we have created.
//        val view = layoutInflater.inflate(R.layout.notes_bottom_menu, null)
//
//        // on below line we are creating a variable for our button
//        // which we are using to dismiss our dialog.
//        val btnClose = view.findViewById<Button>(R.id.button_sheet)
//
//        // on below line we are adding on click listener
//        // for our dismissing the dialog button.
//        btnClose.setOnClickListener {
//            // on below line we are calling a dismiss
//            // method to close our dialog.
//            dialog.dismiss()
//        }
//        // below line is use to set cancelable to avoid
//        // closing of dialog box when clicking on the screen.
//        dialog.setCancelable(false)
//
//        // on below line we are setting
//        // content view to our view.
//        dialog.setContentView(view)
//
//        // on below line we are calling
//        // a show method to display a dialog.
//        dialog.show()
//    }
//
//    private fun showDialog() {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.notes_bottom_menu)
//        val yesBtn = dialog.findViewById(R.id.new_task_button) as Button
//        yesBtn.setOnClickListener {
//            dialog.dismiss()
//        }
////        noBtn.setOnClickListener { dialog.dismiss() }
//        dialog.show()
//
//    }



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
            signInAnonymously()
        }
    }
    private fun getUserProfile() {
        val user = auth.currentUser
        user?.let {
            val headerView = binding.navView.getHeaderView(0)
            val username = headerView.findViewById<TextView>(R.id.nav_username)
            val email = headerView.findViewById<TextView>(R.id.nav_email)
            val img = headerView.findViewById<ShapeableImageView>(R.id.nav_img)

            if (user.displayName.isNullOrBlank()){
                username.text = getString(R.string.unauthorized_username)
            }else{
                username.text = user.displayName
            }
            if (user.email.isNullOrBlank()){
                email.isVisible = false
            }else{
                email.text = user.email
            }
            user.photoUrl?.let {  Picasso.with(this).load(user.photoUrl.toString()).into(img) }
        }
    }
    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                    getUserProfile()
                } else {
                    Log.w("TAG", "signInAnonymously:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    fun gotoLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun gotoReg(view: View) {
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