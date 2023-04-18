// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val buildConfig: Map<String, Any> by extra {
        mapOf(
            "compileSdk" to 33,
            "minSdk" to 26,
            "compatibility" to JavaVersion.VERSION_11,
            "jvmTarget" to "11"
        )
    }
    val versions: Map<String, String> by extra {
        mapOf(
            "kotlin" to "1.8.10",
            "compose" to "1.3.1",
            "hilt" to "2.45",
            "timber" to "4.7.1",
            "navigation" to "2.5.3",
            "coil" to "2.2.2"
        )
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions["kotlin"]}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${versions["hilt"]}")
    }
}
tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}