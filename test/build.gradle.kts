@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    val buildConfig: Map<String, Any> by project

    compileSdk = buildConfig["compileSdk"] as Int
    namespace = "si.f5.mob.test"

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
    api("junit:junit:4.13.2")
    api("androidx.test.ext:junit:1.1.5")
    api("androidx.test.espresso:espresso-core:3.5.1")
}