package com.example.adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data.ReminderData
import com.example.receivers.AlarmReceiver
import com.example.medicine.R
import com.example.medicine.databinding.ComponentReminderBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AdapterReminder(
	private var reminders: List<ReminderData>,
	private val itemClickListener: ItemClickListener,
	private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<AdapterReminder.ReminderViewHolder>() {

	interface ItemClickListener {
		fun onDeleteItemClick(position: Int)
		fun onModifyHour(position: Int, hour: String)
	}

	override fun getItemCount() = reminders.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
		val vb =
			ComponentReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ReminderViewHolder(vb, fragmentManager)
	}

	override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
		val reminder = reminders[position]
		holder.bind(reminder)

	}

	inner class ReminderViewHolder(
		private val itemsReminderBinding: ComponentReminderBinding,
		private val fragmentManager: FragmentManager
	) : RecyclerView.ViewHolder(itemsReminderBinding.root) {
		fun bind(item: ReminderData) {
			with(itemsReminderBinding) {
				notificationTimeEdit.setText(item.hourReminder)
				if (item.dosage != null) {
					numberReminderEdit.setText(item.dosage!!.toString())
				}
				// Sélection du jours des rappels
				for (day in item.listDays) {
					when (day) {
						mondayButton.text -> {
							mondayButton.setBackgroundResource(R.drawable.bordure_blue)
						}

						tuesdayButton.text -> {
							tuesdayButton.setBackgroundResource(R.drawable.bordure_blue)
						}

						wednesdeyButton.text -> {
							wednesdeyButton.setBackgroundResource(R.drawable.bordure_blue)
						}

						thursdayButton.text -> {
							thursdayButton.setBackgroundResource(R.drawable.bordure_blue)
						}

						fridayButton.text -> {
							fridayButton.setBackgroundResource(R.drawable.bordure_blue)
						}

						saturdayButton.text -> {
							saturdayButton.setBackgroundResource(R.drawable.bordure_blue)
						}

						sundayButton.text -> {
							sundayButton.setBackgroundResource(R.drawable.bordure_blue)
						}
					}
				}
				//Ajout de controleur pour la sélection des jours de la semaine pour le rappel
				mondayButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(mondayButton.text.toString())) {
						item.listDays.remove(mondayButton.text)
						mondayButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(mondayButton.text.toString())
						mondayButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				tuesdayButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(tuesdayButton.text.toString())) {
						item.listDays.remove(tuesdayButton.text)
						tuesdayButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(tuesdayButton.text.toString())
						tuesdayButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				wednesdeyButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(wednesdeyButton.text.toString())) {
						item.listDays.remove(wednesdeyButton.text)
						wednesdeyButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(wednesdeyButton.text.toString())
						wednesdeyButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				thursdayButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(thursdayButton.text.toString())) {
						item.listDays.remove(thursdayButton.text)
						thursdayButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(thursdayButton.text.toString())
						thursdayButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				fridayButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(fridayButton.text.toString())) {
						item.listDays.remove(fridayButton.text)
						fridayButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(fridayButton.text.toString())
						fridayButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				saturdayButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(saturdayButton.text.toString())) {
						item.listDays.remove(saturdayButton.text)
						saturdayButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(saturdayButton.text.toString())
						saturdayButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				sundayButton.setOnClickListener {
					if (item.listDays.size > 0 && item.listDays.contains(sundayButton.text.toString())) {
						item.listDays.remove(sundayButton.text)
						sundayButton.setBackgroundResource(R.drawable.edit_text_manual_filling_border)
					} else {
						item.listDays.add(sundayButton.text.toString())
						sundayButton.setBackgroundResource(R.drawable.bordure_blue)
					}
				}
				//Ajout du composant Time Picker pour remplir le champs heure
				notificationTimeEdit.setOnClickListener {

					val timePicker =
						MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(8)
							.setMinute(0).setTitleText("Sélectionner l'heure de l'alarme").build()

					timePicker.show(fragmentManager, "androidknowledge")
					timePicker.addOnPositiveButtonClickListener {
						var hourText = timePicker.hour.toString()
						var minuteText = timePicker.minute.toString()
						if (hourText.length == 1) {
							hourText = "0$hourText"
						}
						if (minuteText.length == 1) {
							minuteText = "0$minuteText"
						}

						notificationTimeEdit.setText("$hourText:$minuteText")
						itemClickListener.onModifyHour(
							absoluteAdapterPosition, "$hourText:$minuteText"
						)
					}
				}
				// Inscrire le dosage dans l'objet ReminderData à chaque modification du champs dosage
				numberReminderEdit.addTextChangedListener {
					item.dosage = numberReminderEdit.text.toString().toInt()
				}
				// Ajout d'un controleur lors de la suppression d'un item
				deleteButton.setOnClickListener {

					itemClickListener.onDeleteItemClick(absoluteAdapterPosition)

					if (item.requestCode != null) {
						val alarmManager =
							itemsReminderBinding.view2.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

						val intentAlarm =
							Intent(itemsReminderBinding.view2.context, AlarmReceiver::class.java)
						val pendingIntent = PendingIntent.getBroadcast(
							itemsReminderBinding.view2.context,
							item.requestCode!!,
							intentAlarm,
							PendingIntent.FLAG_IMMUTABLE
						)
						//Supprime l'alarme
						alarmManager.cancel(pendingIntent)
					}

					Toast.makeText(
						itemsReminderBinding.view2.context, "Alarme supprimée", Toast.LENGTH_LONG
					).show()
				}
			}
		}
	}
}
