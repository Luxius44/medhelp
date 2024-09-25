package com.example.data

import java.io.Serializable

data class ReminderData(
	var requestCode: Int? = null,
	var nameMedicine: String? = null,
	var description: String? = null,
	var duration: Int? = null,
	var hourReminder: String? = null,
	var startReminder: String? = null,
	var dosage: Int? = null,
	var listDays: MutableList<String> = mutableListOf()
) : Serializable