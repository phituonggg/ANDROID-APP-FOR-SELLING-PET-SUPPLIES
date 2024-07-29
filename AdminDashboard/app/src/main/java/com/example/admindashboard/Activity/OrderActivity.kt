package com.example.admindashboard.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admindashboard.Adapter.OrderAdapter
import com.example.admindashboard.Model.ItemsModel
import com.example.admindashboard.Model.OrderModel
import com.example.admindashboard.R
import com.example.admindashboard.databinding.ActivityOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding // View binding for the activity
    private lateinit var orderAdapter: OrderAdapter // Adapter for the RecyclerView
    private var sellerProducts = listOf<ItemsModel>() // List to store the seller's products

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the order adapter with an empty list
        orderAdapter = OrderAdapter(listOf())
        binding.orderRecyclerView.adapter = orderAdapter
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Retrieve the sellerName from SharedPreferences
        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        val sellerName = sharedPref.getString("name", "")

        // Get the seller's products from Firebase
        val productRef = FirebaseDatabase.getInstance().getReference("Items")
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Filter the products to only include those sold by the logged-in seller
                sellerProducts = dataSnapshot.children.mapNotNull { it.getValue(ItemsModel::class.java) }
                    .filter { it.sellerName == sellerName }
                getOrders() // Fetch the orders after retrieving the seller's products
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    // Fetch the orders from Firebase
    private fun getOrders() {
        val ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Map the orders to include only the seller's products and calculate the subtotal
                val orders = dataSnapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                    .map { order ->
                        // Check if the order contains any of the seller's products
                        val sellerItems = order.items.filter { item ->
                            sellerProducts.any { product -> product.title == item.itemModel?.title }
                        }

                        // Calculate the subtotal for the seller's items
                        val subtotal = sellerItems.sumByDouble { item ->
                            val product = sellerProducts.find { it.title == item.itemModel?.title }
                            if (product != null) {
                                item.quantity * product.price * 0.8 // 80% of the price is considered
                            } else {
                                0.0
                            }
                        }

                        // Create a new order with the calculated subtotal
                        order.copy(subtotal = subtotal)
                    }

                // Update the adapter with the new list of orders
                orderAdapter = OrderAdapter(orders)
                binding.orderRecyclerView.adapter = orderAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })
    }
}
