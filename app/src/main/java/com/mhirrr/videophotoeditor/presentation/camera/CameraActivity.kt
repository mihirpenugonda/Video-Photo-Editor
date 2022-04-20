package com.mhirrr.videophotoeditor.presentation.camera

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mhirrr.videophotoeditor.databinding.ActivityCameraBinding
import com.mhirrr.videophotoeditor.presentation.document.DocumentsActivity
import com.mhirrr.videophotoeditor.presentation.editor.EditingActivity
import com.mhirrr.videophotoeditor.utils.Constants
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null

    private var cameraSelectorOption: Boolean = true

    private var flashEnabled: Boolean = false

    private var camera: Camera? = null

    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) finish()
            else
                startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraButton.setOnClickListener {
            takePhoto()
        }

        binding.cameraSwitchButton.setOnClickListener {
            cameraSelectorOption = !cameraSelectorOption
            startCamera()
        }

        binding.cameraButton.setOnLongClickListener {
            Toast.makeText(this, "on long click", Toast.LENGTH_SHORT).show()
            true
        }

        binding.documentButton.setOnClickListener {
            val i = Intent(applicationContext, DocumentsActivity::class.java)
            startActivity(i)
        }

        binding.flashButton.setOnClickListener { torchState() }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val i = Intent(applicationContext, EditingActivity::class.java)
                    i.putExtra("image_uri", result.data?.data.toString())
                    i.putExtra("is_gallery_image", true)
                    startActivity(i)
                }
            }

        binding.galleryButton.setOnClickListener { startPhotoPicker() }
    }

    private fun torchState() {
        if (flashEnabled) {
            flashEnabled = !flashEnabled
            binding.flashButton.setImageResource(com.mhirrr.videophotoeditor.R.drawable.flash_off_icon)
            camera!!.cameraControl.enableTorch(false)
        } else {
            flashEnabled = !flashEnabled
            binding.flashButton.setImageResource(com.mhirrr.videophotoeditor.R.drawable.flash_on_icon)
            camera!!.cameraControl.enableTorch(true)
        }
    }

    private fun startPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        resultLauncher?.launch(photoPickerIntent)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.cameraView.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector =
                if (cameraSelectorOption) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                camera = null
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                    .also { camera ->
                        camera.cameraControl.enableTorch(flashEnabled)
                    }
            } catch (e: Exception) {
                Log.d(Constants.TAG, "startCamera fail: ", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            filesDir,
            "temp" + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val i = Intent(applicationContext, EditingActivity::class.java)
                    i.putExtra("image_uri", outputFileResults.savedUri.toString())
                    i.putExtra("is_gallery_image", false)
                    startActivity(i)
                }

                override fun onError(exception: ImageCaptureException) {}
            })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "permission not granted by user", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allPermissionsGranted(): Boolean =
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
}