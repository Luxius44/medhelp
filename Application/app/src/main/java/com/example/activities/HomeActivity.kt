package com.example.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.example.data.MedicineData
import com.example.medicine.R
import com.example.utils.Util
import iut.sae.medicine.api.MedicineAPI
import iut.sae.medicine.parent.data.entities.MedicamentData

class HomeActivity : AppCompatActivity() {

	private lateinit var medicinesCard: CardView
	private lateinit var sideEffectCard: CardView
	private lateinit var scannerPrescriptionCard: CardView
	private lateinit var manualPrescriptionCard: CardView
	private lateinit var profilButton: ImageButton
	lateinit var searchBar: AutoCompleteTextView
	var context = this@HomeActivity
	lateinit var listMedicaments: List<MedicamentData>

	private val scannerActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val stockActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val manualActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val profilActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val searchActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

	@SuppressLint("MissingSuperCall")
	override fun onBackPressed() {
		this.finish()
	}

	@RequiresApi(Build.VERSION_CODES.N)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_home)

		val sharedPreferences = getSharedPreferences("applicationData", Context.MODE_PRIVATE)
		Util.listElementSave(this@HomeActivity, mutableListOf(false))
		val editor = sharedPreferences.edit()
		editor.remove("predictions")
		editor.apply()

		MedicineAPI.getAllMedicaments().thenAccept {
			println(it)
			listMedicaments = it
			val medicines = MedicineData.createListe(it)
			Util.listElementSave(this@HomeActivity, mutableListOf(it))
			Util.listElementSave(this@HomeActivity, mutableListOf(medicines))
		}.exceptionally {
			it.printStackTrace()
			null
		}

		medicinesCard = findViewById(R.id.homePageMedicinesButton)
		sideEffectCard = findViewById(R.id.homePageSideEffectButton)
		scannerPrescriptionCard = findViewById(R.id.homePageScanPrescriptionButton)
		manualPrescriptionCard = findViewById(R.id.homePageAddPrescriptionButton)
		profilButton = findViewById(R.id.profilButton)
		searchBar = findViewById(R.id.search_bar)

		medicinesCard.setOnClickListener {
			stockActivity.launch(Intent(this, StockActivity::class.java))
		}

		scannerPrescriptionCard.setOnClickListener {
			scannerActivity.launch(Intent(this, ScannerActivity::class.java))
		}

		manualPrescriptionCard.setOnClickListener {
			manualActivity.launch(Intent(this, ManualActivity::class.java))
		}

		profilButton.setOnClickListener {
			profilActivity.launch(Intent(this, ProfilActivity::class.java))
		}

		sideEffectCard.setOnClickListener {
			Toast.makeText(this,R.string.no_feature_developped, Toast.LENGTH_SHORT).show()
		}

		var suggestions = listOf<String>()
		listMedicaments = Util.listElementInCache(this)

		searchBar.addTextChangedListener {
			if (listMedicaments.isEmpty())  listMedicaments = Util.listElementInCache(this)
			if (listMedicaments.isNotEmpty()) suggestions = Util.suggestionMedicament(listMedicaments, searchBar.text.toString())
			val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
			searchBar.setAdapter(adapter)
			searchBar.postDelayed({
				searchBar.showDropDown()
			}, 300)
		}

		searchBar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_DONE && searchBar.text.toString() in suggestions && listMedicaments.isNotEmpty()) {
				val intent = Intent(this, SearchActivity::class.java)
				intent.putExtra("MEDICAMENT_SELECTIONNE", listMedicaments.find { medicament ->
					medicament.medicament.denominationMedicament == searchBar.text.toString()
				})
				searchActivity.launch(intent)
				return@OnEditorActionListener true
			}
			false
		})

	}

	companion object {
		init {
			MedicineAPI.init("https://api.iut-sae-medicine.site/")
		}
	}
}