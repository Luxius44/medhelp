package com.example.medicine

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.example.activities.ScannerActivity
import org.junit.Rule
import org.junit.Test

class ScanDocumentTest {

	@get:Rule
	val activityRule = ActivityScenarioRule(ScannerActivity::class.java)

	@get:Rule
	val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
		Manifest.permission.CAMERA
	) // Autoriser la permission pour le bon déroulement des tests


	private val viewIds = listOf(
		R.id.addPhotoFromCameraButton,
		R.id.addPhotoFromGalleryButton
	)

	@Test
	fun testUIElementsDisplayed() {
		viewIds.forEach { viewId ->
			onView(withId(viewId))
				.check(matches(isDisplayed()))
		}
	}

	@Test
	fun clickingCameraButtonWithNoPermissions_showsPermissionRequest() {
		// Click on the addPhotoFromCameraButton
		onView(withId(R.id.addPhotoFromCameraButton))
			.perform(ViewActions.click())
	}

}