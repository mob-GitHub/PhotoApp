@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.android.application")
    id("dagger.hilt.android.plugin")
}

android {
    val buildConfig: Map<String, Any> by project

    compileSdk = buildConfig["compileSdk"] as Int
    namespace = "si.f5.mob.photoapp"

    defaultConfig {
        applicationId = "si.f5.mob.photoapp"
        minSdk = buildConfig["minSdk"] as Int
        targetSdk = buildConfig["compileSdk"] as Int
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.5"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-D"
        }
    }
    compileOptions {
        sourceCompatibility = buildConfig["compatibility"] as JavaVersion
        targetCompatibility = buildConfig["compatibility"] as JavaVersion
    }
    kotlinOptions {
        jvmTarget = buildConfig["jvmTarget"] as String
        freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":repository"))

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.accompanist.permissions)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // MLKit
    implementation(libs.mlkit.objectdetection)

    testImplementation(project(":test"))

    androidTestImplementation(project(":test"))
}