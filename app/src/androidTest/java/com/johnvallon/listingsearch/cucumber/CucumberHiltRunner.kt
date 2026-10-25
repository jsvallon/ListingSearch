package com.johnvallon.listingsearch.cucumber

import android.app.Application
import android.content.Context
import dagger.hilt.android.testing.HiltTestApplication
import io.cucumber.android.runner.CucumberAndroidJUnitRunner

/**
 * Runs the Cucumber scenarios with Hilt's test application, so tests can swap the real
 * listing service for [FakeListingRepository].
 */
class CucumberHiltRunner : CucumberAndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
