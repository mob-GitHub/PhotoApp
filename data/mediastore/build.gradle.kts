@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    val buildConfig: Map<String, Any> by project

    compileSdk = buildConfig["compileSdk"] as Int
    namespace = "si.f5.mob.mediastore"

    defaultConfig {
        minSdk = buildConfig["minSdk"] as Int
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = buildConfig["compatibility"] as JavaVersion
        targetCompatibility = buildConfig["compatibility"] as JavaVersion
    }
    kotlinOptions {
        jvmTarget = buildConfig["jvmTarget"] as String
    }
}

dependencies {
    implementation(project(":common"))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    testImplementation(project(":test"))
}