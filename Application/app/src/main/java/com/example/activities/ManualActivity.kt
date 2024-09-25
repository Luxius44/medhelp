package com.example.activities

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.example.data.MedicineData
import com.example.medicine.R
import com.example.utils.Util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ManualActivity : BaseActivity() {

	// Name
	lateinit var nameEdit: AutoCompleteTextView
	lateinit var nameInfo: TextView

	// Dosage
	lateinit var dosageEdit: EditText
	lateinit var dosageInfo: TextView

	// Quantity
	lateinit var quantityEdit: EditText
	lateinit var quantityInfo: TextView

	// Frequency
	lateinit var frequencyEdit: EditText
	lateinit var frequencyInfo: TextView
	lateinit var frequencyUnitEdit: Spinner

	// Duration
	lateinit var durationEdit: EditText
	lateinit var durationInfo: TextView
	lateinit var durationUnitEdit: Spinner

	// Renual
	lateinit var renuwalEdit: EditText
	lateinit var renuwalInfo: TextView

	// Shape
	lateinit var shapeEdit: EditText
	lateinit var shapeInfo: TextView

	// Date
	lateinit var dateEdit: EditText
	lateinit var dateInfo: TextView

	// Buttons
	lateinit var cancelButton: Button
	lateinit var validateButton: Button

	// Miscellaneous
	lateinit var listEditInfo: List<Pair<TextView, TextView>>
	private val mainActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private var modifyOrdonnance = false
	private val handler = Handler(Looper.getMainLooper())
	private var timer: Timer? = null
	lateinit var overlay: View
	lateinit var loadingImage: View
	lateinit var scrollView: ScrollView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_manual)

		val arrayFrequency = resources.getStringArray(R.array.duration_list)

		var ordonnanceData = MedicineData()
		validateButton = findViewById(R.id.validateButton)
		cancelButton = findViewById(R.id.cancelButton)
		nameEdit = findViewById(R.id.medicine_name_edit)
		dosageEdit = findViewById(R.id.dosage_edit)
		quantityEdit = findViewById(R.id.quantity_edit)
		frequencyEdit = findViewById(R.id.frequency_edit)
		frequencyUnitEdit = findViewById(R.id.duration_choice)
		durationEdit = findViewById(R.id.duration_edit)
		durationUnitEdit = findViewById(R.id.duration_choice_treatment)
		renuwalEdit = findViewById(R.id.renuwal_edit)
		shapeEdit = findViewById(R.id.shape_edit)
		dateEdit = findViewById(R.id.date_edit)

		nameInfo = findViewById(R.id.nameTextInfo)
		dosageInfo = findViewById(R.id.dosageTextInfo)
		quantityInfo = findViewById(R.id.quantityTextInfo)
		frequencyInfo = findViewById(R.id.frequencyTextInfo)
		durationInfo = findViewById(R.id.durationTextInfo)
		renuwalInfo = findViewById(R.id.renuwalTextInfo)
		shapeInfo = findViewById(R.id.shapeTextInfo)
		dateInfo = findViewById(R.id.dateTextInfo)
		listEditInfo = mutableListOf(
			Pair(dateEdit, dateInfo), Pair(dosageEdit, dosageInfo),
			Pair(durationEdit, durationInfo), Pair(nameEdit, nameInfo),
			Pair(frequencyEdit, frequencyInfo), Pair(quantityEdit, quantityInfo),
			Pair(renuwalEdit, renuwalInfo), Pair(shapeEdit, shapeInfo)
		)
		if (intent.hasExtra("MEDICAMENT_RECHERCHE") ){
			val recherche = intent.getSerializableExtra("MEDICAMENT_RECHERCHE") as MedicineData
			ordonnanceData.name = recherche.name
			ordonnanceData.dosage = recherche.dosage
			ordonnanceData.shape = recherche.shape
			nameEdit.setText(recherche.name)
			dosageEdit.setText(recherche.dosage)
			shapeEdit.setText(recherche.shape)
			intent.removeExtra("MEDICAMENT_RECHERCHE")
		}



		val listOrdonnance: MutableList<MedicineData> = Util.listElementInCache(this)
		val listMedicaments: MutableList<List<MedicineData>> = Util.listElementInCache(this)

		nameEdit.addTextChangedListener {editable ->
		ordonnanceData.name = nameEdit.text.toString()
			nameInfo.text = ""
			var suggestions = listOf<String>()
			if (listMedicaments.isNotEmpty()) suggestions = Util.suggestionMedicine(listMedicaments[0], nameEdit.text.toString())
			val adapter =
				ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
			nameEdit.setAdapter(adapter)
			if (suggestions.size != 1 && (ordonnanceData.name !in suggestions)) nameEdit.postDelayed({
				nameEdit.showDropDown()
			}, 300)
		}

		dosageEdit.addTextChangedListener {
			ordonnanceData.dosage = dosageEdit.text.toString()
			dosageInfo.text = ""
		}
		quantityEdit.addTextChangedListener {
			val input = quantityEdit.text.toString()
			if (input == "") {
				ordonnanceData.quantity = 0
			} else {
				ordonnanceData.quantity = input.toInt()
			}
			quantityInfo.text = ""
		}
		frequencyEdit.addTextChangedListener {
			val input = frequencyEdit.text.toString()
			if (input == "") {
				ordonnanceData.frequency = 0
			} else {
				ordonnanceData.frequency = input.toInt()
			}
			frequencyInfo.text = ""
		}
		frequencyUnitEdit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(
				parent: AdapterView<*>,
				view: View?,
				position: Int,
				id: Long
			) {
				ordonnanceData.frequencyUnit = frequencyUnitEdit.selectedItem.toString()
			}

			override fun onNothingSelected(parent: AdapterView<*>) {
				//Nested
			}
		}
		durationEdit.addTextChangedListener {
			val input = durationEdit.text.toString()
			if (input == "") {
				ordonnanceData.duration = 0
			} else {
				ordonnanceData.duration = input.toInt()
			}
			durationInfo.text = ""
		}
		durationUnitEdit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(
				parent: AdapterView<*>,
				view: View?,
				position: Int,
				id: Long
			) {
				ordonnanceData.durationUnit = durationUnitEdit.selectedItem.toString()
			}

			override fun onNothingSelected(parent: AdapterView<*>) {
				//Nested
			}
		}
		renuwalEdit.addTextChangedListener {
			val input = renuwalEdit.text.toString()
			if (input == "") {
				ordonnanceData.renuwal = 0
			} else {
				ordonnanceData.renuwal = input.toInt()
			}
			renuwalInfo.text = ""
		}
		shapeEdit.addTextChangedListener {
			ordonnanceData.shape = shapeEdit.text.toString()
			shapeInfo.text = ""
		}
		dateEdit.addTextChangedListener {
			ordonnanceData.date = dateEdit.text.toString()
			dateInfo.text = ""
		}
		dateEdit.setOnClickListener {
			Util.showDatePickerDialog(this@ManualActivity, dateEdit)
		}

		var position = -1
		val intent = intent

		if (intent.hasExtra("MODIF")) {

			val objet = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				intent.getSerializableExtra("MODIF", MedicineData::class.java)
			} else {
				intent.getSerializableExtra("MODIF")
			}

			position = listOrdonnance.indexOf(objet)

			ordonnanceData = objet as MedicineData

			nameEdit.text = Editable.Factory.getInstance().newEditable(ordonnanceData.name)
			dosageEdit.text = Editable.Factory.getInstance().newEditable(ordonnanceData.dosage)
			quantityEdit.text =
				Editable.Factory.getInstance().newEditable(ordonnanceData.quantity.toString())
			frequencyEdit.text =
				Editable.Factory.getInstance().newEditable(ordonnanceData.frequency.toString())
			frequencyUnitEdit.setSelection(arrayFrequency.indexOf(ordonnanceData.frequencyUnit))
			durationEdit.text =
				Editable.Factory.getInstance().newEditable(ordonnanceData.duration.toString())
			durationUnitEdit.setSelection(arrayFrequency.indexOf(ordonnanceData.durationUnit))
			renuwalEdit.text =
				Editable.Factory.getInstance().newEditable(ordonnanceData.renuwal.toString())
			shapeEdit.text = Editable.Factory.getInstance().newEditable(ordonnanceData.shape)
			dateEdit.text = Editable.Factory.getInstance().newEditable(ordonnanceData.date)

			modifyOrdonnance = true
		}

		validateButton.setOnClickListener {
			if (areAllFieldsValid()) {
				val intentValidate: Intent?
				if (modifyOrdonnance && position > -1) {
					listOrdonnance[position] = ordonnanceData
				} else {
					listOrdonnance.add(ordonnanceData)
				}
				intentValidate = Intent(this, StockActivity::class.java)
				Util.listElementSave(this, listOrdonnance)
				mainActivity.launch(intentValidate)
			}
		}

		cancelButton.setOnClickListener {
			mainActivity.launch(Intent(this, HomeActivity::class.java))
		}

		overlay = findViewById(R.id.overlay)
		loadingImage = findViewById(R.id.loading)
		scrollView = findViewById<ScrollView>(R.id.scrollview)
		println(intent.getBooleanExtra("chargement",false))
		if (intent.getBooleanExtra("chargement",false)) {
			println("LE CHARGEMENT DEVRAIT MARCH")
			overlay.visibility = View.VISIBLE
			loadingImage.visibility = View.VISIBLE
			// Créer l'objet ObjectAnimator pour l'animation de rotation
			val animator = ObjectAnimator.ofFloat(loadingImage, "rotation", 0f, 36000f)

			// Utiliser LinearInterpolator pour maintenir une vitesse constante
			animator.interpolator = LinearInterpolator()

			// Définir la durée de l'animation en millisecondes
			animator.duration = 300000

			scrollView.setOnTouchListener { _, _ -> true }
			changeEditFocus(false)
			animator.start()

			startSharedPreferencesCheck()
			intent.removeExtra("chargement")
		} else {
			loadingImage.visibility = View.GONE
		}
	}

	private fun checkFormat(editText: EditText, pattern: String, errorMessage: String): Boolean {
		val dose = editText.text.matches(Regex(pattern))
		if (!dose) {
			if (pattern == """^\d+\s[a-zA-Z]+$""") {
				dosageInfo.text = errorMessage
			}else{
				dateInfo.text = errorMessage
			}
		}
		return dose
	}

	private fun checkAllFormat(): Boolean {
		return checkFormat(
			dosageEdit,
			"""^\d+\s[a-zA-Z]+$""",
			getString(R.string.formatErrorDosage)
		) &&
				checkFormat(
					dateEdit,
					"""^\d{2}/\d{2}/\d{4}$""",
					getString(R.string.formatErrorDate)
				)
	}

	private fun areAllFieldsValid(): Boolean {
		var statut = true
		for ((edit, info) in listEditInfo) {
			if (edit.text.toString() == "") {
				info.text = getString(R.string.required_field)
				statut = false
			}
		}
		return checkAllFormat() && statut
	}

	private fun changeEditFocus(focus: Boolean) {
		for ((edit) in listEditInfo) {
			edit.isFocusable = focus
			edit.isFocusableInTouchMode = focus
		}
		frequencyUnitEdit.isEnabled = focus
		durationUnitEdit.isEnabled = focus
	}

	private fun startSharedPreferencesCheck() {
		timer = Timer()
		timer?.scheduleAtFixedRate(object : TimerTask() {
			override fun run() {
				handler.post {
					checkSharedPreferences()
				}
			}
		}, 0, 2000)
	}

	private fun checkSharedPreferences() {
		val sharedPreferences = getSharedPreferences("applicationData", Context.MODE_PRIVATE)
		val predictions = sharedPreferences.getString("predictions", null)
		println("ça marche")
		if (!predictions.isNullOrEmpty()) {
			// Traitez les prédictions ici
			scrollView.setOnTouchListener(null)
			changeEditFocus(true)
			overlay.visibility = View.GONE
			loadingImage.visibility = View.GONE

			val sharedPreferences = getSharedPreferences("applicationData", Context.MODE_PRIVATE)
			val jsonString = sharedPreferences.getString("predictions", "")
			val gson = Gson()
			val pairListType = object : TypeToken<MutableList<Pair<String, String>>>() {}.type
			val predictions: MutableList<Pair<String, String>> =
				gson.fromJson(jsonString, pairListType)

			remplissageIA(predictions)


			// Arrêtez le timer car les informations souhaitées sont disponibles
			stopSharedPreferencesCheck()
			val editor = sharedPreferences.edit()
			editor.remove("predictions") // Supprimer la clé "predictions"
			editor.apply()
		}
	}

	private fun stopSharedPreferencesCheck() {
		timer?.cancel()
		timer = null
	}

	private fun remplissageIA(predictions: MutableList<Pair<String, String>>) {
		val drugName = mutableListOf<String>()
		val drugQuantity = mutableListOf<String>()
		val drugForm = mutableListOf<String>()
		val drugFrequency = mutableListOf<String>()
		val drugDuration = mutableListOf<String>()

		for (item in predictions) {
			when (item.second) {
				"B-Drug" -> drugName.add(item.first)
				"B-DrugQuantity" -> drugQuantity.add(item.first)
				"B-DrugForm" -> drugForm.add(item.first)
				"B-DrugFrequency", "I-DrugFrequency" -> drugFrequency.add(item.first)
				"B-DrugDuration", "I-DrugDuration" -> drugDuration.add(item.first)
			}
		}
		if (drugName.isNotEmpty()) {
			nameEdit.setText(drugName[0])
		}

		if (drugQuantity.isNotEmpty()) {
			dosageEdit.setText(drugQuantity[0])
		}

		if (drugForm.isNotEmpty()) {
			dosageEdit.setText(dosageEdit.text.toString() + " " + drugForm[0])
		}

		if (drugForm.size > 1) {
			shapeEdit.setText(drugForm[1])
		}

		if (drugQuantity.size > 1) {
			frequencyEdit.setText(drugQuantity[1])
		}
	}
}