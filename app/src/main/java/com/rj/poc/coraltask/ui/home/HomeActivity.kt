package com.rj.poc.coraltask.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rj.poc.coraltask.R
import com.rj.poc.coraltask.ui.capture.CaptureImageActivity
import com.rj.poc.coraltask.ui.drawing.DrawingActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.statusBarColor = resources.getColor(android.R.color.black, theme)


        findViewById<AppCompatButton>(R.id.choice1).setOnClickListener {
            startActivity(Intent(this,CaptureImageActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.choice2).setOnClickListener {
            startActivity(Intent(this,DrawingActivity::class.java))
        }

    }
}