package com.example.pmd2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pmd2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var user = Firebase.auth.currentUser
    lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        setHasOptionsMenu(true)
        Toast.makeText(activity, "Profile", Toast.LENGTH_SHORT).show()
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        if (user != null) {
            val user = Firebase.auth.currentUser
            val userId = user?.uid.toString()
            Log.d("user", "in system $userId")
            getUserProfile()

            binding.logout.setOnClickListener {
                signOut()
                updateUI(user)
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("activity", "login")
                startActivity(intent)
            }
        } else {
            Log.d("user", "not in system")
            val intent = Intent(activity, LoginActivity::class.java)
            (activity as MainActivity?)!!.startActivity(intent)
        }


        (activity as MainActivity?)!!.menuToChoose = R.menu.edit_profile
        (activity as MainActivity?)!!.invalidateOptionsMenu()


        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        (activity as MainActivity?)!!.setupDrawerToggle()
        (activity as MainActivity?)!!.navItemChecker()

    }

    private fun getUserProfile() {
        val user = auth.currentUser
        user?.let {
            binding.profileUsername.setText(getString(R.string.unauthorized_username))
            if (user.displayName.isNullOrBlank()) {
                binding.profileUsername.setText(getString(R.string.unauthorized_username))
            } else {
                binding.profileUsername.setText(user.displayName)
            }
            binding.profileEmail.setText(user.email)
            if (user.photoUrl != null) {
                Picasso.with(requireContext()).load(user.photoUrl.toString()).into(binding.profileImg)
            }
        }
    }

    private fun signOut() {
        auth.signOut()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile_menu_icon -> {
                val intent = Intent(activity, ProfileEditActivity::class.java)
                (activity as MainActivity?)!!.startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI(user: FirebaseUser?) {
    }


}