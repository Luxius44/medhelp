package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.data.MedicineData
import com.example.medicine.databinding.ComponentOneMedicineBinding

class AdapterStockMedicine(
	private val medicines: List<MedicineData>, private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<AdapterStockMedicine.MedicineViewHolder>() {

	fun interface ItemClickListener {
		fun onItemClick(medicine: MedicineData)
	}

	override fun getItemCount() = medicines.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
		val vb =
			ComponentOneMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MedicineViewHolder(vb, itemClickListener)
	}

	override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
		val medicine = medicines[position]
		holder.bind(medicine)
	}

	class MedicineViewHolder(
		private val itemsMedicineBinding: ComponentOneMedicineBinding,
		private val itemClickListener: ItemClickListener
	) : RecyclerView.ViewHolder(itemsMedicineBinding.root) {
		fun bind(medicineData: MedicineData) {
			with(itemsMedicineBinding) {
				expiration.text = medicineData.date
				nom.text = medicineData.name
				unite.text = medicineData.quantity.toString()
				if (medicineData.isTake) {
					statut.text = "Pris"
				} else {
					statut.text = "Pas pris"
				}

				root.setOnClickListener {
					itemClickListener.onItemClick(medicineData)
				}
			}
		}
	}

}
