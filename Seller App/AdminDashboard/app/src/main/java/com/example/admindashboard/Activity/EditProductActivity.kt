package com.example.admindashboard.Activity

import ImageAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admindashboard.Model.ItemsModel
import com.example.admindashboard.R
import com.example.admindashboard.databinding.ActivityEditProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

// Activity class for editing an existing product
class EditProductActivity : BaseActivity() {
    private val PICK_IMAGE_MULTIPLE = 1 // Request code for picking multiple images
    private val imageUriList = ArrayList<Uri>() // List to store selected image URIs
    private lateinit var binding: ActivityEditProductBinding // View binding

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the product title passed from the previous activity
        val productTitle = intent.getStringExtra("productTitle")

        if (productTitle != null) {
            // Fetch the product details from the database using the product title
            fetchProductFromDatabase(productTitle) { product ->
                // Populate the UI with the fetched product details
                binding.titleEdt.setText(product.title)
                binding.descriptionEdt.setText(product.description)
                binding.priceEdt.setText(product.price.toString())
                
                val spinner = binding.spinnerCategory
                val categoriesArray = resources.getStringArray(R.array.categories_array)
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                product.categoryId?.let { binding.spinnerCategory.setSelection(it) }
                binding.sizeEdt.setText(product.size.joinToString(", "))

                // Convert the list of image URLs to a list of Uris and set up the RecyclerView with the ImageAdapter
                imageUriList.add(Uri.parse(product.picUrl[0]))
                val imageAdapter = ImageAdapter(this, imageUriList)
                binding.recyclerViewUploadImage.adapter = imageAdapter
                binding.recyclerViewUploadImage.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        // Set click listener for the choose image button
        binding.btnChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_MULTIPLE
            )
        }

        // Set click listener for the confirm button
        binding.btnConfirm.setOnClickListener {
            val updatedTitle = binding.titleEdt.text.toString()
            val updatedDescription = binding.descriptionEdt.text.toString()
            val updatedPrice = binding.priceEdt.text.toString().toDouble()
            val updatedSize = ArrayList(binding.sizeEdt.text.toString().split(",").map { it.trim() })
            val updatedCategoryId = binding.spinnerCategory.selectedItemPosition

            // Create a map of the fields to update
            val updatedProductFields = mapOf(
                "title" to updatedTitle,
                "description" to updatedDescription,
                "price" to updatedPrice,
                "size" to updatedSize,
                "categoryId" to updatedCategoryId
            )

            // Write the updated product fields back to the Firebase database
            val database = FirebaseDatabase.getInstance()
            val itemsRef = database.getReference("Items")
            val query = itemsRef.orderByChild("title").equalTo(productTitle)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (productSnapshot in dataSnapshot.children) {
                        // Update the product in the database
                        productSnapshot.ref.updateChildren(updatedProductFields)
                    }
                    // Show a Toast message when the update is successful
                    Toast.makeText(this@EditProductActivity, "Product updated successfully", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                    Log.w(TAG, "updateProduct:onCancelled", databaseError.toException())
                }
            })
        }

        // Set click listener for the back button
        binding.backBtn.setOnClickListener {
            finish() // Close the activity and go back to the previous screen
        }
    }

    // Fetch product details from the Firebase database
    private fun fetchProductFromDatabase(title: String, onProductFetched: (ItemsModel) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val itemsRef = database.getReference("Items")
        val query = itemsRef.orderByChild("title").equalTo(title)

        // Add a single event listener to the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (productSnapshot in dataSnapshot.children) {
                    // Deserialize the data into an ItemsModel object
                    val product = productSnapshot.getValue(ItemsModel::class.java)!!
                    onProductFetched(product)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Log.w(TAG, "loadProduct:onCancelled", databaseError.toException())
            }
        })
    }
}
