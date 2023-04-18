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
        kotlinCompilerExtensionVersion = "1.4.2"
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
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    val versions: Map<String, String> by project

    implementation(project(":common"))
    implementation(project(":data:mediastore"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.accompanist:accompanist-permissions:0.29.1-alpha")

    // Hilt
    implementation("com.google.dagger:hilt-android:${versions["hilt"]}")
    kapt("com.google.dagger:hilt-android-compiler:${versions["hilt"]}")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:${versions["navigation"]}")
    implementation("androidx.navigation:navigation-fragment-ktx:${versions["navigation"]}")
    implementation("androidx.navigation:navigation-ui-ktx:${versions["navigation"]}")
    implementation("androidx.navigation:navigation-runtime-ktx:${versions["navigation"]}")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:${versions["navigation"]}")

    // Coil
    implementation("io.coil-kt:coil:${versions["coil"]}")
    implementation("io.coil-kt:coil-compose:${versions["coil"]}")

    testImplementation(project(":test"))

    androidTestImplementation(project(":test"))
}