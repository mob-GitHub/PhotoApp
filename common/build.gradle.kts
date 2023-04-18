plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    val buildConfig: Map<String, Any> by project

    compileSdk = buildConfig["compileSdk"] as Int
    namespace = "si.f5.mob.common"

    defaultConfig {
        minSdk = buildConfig["minSdk"] as Int
        targetSdk = buildConfig["compileSdk"] as Int

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
    val versions: Map<String, String> by project

    // Timber
    api("com.jakewharton.timber:timber:${versions["timber"]}")

    // Hilt
    implementation("com.google.dagger:hilt-android:${versions["hilt"]}")
    kapt("com.google.dagger:hilt-android-compiler:${versions["hilt"]}")

    // Kotlin Coroutine
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    testImplementation(project(":test"))
}