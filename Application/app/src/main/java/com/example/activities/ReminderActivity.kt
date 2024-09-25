package com.example.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.example.adapters.AdapterReminder
import com.example.data.MedicineData
import com.example.medicine.R
import com.example.utils.ReminderCounter
import com.example.data.ReminderData
import com.example.utils.Util
import com.example.medicine.databinding.ActivityReminderBinding
import java.util.Calendar

class ReminderActivity : BaseActivity(), AdapterReminder.ItemClickListener {


	private val stockActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private lateinit var binding: ActivityReminderBinding

	private var listReminder: ArrayList<ReminderData> = arrayListOf()
	private lateinit var reminderAdapter: AdapterReminder
	private lateinit var listReminderCache: MutableList<ReminderData>
	private lateinit var description: String
	private lateinit var nameMedicine: String
	private lateinit var startReminder: String
	private lateinit var duration: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_reminder)
		binding = ActivityReminderBinding.inflate(layoutInflater)

		/*Médicament lié aux rappels*/

		var medicineData: MedicineData? = null

		if (intent.hasExtra("REMINDER")) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				medicineData = intent.getSerializableExtra("REMINDER", MedicineData::class.java)
			} else {
				val reminderExtra = intent.getSerializableExtra("REMINDER")
				if (reminderExtra is MedicineData) {
					medicineData = reminderExtra
				}
			}
		}

		duration = medicineData!!.duration.toString()

		nameMedicine = medicineData.name.toString()
		binding.medicineNameReminderEdit.setText(nameMedicine)
		binding.durationEdit.setText(duration)

		/*Rappels stockés dans le cache*/

		listReminderCache = Util.listElementInCache(this@ReminderActivity)

		/*Afficher seulement les rappels concernant le médicament auquel on veut gérer les rappels*/
		for (reminder in listReminderCache) {
			if (reminder.nameMedicine == nameMedicine) {
				listReminder.add(reminder)
			}
		}
		if (listReminder.isEmpty()) {
			listReminder = arrayListOf(
				ReminderData(
					null,
					nameMedicine,
					null,
					null,
					null,
					null,
					null,
					mutableListOf()
				)
			)
		}

		description = listReminder[0].description.toString()
		startReminder = listReminder[0].startReminder.toString()

		if (description != "null") {
			binding.descriptionEdit.setText(description)
		}
		if (startReminder != "null") {
			binding.dateEdit.setText(startReminder)
		}

		setContentView(binding.root)
		reminderAdapter = AdapterReminder(listReminder, this, supportFragmentManager)
		binding.recyclerViewReminder.adapter = reminderAdapter
		reminderAdapter.notifyDataSetChanged()

		binding.apply {
			dateEdit.setOnClickListener {
				Util.showDatePickerDialog(this@ReminderActivity, dateEdit)
			}

			cancelButton.setOnClickListener {
				stockActivity.launch(Intent(this@ReminderActivity, StockActivity::class.java))
			}
			validateButton.setOnClickListener {
				for (reminder in listReminder) {
					if (reminder.hourReminder == null) {
						continue
					}

					if (reminder.requestCode == null || reminder.requestCode == 0) {
						ReminderCounter.incrementCounter()
						reminder.requestCode = ReminderCounter.getCounter()
						listReminderCache.add(reminder)
					}
					reminder.description = binding.descriptionEdit.text.toString()
					reminder.startReminder = binding.dateEdit.text.toString()
					reminder.duration = binding.durationEdit.text.toString().toInt()

					val listDate = reminder.startReminder!!.split("/")

					val calendarStart = Calendar.getInstance()
					calendarStart.set(
						listDate[2].toInt(),
						listDate[1].toInt() - 1,
						listDate[0].toInt()
					)

					Util.addReminder(this@ReminderActivity, reminder, calendarStart)

					if (listReminderCache.isNotEmpty()) {
						var presentInList = false
						for (reminderCache: ReminderData in listReminderCache) {
							if (reminderCache.requestCode == reminder.requestCode) {
								val positionCache = listReminderCache.indexOf(reminderCache)
								listReminderCache[positionCache] = reminder
								presentInList = true
								break
							}
						}
						if (!presentInList) {
							listReminderCache.add(reminder)
						}
					}

					Toast.makeText(this@ReminderActivity, "Alarme configurée", Toast.LENGTH_LONG)
						.show()
				}

				Util.listElementSave(this@ReminderActivity, listReminderCache)

				stockActivity.launch(Intent(this@ReminderActivity, StockActivity::class.java))
			}

			addButton.setOnClickListener {
				val startReminder = dateEdit.text.toString()
				val reminderAdd = ReminderData(
					null,
					nameMedicine,
					description,
					binding.durationEdit.text.toString().toInt(),
					null,
					startReminder,
					null,
					mutableListOf()
				)

				listReminder.add(reminderAdd)
				val position = listReminder.indexOf(reminderAdd)
				reminderAdapter.notifyItemInserted(position)

				Toast.makeText(this@ReminderActivity, "Alarme ajouté à la liste", Toast.LENGTH_LONG)
					.show()
			}
		}
	}

	/**
	 * Supprime l'item de la liste du Recycler View
	 *
	 * @param position
	 * */
	override fun onDeleteItemClick(position: Int) {
		val reminder = listReminder[position]
		listReminder.removeAt(position)
		listReminderCache.remove(reminder)
		reminderAdapter.notifyItemRemoved(position)
	}

	/**
	 * Modification de l'heure appliqué sur l'item
	 *
	 * @param position Position de l'item dans la Recycler View
	 * @param hour
	 * */
	override fun onModifyHour(position: Int, hour: String) {
		listReminder[position].hourReminder = hour
	}
}