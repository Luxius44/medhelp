package com.example.activities

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.receivers.AlarmReceiver
import com.example.data.MedicineData
import com.example.medicine.R
import com.example.data.ReminderData
import com.example.utils.Util
import com.example.medicine.databinding.ActivityModifyMedicineBinding

class ModifyMedicineActivity : BaseActivity() {
	private val stockActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private var listOrdonnance: MutableList<MedicineData>? = null
	private var request_post_notification_pemission = 200

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val binding: ActivityModifyMedicineBinding = DataBindingUtil.setContentView(
			this,
			R.layout.activity_modify_medicine
		)

		listOrdonnance = Util.listElementInCache(this)

		val intent = intent
		var objet: MedicineData? = null

		if (intent.hasExtra("MODIF")) {
			objet = intent.getSerializableExtra("MODIF") as MedicineData
		}



		binding.apply {
			buttonRemove.setOnClickListener {
				listOrdonnance!!.remove(objet)
				Util.listElementSave(this@ModifyMedicineActivity, listOrdonnance!!)

				//On supprime l'ensemble des alarmes reliées aux médicaments qu'on supprime
				val listReminder =
					Util.listElementInCache<ReminderData>(this@ModifyMedicineActivity)
				val listReminderRemove = mutableListOf<ReminderData>()
				for (reminder in listReminder) {
					if (reminder.nameMedicine == objet?.name) {
						listReminderRemove.add(reminder)
						//Supprime l'alarme de l'application
						val alarmManager =
							this@ModifyMedicineActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
						val intentAlarm =
							Intent(this@ModifyMedicineActivity, AlarmReceiver::class.java)
						val pendingIntent = PendingIntent.getBroadcast(
							this@ModifyMedicineActivity,
							reminder.requestCode!!,
							intentAlarm,
							PendingIntent.FLAG_IMMUTABLE
						)
						alarmManager.cancel(pendingIntent)
					}
				}
				// Supprime les données des alarmes dans le cache
				for (reminderRemove in listReminderRemove) {
					listReminder.remove(reminderRemove)
				}
				Util.listElementSave(this@ModifyMedicineActivity, listReminder)

				val intentRemove = Intent(this@ModifyMedicineActivity, StockActivity::class.java)
				stockActivity.launch(intentRemove)
			}
			buttonRetour.setOnClickListener {
				stockActivity.launch(Intent(this@ModifyMedicineActivity, StockActivity::class.java))
			}
			buttonModify.setOnClickListener {
				val pref = this@ModifyMedicineActivity.getSharedPreferences(
					"loading",
					Context.MODE_PRIVATE
				).edit()
				pref.putBoolean("loading", false)
				pref.apply()

				val intentModify = Intent(this@ModifyMedicineActivity, ManualActivity::class.java)
				intentModify.putExtra("MODIF", objet)
				startActivity(intentModify)
			}
			buttonReminder.setOnClickListener {

				if (ContextCompat.checkSelfPermission(
						this@ModifyMedicineActivity,
						Manifest.permission.POST_NOTIFICATIONS
					) != PackageManager.PERMISSION_GRANTED
				) {
					ActivityCompat.requestPermissions(
						this@ModifyMedicineActivity,
						arrayOf(Manifest.permission.POST_NOTIFICATIONS),
						request_post_notification_pemission
					)
					println("Autorisation refusé, il faut autorisé l'utilisation de notification'")
				} else {
					val intentReminder =
						Intent(this@ModifyMedicineActivity, ReminderActivity::class.java)
					intentReminder.putExtra("REMINDER", objet)
					startActivity(intentReminder)
				}
			}
			buttonTakeMedicine.setOnClickListener {
				val intentTake =
					Intent(this@ModifyMedicineActivity, NotificationActivity::class.java)
				intentTake.putExtra("TAKE", objet)
				startActivity(intentTake)
			}
		}
	}
}