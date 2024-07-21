package com.rj.poc.coraltask.ui.drawing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.rj.poc.coraltask.R
import com.rj.poc.coraltask.data.room.AppDatabase
import com.rj.poc.coraltask.ui.DrawingView
import com.rj.poc.coraltask.viewmodel.BoxViewModel
import com.rj.poc.coraltask.viewmodel.BoxViewModelFactory
import kotlinx.coroutines.launch

class DrawingActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private val boxViewModel: BoxViewModel by viewModels {
        BoxViewModelFactory(AppDatabase.getDatabase(this).boxDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.new_back)
        window.statusBarColor = resources.getColor(android.R.color.black, theme)

        drawingView = findViewById(R.id.drawingView)
        boxViewModel.boxes.observe(this) { boxes ->
            drawingView.setBoxes(boxes)
        }

        boxViewModel.saveStatus.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Drawing's saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save drawing's", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<AppCompatButton>(R.id.clearButton).setOnClickListener {
            boxViewModel.clearBoxes()
            drawingView.clearBoxes()
        }
        findViewById<AppCompatButton>(R.id.saveAllButton).setOnClickListener {
            lifecycleScope.launch {
                boxViewModel.saveBoxes(drawingView.getBoxes())
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
