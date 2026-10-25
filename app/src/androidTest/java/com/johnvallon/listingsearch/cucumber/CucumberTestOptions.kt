package com.johnvallon.listingsearch.cucumber

import io.cucumber.junit.CucumberOptions

/**
 * Tells Cucumber where the scenarios (assets/features) and the step definitions (this package) are.
 */
@CucumberOptions(
    features = ["features"],
    glue = ["com.johnvallon.listingsearch.cucumber"],
)
class CucumberTestOptions
