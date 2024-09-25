package com.example.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.data.MedicineData
import com.example.data.ReminderData
import com.example.utils.Util
import java.util.Calendar

class NotificationActionReceiver : BroadcastReceiver() {

	companion object {
		const val medicineTakeText = "PRISE DU MEDICAMENT"
	}

	private var reminderRecup: ReminderData? = null

	override fun onReceive(context: Context, intent: Intent) {
		val action = intent.action
		val requestCode = intent.getIntExtra("requestCode", 0)

		val notificationManager = NotificationManagerCompat.from(context)
		notificationManager.cancel(requestCode)

		val nameMedicine = getNameMedicine(context, requestCode)
		val listMedicine = if (nameMedicine.isNotEmpty()) getMedicineList(context) else null

		if (listMedicine != null) {
			val positionMedicine = getPositionMedicine(listMedicine, nameMedicine)
			if (positionMedicine != -1) {
				handleAction(context, action, listMedicine, positionMedicine, nameMedicine)
			}
		}
	}

	private fun getNameMedicine(context: Context, requestCode: Int): String {
		if (requestCode == 0) return ""

		val listReminderCacheRecup: MutableList<ReminderData> = Util.listElementInCache(context)
		for (reminder in listReminderCacheRecup) {
			if (reminder.requestCode == requestCode) {
				reminderRecup = reminder
				return reminder.nameMedicine ?: ""
			}
		}
		return ""
	}

	private fun getMedicineList(context: Context): MutableList<MedicineData>? {
		return Util.listElementInCache(context)
	}

	private fun getPositionMedicine(
		listMedicine: MutableList<MedicineData>,
		nameMedicine: String
	): Int {
		for (i in listMedicine.indices) {
			if (listMedicine[i].name == nameMedicine) {
				return i
			}
		}
		return -1
	}

	private fun handleAction(
		context: Context,
		action: String?,
		listMedicine: MutableList<MedicineData>,
		positionMedicine: Int,
		nameMedicine: String
	) {
		when (action) {
			"TAKE_MEDICINE" -> takeMedicine(context, listMedicine, positionMedicine, nameMedicine)
			"IGNORE_MEDICINE" -> Log.v(medicineTakeText, "Pas pris")
			"LATER_MEDICINE" -> remindLater(context)
		}
	}

	private fun takeMedicine(
		context: Context,
		listMedicine: MutableList<MedicineData>,
		positionMedicine: Int,
		nameMedicine: String
	) {
		listMedicine[positionMedicine].isTake = true
		val quantity = listMedicine[positionMedicine].quantity
		if (quantity != null && quantity > 0) {
			listMedicine[positionMedicine].quantity = quantity - 1
		} else {
			Toast.makeText(context, "C'était votre dernier $nameMedicine", Toast.LENGTH_LONG).show()
		}
		Util.listElementSave(context, listMedicine)
		Log.v(medicineTakeText, "Pris")
	}

	private fun remindLater(context: Context) {
		val calendar = Calendar.getInstance().apply {
			add(Calendar.MINUTE, 10)
		}
		reminderRecup?.let {
			Util.addReminder(context, it, calendar, false)
		}
		Log.v(medicineTakeText, "Dans 10min")
	}
}
