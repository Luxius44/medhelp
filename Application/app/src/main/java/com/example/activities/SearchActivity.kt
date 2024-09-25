package com.example.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.data.MedicineData
import com.example.medicine.R
import iut.sae.medicine.parent.data.entities.MedicamentData

class SearchActivity : BaseActivity() {

	private lateinit var ajouterBouton: Button
	private lateinit var nomComplet : TextView
	private lateinit var nom : TextView
	private lateinit var dosage : TextView
	private lateinit var forme : TextView
	private lateinit var administration : TextView
	private lateinit var substance : TextView
	private lateinit var presentation : TextView
	private lateinit var informations : TextView
	private lateinit var effets_secondaires : TextView

	private val manuelActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}


	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_search_medicine)

		ajouterBouton = findViewById(R.id.validateButton)
		nomComplet = findViewById(R.id.medicine_name_title2)
		nom = findViewById(R.id.medicine_name_color)
		dosage = findViewById(R.id.dosage_color)
		forme = findViewById(R.id.forme_color)
		administration = findViewById(R.id.administration_color)
		substance = findViewById(R.id.substance_color)
		presentation = findViewById(R.id.presentation_color)
		informations = findViewById(R.id.information_color)
		effets_secondaires = findViewById(R.id.effets_secondaires_color)
		val medicamentRecu = intent.getSerializableExtra("MEDICAMENT_SELECTIONNE") as? MedicamentData

		nomComplet.text = medicamentRecu!!.medicament.denominationMedicament
		val medicineData = MedicineData.createListe(mutableListOf(medicamentRecu))[0]
		nom.text = medicineData.name
		dosage.text = if (medicineData.dosage.isNullOrEmpty()) "Inconnu" else medicineData.dosage
		forme.text = medicineData.shape
		administration.text = medicamentRecu.medicament.voiesAdministration
		substance.text = medicamentRecu.composition.denominationSubstance

		if (medicamentRecu.effetsSecondaires.effets.isEmpty()) effets_secondaires.text = "Aucune information disponible"
		if (medicamentRecu.effetsSecondaires.effets.size >= 5 ) {
			effets_secondaires.text = medicamentRecu.effetsSecondaires.effets.subList(0, 5).toString().replace("[","").replace("]","")
		} else if (medicamentRecu.effetsSecondaires.effets.isNotEmpty()) {
			effets_secondaires.text = medicamentRecu.effetsSecondaires.effets.toString().replace("[","").replace("]","") // Ou une autre valeur par défaut
		}

		presentation.text = medicamentRecu.presentation.libellePresentation

		informations.text = if (medicamentRecu.informationsImportantes != null) {
			val regex = Regex("href=['\"]([^'\"]+)['\"]")
			val matchResult = regex.find(medicamentRecu.informationsImportantes.texteAAfficher)
			val url = matchResult?.groupValues?.get(1)
			"Lien direct vers l'information importante sur le site de l'ANSM : $url"
		} else {
			"Aucune information disponible"
		}

		ajouterBouton.setOnClickListener {
			val intent = Intent(this, ManualActivity::class.java)
			intent.putExtra("MEDICAMENT_RECHERCHE", medicineData)
			manuelActivity.launch(intent)
		}
	}
}