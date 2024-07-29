package com.example.admindashboard.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.admindashboard.Helper.HashUtil.hashPassword
import com.example.admindashboard.Model.SellerModel
import com.example.admindashboard.databinding.ActivitySignUpBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        // Set click listener for the sign-up button
        binding.signupButton.setOnClickListener {
            val username = binding.signupUsername.text.toString()
            val name = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val phone = binding.signupPhoneNum.text.toString()
            val address = binding.signupAddress.text.toString()

            // Validate user input
            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Create a SellerModel instance with user data
                val user = SellerModel(name, email, username, password, phone, address, "", -1)
                
                // Save the user to Firebase
                saveUserToFirebase(user)
            }
        }

        // Set click listener for the login redirect text
        binding.loginRedirectText.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }

    // Function to save user data to Firebase
    private fun saveUserToFirebase(user: SellerModel) {
        val ref = database.getReference("Sellers")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the current number of users and use it as the userId for the new user
                val userId = dataSnapshot.childrenCount.toInt()
                user.id = userId

                // Hash the user's password before saving to the database
                val hashedPassword = hashPassword(user.password!!)
                user.password = hashedPassword

                // Save the user to the database
                ref.child(user.username!!).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this@SignUpActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@SignUpActivity, "Failed to register user: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}
