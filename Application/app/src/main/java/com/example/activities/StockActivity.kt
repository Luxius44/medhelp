package com.example.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.adapters.AdapterStockMedicine
import com.example.data.MedicineData
import com.example.utils.Util
import com.example.medicine.databinding.ActivityStockMedicineRecyclerBinding


class StockActivity : BaseActivity(), AdapterStockMedicine.ItemClickListener {

	private lateinit var binding: ActivityStockMedicineRecyclerBinding
	private var listOrdonnance: MutableList<MedicineData>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityStockMedicineRecyclerBinding.inflate(layoutInflater)
		setContentView(binding.root)

		listOrdonnance = Util.listElementInCache(this)

		if (!listOrdonnance.isNullOrEmpty()) {
			binding.pasMedoc.visibility = View.INVISIBLE
		}

		val medicineAdapter = AdapterStockMedicine(listOrdonnance!!, this)
		binding.recyclerViewMedicine.adapter = medicineAdapter
		medicineAdapter.notifyDataSetChanged()
	}

	override fun onItemClick(medicine: MedicineData) {
		val intent = Intent(this@StockActivity, ModifyMedicineActivity::class.java)
		intent.putExtra("MODIF", medicine)
		startActivity(intent)
	}
}