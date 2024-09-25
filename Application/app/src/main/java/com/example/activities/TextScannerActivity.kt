package com.example.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.medicine.R
import com.example.utils.Util
import com.example.ai.PrescriptionAI
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TextScannerActivity : BaseActivity() {

	private val manualActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

	val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

	private val takePictureLauncher = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) { result: ActivityResult ->
		if (result.resultCode == Activity.RESULT_OK) {
			processImageAnalyze()
		}
	}

	private var ordonnance: String? = null

	private lateinit var currentPhotoPath: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_scanner)

		takeImage()
	}

	private fun takeImage() {
		val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		try {
			// Création du fichier où l'image va être stockée AVANT de lancer l'activité
			val photoFile: File? = try {
				createImageFile()
			} catch (ex: IOException) {
				null
			}

			photoFile?.also {
				val photoURI: Uri = FileProvider.getUriForFile(
					this,
					"com.example.medicine.fileprovider",
					it
				)
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
			}

			// Lancer l'activité Appareil photo UNE SEULE FOIS
			takePictureLauncher.launch(takePictureIntent)

		} catch (e: ActivityNotFoundException) {
			// Gérer le cas où aucune activité ne peut gérer l'intention
			Toast.makeText(this, "Aucune application Appareil photo trouvée", Toast.LENGTH_SHORT)
				.show()
		}
	}


	fun processImageAnalyze() {
		// Utiliser pour récupérer l'image
		val imageFile = File(currentPhotoPath)
		if (imageFile.exists()) {
			val image = InputImage.fromFilePath(this, Uri.fromFile(imageFile))
			imageFile.delete()

			recognizer.process(image)
				.addOnSuccessListener { visionText ->
					ordonnance = visionText.text
					val items = mutableListOf<Pair<String, String>>()
					val prescriptionAI = PrescriptionAI.getInstance(this)
					prescriptionAI.analyse_text(
						this,
						visionText.text
					) { predictions -> items.addAll(predictions) }
					val editor = this.getSharedPreferences("loading", Context.MODE_PRIVATE).edit()
					Util.listElementSave(this@TextScannerActivity, mutableListOf(true))
					editor.putBoolean("loading", true)
					editor.apply()
					val intent = Intent(this, ManualActivity::class.java)
					intent.putExtra("chargement",true)
					manualActivity.launch(intent)
				}
				.addOnFailureListener { e ->
					e.printStackTrace()
				}
		}
	}

	private fun createImageFile(): File {
		// Create an image file name
		val timeStamp: String =
			SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
		val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		return File.createTempFile(
			"JPEG_${timeStamp}_", /* prefix */
			".jpg", /* suffix */
			storageDir /* directory */
		).apply {
			currentPhotoPath = absolutePath
		}
	}
}


