package com.example.utils

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.DatePicker
import android.widget.EditText
import com.example.data.MedicineData
import com.example.data.ProfilData
import com.example.data.ReminderData
import com.example.medicine.R
import com.example.receivers.AlarmReceiver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import iut.sae.medicine.parent.data.entities.MedicamentData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Util {

	/**
	 * Permet d'ajouter une alarme dans l'application pour un médicament
	 *
	 * @param context
	 * @param reminderData Les données de l'alarme du médicament
	 * @param startReminder La date de début de l'alarme
	 * @param hourReminder l'heure de l'alarme si celle dans reminderData n'est pas celle qu'on souhaite
	 * */
	fun addReminder(
		context: Context,
		reminderData: ReminderData,
		startReminder: Calendar? = null,
		hourReminder: Boolean = true
	) {
		createNotificationChannel(context)

		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

		val intentAlarm = Intent(context, AlarmReceiver::class.java)
		intentAlarm.putExtra("requestCode", reminderData.requestCode!!)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			reminderData.requestCode!!,
			intentAlarm,
			PendingIntent.FLAG_IMMUTABLE
		)

		var calendar = Calendar.getInstance()

		if (startReminder != null) {
			calendar = startReminder
		}

		if (hourReminder) {
			val hourMinute = reminderData.hourReminder!!.split(":")

			calendar.set(Calendar.HOUR_OF_DAY, hourMinute[0].toInt())
			calendar.set(Calendar.MINUTE, hourMinute[1].toInt())
			calendar.set(Calendar.SECOND, 0)
			calendar.set(Calendar.MILLISECOND, 0)
		}

		val currentTime = Calendar.getInstance()

		if (calendar.before(currentTime)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1)
		}

		calendar = verifyDayForReminder(context, reminderData, calendar)

		alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
	}

	/**
	 * Permet de donner la bonne date pour à laquelle l'alarm devra sonner
	 *
	 * @param context
	 * @param reminder
	 * @param calendar
	 *
	 * @return Calendar
	 * */
	fun verifyDayForReminder(
		context: Context,
		reminder: ReminderData,
		calendar: Calendar
	): Calendar {
		var ok = false
		while (!ok) {
			val dayStartReminder = calendar.get(Calendar.DAY_OF_WEEK)
			if (dayStartReminder == 2) {
				if (reminder.listDays.contains(context.getString(R.string.monday_mini))) {
					ok = true
				}
			}
			if (dayStartReminder == 3) {
				if (reminder.listDays.contains(context.getString(R.string.tuesday_mini))) {
					ok = true
				}
			}
			if (dayStartReminder == 4) {
				if (reminder.listDays.contains(context.getString(R.string.wednesday_mini))) {
					ok = true
				}
			}
			if (dayStartReminder == 5) {
				if (reminder.listDays.contains(context.getString(R.string.thursday_mini))) {
					ok = true
				}
			}
			if (dayStartReminder == 6) {
				if (reminder.listDays.contains(context.getString(R.string.friday_mini))) {
					ok = true
				}
			}
			if (dayStartReminder == 7) {
				if (reminder.listDays.contains(context.getString(R.string.saturday_mini))) {
					ok = true
				}
			}
			if (dayStartReminder == 1) {
				if (reminder.listDays.contains(context.getString(R.string.sunday_mini))) {
					ok = true
				}
			}
			if (!ok) {
				calendar.add(Calendar.DAY_OF_MONTH, 1)
			}
		}
		return calendar
	}

	/**
	 * Créer une catégorie de notification et permet d'envoyer des notifications
	 *
	 * @param context
	 * */
	private fun createNotificationChannel(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = "akchannel"
			val desc = "Channel for Alarm Manager"
			val imp = NotificationManager.IMPORTANCE_HIGH
			val channel = NotificationChannel("androidknowledge", name, imp)
			channel.description = desc

			val notificationManager = context.getSystemService(NotificationManager::class.java)
			notificationManager.createNotificationChannel(channel)

		}
	}

	/**
	 * Récupère une liste d'objet stocké dans le cache de l'application
	 *
	 * @param context
	 *
	 * @return MutableList<T>
	 * */
	inline fun <reified T> listElementInCache(context: Context): MutableList<T> {
		val typeElement = when (T::class) {
			ReminderData::class -> "reminders"
			MedicineData::class -> "ordonnances"
			MedicamentData::class -> "informations"
			ProfilData::class -> "profil"
			Pair::class -> "predictions"
			List::class -> "autocompletion"
			else -> return mutableListOf()
		}
		val prefs = context.getSharedPreferences("applicationData", Context.MODE_PRIVATE)
		val gson = Gson()
		val doc = prefs.getString(typeElement, null)
		val type = object : TypeToken<List<T>>() {}.type
		val listReminderCacheRecup = gson.fromJson<List<T>>(doc, type) as MutableList?
		return listReminderCacheRecup ?: mutableListOf()
	}

	/**
	 * Permet de sauvegarder une liste d'objet dans le cache
	 *
	 * @param context
	 * @param listReminder La liste d'objet qu'on souhaite enregistrer dans le cache
	 * */
	inline fun <reified T> listElementSave(context: Context, listReminder: MutableList<T>) {
		val typeElement = when (T::class) {
			ReminderData::class -> "reminders"
			MedicineData::class -> "ordonnances"
			MedicamentData::class -> "informations"
			ProfilData::class -> "profil"
			Pair::class -> "predictions"
			Boolean::class -> "loading"
			List::class -> "autocompletion"
			else -> return
		}
		val gson = Gson()
		val listJson = gson.toJson(listReminder)
		val editor = context.getSharedPreferences("applicationData", Context.MODE_PRIVATE).edit()
		editor.putString(typeElement, listJson)
		editor.apply()
	}

	/**
	 * Permet de mettre le composant Date Picker à un Edit Text
	 *
	 * @param context
	 * @param dateEditText
	 * */
	fun showDatePickerDialog(context: Context, dateEditText: EditText) {
		val calendar = Calendar.getInstance()
		val currentYear = calendar.get(Calendar.YEAR)
		val currentMonth = calendar.get(Calendar.MONTH)
		val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

		val datePickerDialog = DatePickerDialog(
			context,
			{ _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
				// Mise à jour du TextView avec la date sélectionnée
				val selectedDate = Calendar.getInstance()
				selectedDate.set(Calendar.YEAR, year)
				selectedDate.set(Calendar.MONTH, month)
				selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

				val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
				dateEditText.setText(dateFormat.format(selectedDate.time))
			},
			currentYear,
			currentMonth,
			currentDay
		)

		// Affichage du DatePickerDialog
		datePickerDialog.show()
	}

	fun suggestionMedicine(medicines: List<MedicineData>, nom: String): List<String> {
		return medicines.filter { medicine ->
			medicine.name?.contains(nom, ignoreCase = true) ?: false
		}.mapNotNull { it.name }.toSet().toList()
	}

	fun suggestionMedicament(medicines: List<MedicamentData>, nom: String): List<String> {
		return medicines.filter { medicine ->
			medicine.medicament.denominationMedicament?.contains(nom, ignoreCase = true) ?: false
		}.mapNotNull { it.medicament.denominationMedicament }.toSet().toList()
	}
}