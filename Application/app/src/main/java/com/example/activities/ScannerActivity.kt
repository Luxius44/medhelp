package com.example.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.medicine.R

class ScannerActivity : BaseActivity() {

	private lateinit var cameraButton: CardView
	private lateinit var galleryButton: CardView

	private val textScanner =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val requestCameraPermission = 1


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_scanner)

		cameraButton = findViewById(R.id.addPhotoFromCameraButton)
		galleryButton = findViewById(R.id.addPhotoFromGalleryButton)

		cameraButton.setOnClickListener {
			if (ContextCompat.checkSelfPermission(
					this,
					Manifest.permission.CAMERA
				) != PackageManager.PERMISSION_GRANTED
			) {
				ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.CAMERA),
					requestCameraPermission
				)
			} else {
				textScanner.launch(Intent(this, TextScannerActivity::class.java))
			}
		}

		galleryButton.setOnClickListener {
			// Start activity gallery
		}
	}


	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			requestCameraPermission -> {
				if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
					textScanner.launch(Intent(this, TextScannerActivity::class.java))
				}
			}
		}
	}
}