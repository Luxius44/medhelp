package com.example.utils

object ReminderCounter {
	private var counter: Int = 1

	fun incrementCounter() {
		counter++
	}

	fun getCounter(): Int {
		return counter
	}
}