package com.example.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {

	private val homePageActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

	@SuppressLint("MissingSuperCall")
	override fun onBackPressed() {
		homePageActivity.launch(Intent(this, HomeActivity::class.java))
		finish()
	}
}