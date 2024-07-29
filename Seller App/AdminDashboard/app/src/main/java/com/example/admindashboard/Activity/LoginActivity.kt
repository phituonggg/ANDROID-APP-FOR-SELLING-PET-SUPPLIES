package com.example.admindashboard.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.admindashboard.Helper.HashUtil.hashPassword
import com.example.admindashboard.Model.SellerModel
import com.example.admindashboard.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding // View binding for the login activity
    private lateinit var database: FirebaseDatabase // Firebase database instance

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance() // Initialize the Firebase database

        // Set click listener for the login button
        binding.loginButton.setOnClickListener {
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(username, password) // Authenticate the user
            }
        }

        // Set click listener for the signup redirect text
        binding.signupRedirectText.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)) // Redirect to signup activity
        }
    }

    // Authenticate the user with the given username and password
    private fun authenticateUser(username: String, password: String) {
        val ref = database.getReference("Sellers").child(username)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(SellerModel::class.java)
                if (user != null) {
                    val hashedPassword = hashPassword(password)
                    if (user.password == hashedPassword) {
                        // Save the user details to SharedPreferences
                        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("name", user.name)
                            putString("id", user.id.toString())
                            putString("pic", user.pic)
                            putString("phone", user.phone)
                            apply()
                        }

                        // Navigate to the main activity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Log.w(TAG, "authenticateUser:onCancelled", databaseError.toException())
            }
        })
    }
}
