package com.example.admindashboard.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.admindashboard.R

// Activity class for handling delivery-related functionality
class DeliverActivity : AppCompatActivity() {
    
    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set the user interface layout for this activity
        // The layout resource is defined in the res/layout/activity_deliver.xml file
        setContentView(R.layout.activity_deliver)
    }
}
