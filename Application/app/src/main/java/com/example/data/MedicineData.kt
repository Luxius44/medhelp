package com.example.data

import iut.sae.medicine.parent.data.entities.Medicament
import iut.sae.medicine.parent.data.entities.MedicamentData
import java.io.Serializable

data class MedicineData(
	var name: String? = null,
	var dosage: String? = null,
	var quantity: Int? = null,
	var frequency: Int? = null,
	var frequencyUnit: String? = null,
	var duration: Int? = null,
	var durationUnit: String? = null,
	var renuwal: Int? = null,
	var shape: String? = null,
	var date: String? = null,
	var isTake: Boolean = true
) : Serializable {

	companion object Factory {

		fun createListe(medicaments: MutableList<MedicamentData>): List<MedicineData> {
			val medicineDataList = mutableListOf<MedicineData>()

			// Expression régulière pour extraire chaque médicament
			val dosageRegex = """(\d+(?:,\d+)?)\s*(mg|g|µg|mcg)""".toRegex()
			val nameRegex = """^([^\d]+)""".toRegex()

			medicaments.forEach {
				val medicament = MedicineData()
				it.medicament.formePharmaceutique
				val nameMatch = nameRegex.find(it.medicament.denominationMedicament)
				val dosageMatch = dosageRegex.find(it.medicament.denominationMedicament)
				if (nameMatch != null) {
					medicament.name = nameMatch.groupValues[1].trim()
				}
				if (dosageMatch != null) {
					val quantity = dosageMatch.groupValues[1]
					val unit = dosageMatch.groupValues[2]
					medicament.dosage = "$quantity $unit"
				}
				medicament.shape = it.medicament.formePharmaceutique
				medicineDataList.add(medicament)
			}
			return medicineDataList
		}
	}
}
