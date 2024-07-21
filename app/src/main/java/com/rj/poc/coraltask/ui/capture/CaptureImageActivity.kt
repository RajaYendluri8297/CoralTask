package com.rj.poc.coraltask.ui.capture

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.core.view.isVisible
import com.rj.poc.coraltask.R
import com.rj.poc.coraltask.ui.viewimages.ViewImagesActivity
import java.text.SimpleDateFormat
import java.util.*

class CaptureImageActivity : AppCompatActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var viewFinder: PreviewView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var capturedImages: MutableList<String>
    private lateinit var btnView: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.new_back)
        window.statusBarColor = resources.getColor(android.R.color.black, theme)

        viewFinder = findViewById(R.id.viewFinder)
        val btnCapture: Button = findViewById(R.id.btnCapture)
        btnView = findViewById(R.id.viewImages)

        capturedImages = mutableListOf()

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }

        checkIfImagesExist("Pictures/${applicationContext.getString(R.string.app_name)}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (allPermissionsGrantedTiramisu()) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_TIRAMISU)
            }
        } else {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }

        btnCapture.setOnClickListener {
            captureImageAtZoomLevels()
        }

        btnView.setOnClickListener {
            startActivity(Intent(this, ViewImagesActivity::class.java))
        }
    }

    private fun checkIfImagesExist(folderName: String) {
        val folderUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%$folderName%")

        val cursor = contentResolver.query(folderUri, projection, selection, selectionArgs, null)
        val imagesExist = cursor?.count ?: 0 > 0
        cursor?.close()
        btnView.isVisible = imagesExist
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGrantedTiramisu() = REQUIRED_PERMISSIONS_TIRAMISU.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Toast.makeText(this, "Error starting camera: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImageAtZoomLevels() {
        val zoomLevels = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)
        val handler = android.os.Handler(Looper.getMainLooper())
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)

            zoomLevels.forEachIndexed { index, zoomLevel ->
                handler.postDelayed({
                    camera.cameraControl.setLinearZoom(zoomLevel)
                    captureImage(zoomLevel, index == zoomLevels.lastIndex)
                }, (index * 2000).toLong())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage(zoomLevel: Float, isLast: Boolean) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${getFormattedDate()}_${zoomLevel}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${applicationContext.getString(R.string.app_name)}")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(this@CaptureImageActivity, "Error capturing image: ${exc.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val imageUri = output.savedUri.toString()
                    capturedImages.add(imageUri)
                    Toast.makeText(this@CaptureImageActivity, "Image saved: $imageUri", Toast.LENGTH_SHORT).show()
                    Log.d("CaptureImageActivity", "Image captured at zoom level: $zoomLevel")

                    if (isLast) {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(this@CaptureImageActivity)
                        cameraProviderFuture.addListener({
                            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                            val camera = cameraProvider.bindToLifecycle(this@CaptureImageActivity, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)
                            camera.cameraControl.setLinearZoom(0.0f)
                            checkIfImagesExist("Pictures/${applicationContext.getString(R.string.app_name)}")
                        }, ContextCompat.getMainExecutor(this@CaptureImageActivity))
                    }
                }
            }
        )
    }

    private fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        private val REQUIRED_PERMISSIONS_TIRAMISU = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
