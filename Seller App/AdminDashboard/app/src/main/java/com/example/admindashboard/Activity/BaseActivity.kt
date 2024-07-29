package com.example.admindashboard.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// Base activity class that other activities can inherit from
open class BaseActivity : AppCompatActivity() {

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set window flags to enable full-screen layout without limits
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}

}