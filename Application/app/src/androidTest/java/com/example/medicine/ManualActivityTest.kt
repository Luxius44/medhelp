package com.example.medicine

import android.view.KeyEvent
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.EspressoException
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.activities.HomeActivity
import com.example.activities.ManualActivity
import org.hamcrest.CoreMatchers
import org.junit.Before

class ManualActivityTest {

	// Règle qui lancera l'activité avant chaque test
	@get:Rule
	val activityRule = ActivityScenarioRule(ManualActivity::class.java)

	// Liste des composants visible et accessible dans l'écran
	private val viewIds = listOf(
		R.id.medicine_name_edit,
		R.id.dosage_edit,
		R.id.quantity_edit,
		R.id.frequency_edit,
		R.id.duration_edit,
		R.id.renuwal_edit,
		R.id.shape_edit,
		R.id.date_edit,
		R.id.validateButton,
		R.id.cancelButton
	)

	@Before
	fun setUp() {
		val instrumentation = InstrumentationRegistry.getInstrumentation()
		instrumentation.uiAutomation.executeShellCommand("settings put global window_animation_scale 1")
		instrumentation.uiAutomation.executeShellCommand("settings put global transition_animation_scale 1")
		instrumentation.uiAutomation.executeShellCommand("settings put global animator_duration_scale 1")
	}

	@Test
	fun testUIElementsDisplayed() {
		// Vérifie si les éléments d'interface utilisateur sont affichés correctement
		viewIds.forEach { viewId ->
			Espresso.onView(withId(viewId))
				.perform(ViewActions.scrollTo())
				.check(matches(isDisplayed()))
		}
	}

	@Test
	fun testInputValidation() {
		// Remplis certains champs avec des données valides
		Espresso.onView(withId(R.id.medicine_name_edit))
			.perform(
				ViewActions.typeText("ALYMSYS "),
				ViewActions.pressKey(KeyEvent.KEYCODE_DEL),
				ViewActions.closeSoftKeyboard()
			)
		Espresso.onView(withId(R.id.dosage_edit))
			.perform(ViewActions.typeText("500 mg"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.quantity_edit))
			.perform(ViewActions.typeText("20"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.frequency_edit))
			.perform(ViewActions.typeText("3"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.duration_edit))
			.perform(ViewActions.typeText("7"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.renuwal_edit))
			.perform(ViewActions.typeText("5"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.shape_edit))
			.perform(ViewActions.typeText("Capsule"), ViewActions.closeSoftKeyboard())

		// Ouvre le TimePicker en simulant un clic sur l'EditText ou le bouton qui déclenche le TimePicker
		Espresso.onView(ViewMatchers.withId(R.id.validateButton)).perform(ViewActions.scrollTo())
		Espresso.onView(ViewMatchers.withId(R.id.date_edit)).perform(ViewActions.click())

		// Confirme la sélection de la date de l'ordonnance
		Espresso.onView(ViewMatchers.withText("OK"))
			.perform(ViewActions.click())

		Espresso.onView(ViewMatchers.withId(R.id.ManualActivity)).perform(
			GeneralSwipeAction(
				Swipe.FAST,
				GeneralLocation.CENTER,
				GeneralLocation.TOP_CENTER,
				Press.FINGER
			)
		)

		// Clique sur le bouton de validation
		Espresso.onView(withId(R.id.validateButton)).perform(ViewActions.click())

		// Vérifie si l'activité MainActivity est affichée après la validation
		//    Espresso.onView(withId(R.id.stock_button)).check(matches(isDisplayed()))
	}

	@Test
	fun testInputValidationWithoutOneInformation() {
		// Remplis certains champs avec des données valides
		Espresso.onView(withId(R.id.medicine_name_edit))
			.perform(
				ViewActions.typeText("ALYMSYS "),
				ViewActions.pressKey(KeyEvent.KEYCODE_DEL),
				ViewActions.closeSoftKeyboard()
			)
		Espresso.onView(withId(R.id.dosage_edit))
			.perform(ViewActions.typeText("500 mg"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.quantity_edit))
			.perform(ViewActions.typeText("20"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.frequency_edit))
			.perform(ViewActions.typeText("3"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.duration_edit))
			.perform(ViewActions.typeText("7"), ViewActions.closeSoftKeyboard())
		Espresso.onView(withId(R.id.shape_edit))
			.perform(ViewActions.typeText("Capsule"), ViewActions.closeSoftKeyboard())

		// Ouvre le TimePicker en simulant un clic sur l'EditText ou le bouton qui déclenche le TimePicker
		Espresso.onView(ViewMatchers.withId(R.id.validateButton)).perform(ViewActions.scrollTo())
		Espresso.onView(ViewMatchers.withId(R.id.date_edit)).perform(ViewActions.click())

		// Confirme la sélection de la date de l'ordonnance
		Espresso.onView(ViewMatchers.withText("OK"))
			.perform(ViewActions.click())

		Espresso.onView(withId(R.id.renuwalTextInfo)).check(
			matches(
				CoreMatchers.not(isDisplayed())
			)
		)

		Espresso.onView(ViewMatchers.withId(R.id.validateButton)).perform(ViewActions.scrollTo())
		Espresso.onView(ViewMatchers.withId(R.id.ManualActivity)).perform(
			GeneralSwipeAction(
				Swipe.FAST,
				GeneralLocation.CENTER,
				GeneralLocation.TOP_CENTER,
				Press.FINGER
			)
		)

		// Clique sur le bouton de validation
		Espresso.onView(withId(R.id.validateButton)).perform(ViewActions.click())

		// Vérifie si on n'a pas changé d'écran
		Espresso.onView(withId(R.id.validateButton)).check(matches(isDisplayed()))

	}
}