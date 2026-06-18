plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "mx.utng.utngrunner"
    compileSdk = 35

    defaultConfig {
        applicationId = "mx.utng.utngrunner"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose for Wear OS
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.wear.compose:compose-navigation:1.3.1")

    // ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // DataStore (best score)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Health Services (FC sensor)
    implementation("androidx.health:health-services-client:1.1.0-alpha03")

    // Splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("junit:junit:4.13.2")
}