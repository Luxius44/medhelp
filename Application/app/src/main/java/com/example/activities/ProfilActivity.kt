package com.example.activities

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.example.data.ProfilData
import com.example.medicine.R
import com.example.utils.Util

class ProfilActivity : BaseActivity() {

	lateinit var ageEdit: EditText
	lateinit var tailleEdit: EditText
	lateinit var sexeEdit: EditText
	lateinit var poidsEdit: EditText
	lateinit var allergieEdit: EditText
	lateinit var chroniqueEdit: EditText
	lateinit var complementEdit: EditText
	private var userData = ProfilData()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_profil)

		ageEdit = findViewById(R.id.age_edit)
		tailleEdit = findViewById(R.id.taille_edit)
		sexeEdit = findViewById(R.id.sexe_edit)
		poidsEdit = findViewById(R.id.poids_edit)
		allergieEdit = findViewById(R.id.allergie_edit)
		chroniqueEdit = findViewById(R.id.chronique_edit)
		complementEdit = findViewById(R.id.complement_edit)


		ageEdit.addTextChangedListener {
			val ageText = ageEdit.text.toString()
			if (ageText.isNotEmpty()) {
				userData.age = ageText.toInt()
			}
		}

		tailleEdit.addTextChangedListener {
			val tailleText = tailleEdit.text.toString()
			if (tailleText.isNotEmpty()) {
				userData.taille = tailleText.toInt()
			}
		}

		poidsEdit.addTextChangedListener {
			val poidsText = poidsEdit.text.toString()
			if (poidsText.isNotEmpty()) {
				userData.poids = poidsText.toInt()
			}
		}

		sexeEdit.addTextChangedListener {
			val sexeText = sexeEdit.text.toString()
			if (sexeText.isNotEmpty()) {
				userData.sexe = sexeText
			}
		}

		allergieEdit.addTextChangedListener {
			val allergieText = allergieEdit.text.toString()
			if (allergieText.isNotEmpty()) {
				userData.allergie = allergieText
			}
		}

		chroniqueEdit.addTextChangedListener {
			val chroniqueText = chroniqueEdit.text.toString()
			if (chroniqueText.isNotEmpty()) {
				userData.chronique = chroniqueText
			}
		}

		complementEdit.addTextChangedListener {
			val complementText = complementEdit.text.toString()
			if (complementText.isNotEmpty()) {
				userData.complement = complementText
			}
		}

		val infosUser: MutableList<ProfilData> = Util.listElementInCache(this)

		if (infosUser.isNotEmpty()) {
			userData = infosUser[0]

			if (userData.age != null) ageEdit.text =
				Editable.Factory.getInstance().newEditable(userData.age.toString())
			if (userData.taille != null) tailleEdit.text =
				Editable.Factory.getInstance().newEditable(userData.taille.toString())
			if (userData.sexe != null) sexeEdit.text =
				Editable.Factory.getInstance().newEditable(userData.sexe.toString())
			if (userData.poids != null) poidsEdit.text =
				Editable.Factory.getInstance().newEditable(userData.poids.toString())
			if (userData.allergie != null) allergieEdit.text =
				Editable.Factory.getInstance().newEditable(userData.allergie.toString())
			if (userData.chronique != null) chroniqueEdit.text =
				Editable.Factory.getInstance().newEditable(userData.chronique.toString())
			if (userData.complement != null) complementEdit.text =
				Editable.Factory.getInstance().newEditable(userData.complement.toString())

		}

		Util.listElementSave(this@ProfilActivity, mutableListOf(false))


	}

	override fun onStop() {
		super.onStop()
		Util.listElementSave(this@ProfilActivity, mutableListOf(userData))
	}
}