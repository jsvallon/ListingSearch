plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.android); alias(libs.plugins.kotlin.compose); alias(libs.plugins.hilt); alias(libs.plugins.ksp) }
android {
    namespace = "com.johnvallon.listingsearch"; compileSdk = 35
    defaultConfig { applicationId = "com.johnvallon.listingsearch"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0"; testInstrumentationRunner = "com.johnvallon.listingsearch.cucumber.CucumberHiltRunner"
        // Where the @CucumberOptions class lives (androidTest).
        testInstrumentationRunnerArguments["optionsAnnotationPackage"] = "com.johnvallon.listingsearch.cucumber"
    }
    buildTypes { release { isMinifyEnabled = false; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") } }
    buildFeatures { compose = true }
    // Test libraries (Cucumber, JUnit) ship duplicate license files; keep them out of the test APK.
    packaging { resources { excludes += setOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md", "META-INF/AL2.0", "META-INF/LGPL2.1") } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    implementation(project(":feature:search")); implementation(project(":common:data")); implementation(project(":common:network"))
    implementation(libs.androidx.core.ktx); implementation(libs.androidx.lifecycle.runtime.ktx); implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)); implementation(libs.androidx.compose.ui); implementation(libs.androidx.compose.material3); implementation(libs.androidx.compose.ui.tooling.preview); debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.hilt.android); ksp(libs.hilt.compiler)
    testImplementation(libs.junit); androidTestImplementation(libs.androidx.junit); androidTestImplementation(libs.androidx.espresso.core)

    // Cucumber scenarios (app/src/androidTest/assets/features) driving the Compose UI by test tag.
    androidTestImplementation(libs.cucumber.android)
    androidTestImplementation(libs.cucumber.picocontainer)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
