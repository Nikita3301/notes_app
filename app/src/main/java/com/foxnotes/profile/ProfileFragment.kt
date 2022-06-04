package com.foxnotes.profile

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.foxnotes.FirstEnterActivity
import com.foxnotes.MainActivity
import com.foxnotes.R
import com.foxnotes.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var firebase: Firebase
    private var user = Firebase.auth.currentUser
    lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        if (user != null) {
            val user = Firebase.auth.currentUser
            val userId = user?.uid.toString()
            Log.d("user", "in system $userId")

            user?.let {
                binding.profileEmail.setText(user.email)
            }

            if (user?.isAnonymous == false){
                if (user.isEmailVerified) {
                    getUserProfile()
                } else {
                    binding.usernameTextField.visibility = View.GONE
                    binding.reloadLayout.visibility = View.VISIBLE
                    binding.resendLayout.visibility = View.VISIBLE
                }
            }else{
                getUserProfile()
                binding.emailTextField.visibility = View.GONE
            }


            binding.reloadButton.setOnClickListener {
               checkVerification()
            }

            binding.resendButton.setOnClickListener {
             sendVerification()

            }
            binding.logout.setOnClickListener {
              logout()
            }
        } else {
            Log.d("user", "not in system")
            val intent = Intent(activity, LoginActivity::class.java)
            (activity as MainActivity?)!!.startActivity(intent)
        }



        setToolbar()

    }

    private fun logout(){
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_sign_out)
            .setTitle("Are you really want to sign out?")
            .setNeutralButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { _, _ ->
                signOut()
                updateUI(user)
                val intent = Intent(activity, FirstEnterActivity::class.java)
                intent.putExtra("activity", "firstEnter")
                startActivity(intent)
            }.show()
    }

    private fun sendVerification(){
        auth.addAuthStateListener {
            user!!.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Email send", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Email sent.")
                }

            }
        }
    }

    private fun checkVerification(){
        user?.reload()
        auth.addAuthStateListener {
            if (user!!.isEmailVerified) {
                getUserProfile()
                binding.usernameTextField.visibility = View.VISIBLE
                binding.reloadLayout.visibility = View.GONE
                binding.resendLayout.visibility = View.GONE
            } else {
                Toast.makeText(activity, "Email not verified", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setToolbar(){
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
            if (user.displayName.isNullOrBlank()) {
                binding.profileUsername.setText(activity?.getString(R.string.unauthorized_username))
            } else {
                binding.profileUsername.setText(user.displayName)
            }
            binding.profileEmail.setText(user.email)

            user.photoUrl?.let {
                if (isOnline(requireContext())) {
                    Picasso.with(requireContext()).invalidate(user.photoUrl)
                    Picasso.with(requireContext()).load(user.photoUrl).memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE).into(binding.profileImg)
                } else {
                    Picasso.with(requireContext()).load(user.photoUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(binding.profileImg)
                }}
        }
    }


    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
    private fun signOut() {
        auth.signOut()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (user?.isEmailVerified == true){
            when (item.itemId) {
                R.id.edit_profile_menu_icon -> {
                    if (isOnline(requireContext())){
                        val intent = Intent(activity, ProfileEditActivity::class.java)
                        (activity as MainActivity?)!!.startActivity(intent)
                        (activity as MainActivity?)!!.overridePendingTransition(
                            R.anim.slide_in_left,
                            R.anim.slide_out_left
                        )
                    }else{
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("To edit a profile you must to be online")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }.show()
                    }

                }
            }
        }else{
            Toast.makeText(activity, "Email not verified", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateUI(user: FirebaseUser?) {
    }


}