package com.rj.poc.coraltask.ui.viewimages

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.rj.poc.coraltask.R

class ViewImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_images)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.new_back)
        window.statusBarColor = resources.getColor(android.R.color.black, theme)

        recyclerView = findViewById(R.id.rvImages)
        imageAdapter = ImageAdapter(getImagesFromFolder("CoralTask"))
        recyclerView.adapter = imageAdapter

    }


    private fun getImagesFromFolder(folderName: String): List<Uri> {
        val imageUris = mutableListOf<Uri>()
        val folderUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%$folderName%")

        val cursor = contentResolver.query(folderUri, projection, selection, selectionArgs, null)
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(folderUri, id.toString())
                imageUris.add(contentUri)
            }
        }
        return imageUris
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}