import java.util.Properties // <-- This import was missing.

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
    namespace = "com.quest1.demopos"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        applicationId = "com.quest1.demopos"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load credentials from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // Create BuildConfig fields from the loaded properties
        buildConfigField("String", "DITTO_APP_ID", "\"${localProperties.getProperty("DITTO_APP_ID")}\"")
        buildConfigField("String", "DITTO_TOKEN", "\"${localProperties.getProperty("DITTO_TOKEN")}\"")
        buildConfigField("String", "DITTO_AUTH_URL", "\"${localProperties.getProperty("DITTO_AUTH_URL")}\"")
        buildConfigField("String", "DITTO_WS_URL", "\"${localProperties.getProperty("DITTO_WS_URL")}\"")
        buildConfigField("String", "AUTH_SERVICE_BASE_URL", "\"${localProperties.getProperty("AUTH_SERVICE_BASE_URL")}\"")
        buildConfigField("String", "PAYMENT_SERVICE_BASE_URL", "\"${localProperties.getProperty("PAYMENT_SERVICE_BASE_URL")}\"")
        buildConfigField("long", "PayInitiatingDelay", "1500L")
        buildConfigField("long", "PayRedirectingDelay", "2000L")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // AndroidX & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Hilt Dependency Injection
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Ditto
    implementation(project(":ditto-wrapper"))
    implementation("live.ditto:ditto:4.8.1")
    implementation("live.ditto:ditto-tools-android:4.0.2")

    // Network (Retrofit & Moshi)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
//    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    // Other
    implementation("com.auth0.android:jwtdecode:2.0.2")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}