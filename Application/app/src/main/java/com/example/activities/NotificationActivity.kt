package com.example.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.MedicineData
import com.example.medicine.R
import com.example.data.ReminderData
import com.example.utils.Util


class NotificationActivity : BaseActivity() {
	private val mainActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private var reminderRecup: ReminderData? = null

	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_notification)

		var requestCode = 0
		var medicine: MedicineData? = null
		var nameMedicine = ""

		if (intent.hasExtra("TAKE"))
			medicine = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				intent.getSerializableExtra("TAKE", MedicineData::class.java)
			} else {
				val reminderExtra = intent.getSerializableExtra("TAKE")
				if (reminderExtra is MedicineData) {
					reminderExtra
				} else {
					null
				}
			}
		if (medicine != null) {
			nameMedicine = medicine.name!!
		}

		if (intent.hasExtra("requestCode")) {
			requestCode = intent.getIntExtra("requestCode", 0)
		}

		var positionMedicine = -1
		var listMedicine: MutableList<MedicineData>? = null

		if (requestCode != 0 || medicine != null) {
			val listReminderCacheRecup: MutableList<ReminderData> = Util.listElementInCache(this)

			if (medicine != null) {
				for (reminder in listReminderCacheRecup) {
					if (reminder.nameMedicine == nameMedicine) {
						reminderRecup = reminder
						break
					}
				}
			} else {
				for (reminder in listReminderCacheRecup) {
					if (reminder.requestCode == requestCode) {
						nameMedicine = reminder.nameMedicine!!
						reminderRecup = reminder
						break
					}
				}
			}
		}

		if (nameMedicine != "") {
			listMedicine = Util.listElementInCache(this)

			if (medicine != null) {
				positionMedicine = listMedicine.indexOf(medicine)
			} else {
				for (i in 0..listMedicine.size) {
					if (listMedicine[i].name == nameMedicine) {
						positionMedicine = i
						medicine = listMedicine[i]
						break
					}
				}
			}
		}

		val nameMedicineTextView: TextView = findViewById(R.id.nameMedicine)

		nameMedicineTextView.text = nameMedicine

		val quantityEditText: EditText = findViewById(R.id.quantity)

		quantityEditText.setText(medicine?.frequency!!.toString())

		val frequencySpinner: Spinner = findViewById(R.id.frequency_spinner)

		val arrayFrequency = resources.getStringArray(R.array.duration_list)

		val positionFrequency = arrayFrequency.indexOf(medicine.frequencyUnit)

		frequencySpinner.setSelection(positionFrequency)

		val stockEditText: EditText = findViewById(R.id.quantity_edit)

		stockEditText.setText(medicine.quantity!!.toString())

		val cancelButton: Button = findViewById(R.id.notTakeButton)
		cancelButton.setOnClickListener {
			Log.v("PRISE DU MEDICAMENT", "Pas pris")
			val intent = Intent(this, StockActivity::class.java)
			mainActivity.launch(intent)
		}

		val valideButton: Button = findViewById(R.id.validateButton)
		valideButton.setOnClickListener {
			if (listMedicine != null && positionMedicine != -1) {
				listMedicine[positionMedicine].isTake = true
				val quantity = listMedicine[positionMedicine].quantity
				if (quantity != null && quantity > 0) {
					listMedicine[positionMedicine].quantity = quantity - 1
				} else {
					Toast.makeText(
						this@NotificationActivity,
						"C'était votre dernier $nameMedicine",
						Toast.LENGTH_LONG
					).show()
				}

				Util.listElementSave(this, listMedicine)
			}

			Log.v("PRISE DU MEDICAMENT", "Pris")
			val intent = Intent(this, StockActivity::class.java)
			mainActivity.launch(intent)
		}
	}
}