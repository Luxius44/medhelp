package com.example.receivers

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.activities.NotificationActivity
import com.example.data.MedicineData
import com.example.data.ReminderData
import com.example.medicine.R
import com.example.utils.Util

class AlarmReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		// Ici, vous pouvez afficher une notification pour rappeler à l'utilisateur de prendre son médicament.
		// On utilise NotificationManager pour afficher la notification.

		val nextActivity = Intent(context, NotificationActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

		val requestCode = intent.getIntExtra("requestCode", 0)

		var nameMedicine = ""

		if (requestCode != 0) {

			val listReminderCacheRecup: MutableList<ReminderData> =
				Util.listElementInCache(context)

			for (reminder in listReminderCacheRecup) {
				if (reminder.requestCode == requestCode) {
					nameMedicine = reminder.nameMedicine!!
					break
				}
			}
		}

		if (nameMedicine != "") {
			val listMedicine = Util.listElementInCache<MedicineData>(context)

			for (medicine in listMedicine) {
				if (medicine.name == nameMedicine) {
					medicine.isTake = false
					break
				}
			}
			Util.listElementSave(context, listMedicine)
		}

		Log.v("REQUESTCODE", "requestCode de l'alarme : $requestCode")

		nextActivity.putExtra("requestCode", requestCode)

		val pendingIntent = PendingIntent.getActivity(
			context,
			requestCode,
			nextActivity,
			PendingIntent.FLAG_MUTABLE
		)

		val takeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
			action = "TAKE_MEDICINE"
			putExtra("requestCode", requestCode)
		}
		val takePendingIntent =
			PendingIntent.getBroadcast(context, requestCode, takeIntent, PendingIntent.FLAG_MUTABLE)

		val ignoreIntent = Intent(context, NotificationActionReceiver::class.java).apply {
			action = "IGNORE_MEDICINE"
			putExtra("requestCode", requestCode)
		}
		val ignorePendingIntent = PendingIntent.getBroadcast(
			context,
			requestCode,
			ignoreIntent,
			PendingIntent.FLAG_MUTABLE
		)

		val laterIntent = Intent(context, NotificationActionReceiver::class.java).apply {
			action = "LATER_MEDICINE"
			putExtra("requestCode", requestCode)
		}
		val laterPendingIntent = PendingIntent.getBroadcast(
			context,
			requestCode,
			laterIntent,
			PendingIntent.FLAG_MUTABLE
		)

		var reminderRecup: ReminderData? = null

		if (requestCode != 0) {
			val listReminderCacheRecup = Util.listElementInCache<ReminderData>(context)

			for (reminder in listReminderCacheRecup) {
				if (reminder.requestCode == requestCode) {
					nameMedicine = reminder.nameMedicine!!
					reminderRecup = reminder
					break
				}
			}
		}

		Util.addReminder(context, reminderRecup!!)

		val notificationBuilder = NotificationCompat.Builder(context, "androidknowledge")
			.setContentTitle("Rappel de médicament")
			.setContentText("Il est temps de prendre votre $nameMedicine.")
			.setSmallIcon(R.drawable.ic_launcher_notifications_24)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setDefaults(NotificationCompat.DEFAULT_ALL)
			.setContentIntent(pendingIntent)
			.addAction(R.drawable.ic_take, "Prendre", takePendingIntent)
			.addAction(R.drawable.ic_ignore, "Ignorer", ignorePendingIntent)
			.addAction(R.drawable.ic_later, "Dans 10min", laterPendingIntent)

		val notificationManagerCompat = NotificationManagerCompat.from(context)

		if (ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.ACCESS_NOTIFICATION_POLICY
			) != PackageManager.PERMISSION_GRANTED
		) {
			Toast.makeText(context, "Pas l'autorisation de faire un rappel", Toast.LENGTH_LONG)
				.show()
			return
		}
		notificationManagerCompat.notify(requestCode, notificationBuilder.build())
	}
}
