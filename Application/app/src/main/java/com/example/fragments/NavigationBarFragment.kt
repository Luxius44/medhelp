package com.example.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.activities.HomeActivity
import com.example.activities.ManualActivity
import com.example.activities.ModifyMedicineActivity
import com.example.activities.NotificationActivity
import com.example.activities.ProfilActivity
import com.example.activities.ReminderActivity
import com.example.activities.ScannerActivity
import com.example.activities.StockActivity
import com.example.medicine.R


class NavigationBarFragment : Fragment() {

	private val homeActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val scannerActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val stockActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
	private val manualActivity =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}


	private lateinit var homeButton: ImageButton
	private lateinit var medicinesButton: ImageButton
	private lateinit var addPrescriptionManualButton: ImageButton
	private lateinit var scanPrescriptionButton: ImageButton
	private lateinit var prescriptionSideEffet: ImageButton

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		val view = inflater.inflate(R.layout.fragment_navigation_bar, container, false)

		homeButton = view.findViewById(R.id.homeButton)
		medicinesButton = view.findViewById(R.id.medicationsButton)
		addPrescriptionManualButton = view.findViewById(R.id.prescriptionAddButton)
		scanPrescriptionButton = view.findViewById(R.id.prescriptionScanButton)
		prescriptionSideEffet = view.findViewById(R.id.prescriptionSideEffectButton)

		homeButton.setOnClickListener {
			homeActivity.launch(Intent(requireContext(), HomeActivity::class.java))
		}

		medicinesButton.setOnClickListener {
			stockActivity.launch(Intent(requireContext(), StockActivity::class.java))
		}

		addPrescriptionManualButton.setOnClickListener {
			manualActivity.launch(Intent(requireContext(), ManualActivity::class.java))
		}

		scanPrescriptionButton.setOnClickListener {
			scannerActivity.launch(Intent(requireContext(), ScannerActivity::class.java))
		}

		prescriptionSideEffet.setOnClickListener {
			Toast.makeText(this.requireContext(),R.string.no_feature_developped,Toast.LENGTH_SHORT).show()
		}

		// Inflate the layout for this fragment
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Listener appelé une fois que les elements de la vue sont créés
		view.viewTreeObserver.addOnGlobalLayoutListener(object :
			ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {

				val colorHome = ContextCompat.getColor(requireContext(), R.color.home_page_light)
				val colorMedicine = ContextCompat.getColor(requireContext(), R.color.medication_stock_light)
				val colorScan = ContextCompat.getColor(requireContext(), R.color.scan_document_light)
				val colorAddPrescription = ContextCompat.getColor(requireContext(), R.color.manual_filling_light)

				when (requireActivity()) {
					is HomeActivity, is ProfilActivity -> {
						val homeActivity = homeButton.background as Drawable
						homeActivity.colorFilter =
							PorterDuffColorFilter(colorHome, PorterDuff.Mode.MULTIPLY)
					}

					is ScannerActivity -> {
						val scanPrescription = scanPrescriptionButton.background as Drawable
						scanPrescription.colorFilter =
							PorterDuffColorFilter(colorScan, PorterDuff.Mode.MULTIPLY)
					}

					is StockActivity, is ModifyMedicineActivity , is ReminderActivity, is NotificationActivity-> {
						val medicine = medicinesButton.background as Drawable
						medicine.colorFilter =
							PorterDuffColorFilter(colorMedicine, PorterDuff.Mode.MULTIPLY)
					}

					is ManualActivity -> {
						val addPrescription = addPrescriptionManualButton.background as Drawable
						addPrescription.colorFilter =
							PorterDuffColorFilter(colorAddPrescription, PorterDuff.Mode.MULTIPLY)
					}
				}

				// On retire le listener
				view.viewTreeObserver.removeOnGlobalLayoutListener(this)
			}
		})
	}

}