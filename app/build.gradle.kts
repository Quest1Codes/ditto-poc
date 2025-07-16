plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
    namespace = "com.example.quest1pos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quest1pos"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    kapt(libs.google.dagger.hilt.compiler)
    implementation(libs.androidx.appcompat) // Or the direct string: "androidx.appcompat:appcompat:1.6.1"
    implementation(libs.google.android.material) // Or: "com.google.android.material:material:1.11.0"
    implementation(libs.google.dagger.hilt.android) // Or: "com.google.dagger:hilt-android:2.48"
    implementation(libs.androidx.hilt.navigation.compose) // Or: "androidx.hilt:hilt-navigation-compose:1.1.0"dependencies {
    implementation(libs.google.dagger.hilt.android) // This is for the library, not the plugin
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    //Ditto Wrapper
    implementation(project(":ditto-wrapper"))

    //Ditto
    implementation("live.ditto:ditto:4.8.1")
}