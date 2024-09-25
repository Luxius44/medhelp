package com.example.medicine

import android.view.KeyEvent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.activities.HomeActivity
import org.junit.After
import org.junit.Rule
import org.junit.Test

class HomeActivityTest {

	@get:Rule
	val activityRule = ActivityScenarioRule(HomeActivity::class.java)

	private val viewIds = listOf(
		R.id.homePageMedicinesButton,
		R.id.homePageSideEffectButton,
		R.id.homePageScanPrescriptionButton,
		R.id.homePageAddPrescriptionButton,
		R.id.profilButton
	)

	@Test
	fun testElementsDisplayed() {
		onView(withId(R.id.homePageMedicinesButton)).check(matches(isDisplayed())) // Stock de médicaments - StockActivity
		onView(withId(R.id.homePageSideEffectButton)).check(matches(isDisplayed())) // Effets secondaires - /
		onView(withId(R.id.homePageScanPrescriptionButton)).check(matches(isDisplayed())) // Scan d'ordonnances - ScannerActivity
		onView(withId(R.id.homePageAddPrescriptionButton)).check(matches(isDisplayed())) // Emplissage manuel - ManualActivity
		onView(withId(R.id.profilButton)).check(matches(isDisplayed())) // Profil - ProfilActivity
	}

	@Test
	fun testGoToStockList() {
		onView((withId(R.id.homePageMedicinesButton))).perform(ViewActions.click())

		// Vérifier que l'intention correspond à l'activité attendue
		onView(withId(R.id.StockActivity)).check(matches(isDisplayed()))

		// Vérification fonctionnement retour arrière
		onView(withId(R.id.StockActivity)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
		onView(withId(R.id.HomePage)).check(matches(isDisplayed()))
	}

	@Test
	fun testGoToScanPrescription() {
		onView((withId(R.id.homePageScanPrescriptionButton))).perform(ViewActions.click())

		// Vérifier que l'intention correspond à l'activité attendue
		onView(withId(R.id.ScannerActivity)).check(matches(isDisplayed()))

		// Vérification fonctionnement retour arrière
		onView(withId(R.id.ScannerActivity)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
		onView(withId(R.id.HomePage)).check(matches(isDisplayed()))
	}

	@Test
	fun testGoToAddPrescription() {
		onView((withId(R.id.homePageAddPrescriptionButton))).perform(ViewActions.click())

		// Vérifier que l'intention correspond à l'activité attendue
		onView(withId(R.id.ManualActivity)).check(matches(isDisplayed()))

		// Vérification fonctionnement retour arrière
		onView(withId(R.id.ManualActivity)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
		onView(withId(R.id.HomePage)).check(matches(isDisplayed()))
	}

	@Test
	fun testGoToProfile() {
		onView((withId(R.id.profilButton))).perform(ViewActions.click())

		// Vérifier que l'intention correspond à l'activité attendue
		onView(withId(R.id.ProfileActivity)).check(matches(isDisplayed()))

		// Vérification fonctionnement retour arrière
		onView(withId(R.id.ProfileActivity)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
		onView(withId(R.id.HomePage)).check(matches(isDisplayed()))
	}

}