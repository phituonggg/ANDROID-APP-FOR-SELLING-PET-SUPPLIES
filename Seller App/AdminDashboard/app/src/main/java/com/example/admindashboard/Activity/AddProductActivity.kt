package com.example.admindashboard.Activity

import ImageAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admindashboard.Model.ItemsModel
import com.example.admindashboard.R
import com.example.admindashboard.databinding.ActivityAddProductBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddProductActivity : AppCompatActivity() {

    // Request code for picking multiple images
    private val PICK_IMAGE_MULTIPLE = 1
    private val imageUriList = ArrayList<Uri>() // List to store selected image URIs
    private val uploadedImageUrls = ArrayList<String>() // List to store URLs of uploaded images
    private lateinit var binding: ActivityAddProductBinding // View binding
    private var imageAdapter: ImageAdapter? = null // Adapter for displaying selected images

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize image adapter and set it to RecyclerView
        imageAdapter = ImageAdapter(this, imageUriList)
        binding.recyclerViewUploadImage.adapter = imageAdapter
        binding.recyclerViewUploadImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize spinner with categories
        val spinner = binding.spinnerCategory
        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array, // Resource array of categories
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Set onClickListener for the choose images button
        binding.btnChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE)
        }

        // Set onClickListener for the upload button
        binding.btnUpload.setOnClickListener {
            uploadImagesToFirebaseStorage()
        }
    }

    // Handle result of image picking
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) { // Multiple images selected
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageUriList.add(imageUri)
                }
            } else if (data?.data != null) { // Single image selected
                val imageUri = data.data!!
                imageUriList.add(imageUri)
            }
            imageAdapter?.notifyDataSetChanged()
        }
    }

    // Upload selected images to Firebase Storage
    private fun uploadImagesToFirebaseStorage() {
        val storageRef = FirebaseStorage.getInstance().getReference("picUrl")
        for (uri in imageUriList) {
            val imageRef = storageRef.child(UUID.randomUUID().toString())
            imageRef.putFile(uri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    uploadedImageUrls.add(downloadUri.toString())
                    // If all images are uploaded, add product to database
                    if (uploadedImageUrls.size == imageUriList.size) {
                        addProductToFirebaseDatabase()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Add product details to Firebase Database
    private fun addProductToFirebaseDatabase() {
        val title = binding.titleEdt.text.toString()
        val description = binding.descriptionEdt.text.toString()
        val price = binding.priceEdt.text.toString().toDouble()
        val size = binding.sizeEdt.text.toString().split(",").map { it.trim() } as ArrayList<String>
        val categoryId = binding.spinnerCategory.selectedItemPosition
        val id = UUID.randomUUID().toString()

        // Retrieve seller details from SharedPreferences
        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        val sellerName = sharedPref.getString("name", "")
        val sellerPic = sharedPref.getString("pic", "")
//        val sellerTell = sharedPref.getString("phone", "")

        // Create product object
        val product = ItemsModel().apply {
            this.title = title
            this.description = description
            this.price = price
            this.size = size
            this.picUrl = uploadedImageUrls
            this.sellerName = sellerName ?: ""
            this.sellerPic = sellerPic ?: ""
//            if (sellerTell != null) {
//                this.sellerTell = sellerTell
//            }
            this.rating = 0.0  // Set initial rating to 0
            this.categoryId = categoryId
//            this.id = id
        }

        // Add product to Firebase Database
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.push().setValue(product).addOnSuccessListener {
            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
            // Navigate to ProductActivity
            Intent(this, ProductActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
        }
    }
}
