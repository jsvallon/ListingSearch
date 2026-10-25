plugins {
    alias(libs.plugins.android.library); alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt); alias(libs.plugins.ksp)
}
android { namespace = "com.johnvallon.listingsearch.common.network"; compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    implementation(libs.hilt.android); ksp(libs.hilt.compiler)
    implementation(libs.retrofit.core); implementation(libs.retrofit.kotlinx)
    implementation(libs.okhttp); implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}
