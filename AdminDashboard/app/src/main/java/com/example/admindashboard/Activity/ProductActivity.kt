package com.example.admindashboard.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admindashboard.Adapter.ProductAdapter
import com.example.admindashboard.Model.ItemsModel
import com.example.admindashboard.R
import com.example.admindashboard.databinding.ActivityProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductActivity : BaseActivity() {
    private lateinit var binding: ActivityProductBinding
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<ItemsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the name from SharedPreferences
        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        val name = sharedPref.getString("name", "")

        Log.d("ProductActivity", "Seller name: $name")
        
        // Set up the RecyclerView with the product adapter
        productAdapter = ProductAdapter(productList)
        binding.recyclerView.adapter = productAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // Query the Firebase Realtime Database to retrieve products
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in dataSnapshot.children) {
                    val product = productSnapshot.getValue(ItemsModel::class.java)
                    if (product != null && product.sellerName == name) {
                        Log.d("ProductActivity", "Product: ${product.title}")
                        productList.add(product)
                    }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Log.e("ProductActivity", "Database error: ${databaseError.message}")
            }
        })

        // Set up the back button to finish the activity
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Set up the add product button to navigate to AddProductActivity
        binding.addProductBtn.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}
