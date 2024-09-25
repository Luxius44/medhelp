package com.example.medicine

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.example.adapters.AdapterStockMedicine
import com.example.activities.StockActivity
import com.example.data.MedicineData
import com.example.data.ReminderData
import com.example.utils.Util
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StockActivityTest {
	// Règle qui lancera l'activité avant chaque test
	/*@get:Rule
	val activityRule = ActivityScenarioRule(StockActivity::class.java)*/

	@get:Rule
	val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
		android.Manifest.permission.POST_NOTIFICATIONS
	)

	// Liste des composants visible et accessible dans l'écran
	private val viewIds = listOf(
		R.id.recyclerViewMedicine
	)

	@Before
	fun setUp() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val listMedicine = Util.listElementInCache<MedicineData>(context)

		if (listMedicine.size == 0) {
			val medicine = MedicineData(
				"ALYMSYS",
				"500 mg",
				5,
				8,
				"Jour(s)",
				9,
				"Jour(s)",
				2,
				"Comprimé",
				"14/02/2025",
				true
			)
			listMedicine.add(medicine)
			Util.listElementSave(context, listMedicine)
		}

		// Lance l'activité avant chaque test
		ActivityScenario.launch(StockActivity::class.java)
	}

	@Test
	fun testUIElementsDisplayed() {
		// Vérifie si les éléments d'interface utilisateur sont affichés correctement
		viewIds.forEach { viewId ->
			Espresso.onView(ViewMatchers.withId(viewId))
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
		}

		//L'élément suivant est invisible au vu qu'il y a des données de présentes
		Espresso.onView(ViewMatchers.withId(R.id.pasMedoc))
			.check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

		val context = ApplicationProvider.getApplicationContext<Context>()
		Util.listElementSave(context, mutableListOf<MedicineData>())
	}

	@Test
	fun testRecyclerViewItemClick() {
		// Clique sur le premier élément de la RecyclerView
		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewMedicine))
			.perform(
				RecyclerViewActions.actionOnItemAtPosition<AdapterStockMedicine.MedicineViewHolder>(
					0,
					ViewActions.click()
				)
			)

		// Vérifie si on est redirigé vers la page souhaitée
		Espresso.onView(ViewMatchers.withId(R.id.button_remove))
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
	}

	@Test
	fun testRemoveOneMedicine() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val listMedicine = Util.listElementInCache<MedicineData>(context)

		// Clique sur le premier élément de la RecyclerView
		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewMedicine))
			.perform(
				RecyclerViewActions.actionOnItemAtPosition<AdapterStockMedicine.MedicineViewHolder>(
					0,
					ViewActions.click()
				)
			)


		// Clique sur le bouton de suppression
		Espresso.onView(ViewMatchers.withId(R.id.button_remove)).perform(ViewActions.click())

		val listMedicineAfterRemove = Util.listElementInCache<MedicineData>(context)
		Assert.assertEquals(listMedicine.size - 1, listMedicineAfterRemove.size)
	}

	@Test
	fun testModifyOneMedicine() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val listMedicine = Util.listElementInCache<MedicineData>(context)

		// Clique sur le premier élément de la RecyclerView
		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewMedicine))
			.perform(
				RecyclerViewActions.actionOnItemAtPosition<AdapterStockMedicine.MedicineViewHolder>(
					0,
					ViewActions.click()
				)
			)


		// Clique sur le bouton de modification
		Espresso.onView(ViewMatchers.withId(R.id.button_modify)).perform(ViewActions.click())

		// Vérifie si on est redirigé vers la page souhaitée
		Espresso.onView(ViewMatchers.withId(R.id.frequency_edit))
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

		Espresso.onView(ViewMatchers.withId(R.id.medicine_name_edit))
			.perform(
				ViewActions.click()
			)

		//Change la fréquence
		Espresso.onView(ViewMatchers.withId(R.id.frequency_edit))
			.perform(ViewActions.typeText("2"), ViewActions.closeSoftKeyboard())

		Espresso.onView(ViewMatchers.withId(R.id.validateButton))
			.perform(ViewActions.scrollTo())
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
			.perform(ViewActions.click())

		// Vérifie si on est redirigé vers la page souhaitée
		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewMedicine))
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

		val listMedicineAfterModify = Util.listElementInCache<MedicineData>(context)
		Assert.assertEquals(listMedicine.size, listMedicineAfterModify.size)

		Util.listElementSave(context, mutableListOf<MedicineData>())
	}

	@Test
	fun testAddOneReminder() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		Util.listElementSave(context, mutableListOf<ReminderData>())

		// Clique sur le premier élément de la RecyclerView
		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewMedicine))
			.perform(
				RecyclerViewActions.actionOnItemAtPosition<AdapterStockMedicine.MedicineViewHolder>(
					0,
					ViewActions.click()
				)
			)


		// Clique sur le bouton de modification
		Espresso.onView(ViewMatchers.withId(R.id.button_reminder)).perform(ViewActions.click())

		// Vérifie si on est redirigé vers la page souhaitée
		Espresso.onView(ViewMatchers.withId(R.id.reminder_subtitle))
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewReminder))
			.perform(
				RecyclerViewActions.actionOnItemAtPosition<AdapterStockMedicine.MedicineViewHolder>(
					0,
					ViewActions.click()
				)
			)

		// Mettre une description
		Espresso.onView(ViewMatchers.withId(R.id.description_edit)).perform(
			ViewActions.typeText("a prendre apres le repas"),
			ViewActions.closeSoftKeyboard()
		)

		// Mettre une date de début
		Espresso.onView(ViewMatchers.withId(R.id.date_edit)).perform(ViewActions.click())

		// Confirme la sélection de la date de début
		Espresso.onView(ViewMatchers.withText("OK"))
			.perform(ViewActions.click())


		// Ouvre le TimePicker en simulant un clic sur l'EditText ou le bouton qui déclenche le TimePicker
		Espresso.onView(ViewMatchers.withId(R.id.notification_time_edit))
			.perform(ViewActions.scrollTo()).perform(ViewActions.click())

		// Confirme la sélection de l'heure
		Espresso.onView(ViewMatchers.withText("OK"))
			.perform(ViewActions.click())

		// Vérifie que l'EditText contient maintenant l'heure sélectionnée
		Espresso.onView(ViewMatchers.withId(R.id.notification_time_edit))
			.check(ViewAssertions.matches(ViewMatchers.withText("08:00")))

		//Mettre une quantité
		Espresso.onView(ViewMatchers.withId(R.id.number_reminder_edit))
			.perform(ViewActions.scrollTo())
			.perform(ViewActions.typeText("1"), ViewActions.closeSoftKeyboard())

		Espresso.onView(ViewMatchers.withId(R.id.monday_button))
			.perform(ViewActions.scrollTo(),ViewActions.click())

		Espresso.onView(ViewMatchers.withId(R.id.validateButton))
			.perform(ViewActions.scrollTo())
			.perform(ViewActions.click())

		// Vérifie si on est redirigé vers la page souhaitée
		Espresso.onView(ViewMatchers.withId(R.id.recyclerViewMedicine))
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

		val listReminder = Util.listElementInCache<ReminderData>(context)
		Assert.assertEquals(1, listReminder.size)
	}
}