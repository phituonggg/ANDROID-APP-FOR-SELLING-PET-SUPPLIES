package com.example.admindashboard.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.admindashboard.Model.ItemsModel
import com.example.admindashboard.R
import com.example.admindashboard.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding // View binding for the main activity

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve user details from SharedPreferences
        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        val name = sharedPref.getString("name", "")
        val picUrl = sharedPref.getString("pic", "")

        Log.d("MainActivity", "User name: $name")
        Log.d("MainActivity", "User pic: $picUrl")

        // Set the user's name and profile picture in the UI
        binding.sellerName.text = name
        Glide.with(this).load(picUrl).into(binding.picSeller)

        // Set click listener for the product card view
        binding.productCardView.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

        // Retrieve the list of products from Firebase and count the number of products for the logged-in user
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var productCount = 0
                for (productSnapshot in dataSnapshot.children) {
                    val product = productSnapshot.getValue(ItemsModel::class.java)
                    if (product != null && product.sellerName.toString() == name) {
                        productCount++
                    }
                }

                // Display the number of products in the UI
                binding.productNumTxt.text = productCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })

        // Set click listener for the order card view
        binding.orderCardView.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the deliver card view
        binding.deliverCardView.setOnClickListener {
            val intent = Intent(this, DeliverActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the pending card view
        binding.pendingCardView.setOnClickListener {
            val intent = Intent(this, PendingActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the profile card view
        binding.profileCardView.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the logout card view
        binding.logOutCardView.setOnClickListener {
            // Handle log out (implementation needed)
        }
    }
}
