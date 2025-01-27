import groovyjarjarantlr.build.ANTLR.compiler

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.barberbookingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.barberbookingapp"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(libs.github.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fresco)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
    implementation(libs.play.services.auth.v2070)
    implementation (libs.facebook.android.sdk)
    implementation (libs.fresco)
 




}