// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    @Suppress("UNUSED_VARIABLE")
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
            "compose" to "1.3.1",
            "hilt" to "2.45",
            "timber" to "4.7.1"
        )
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath(libs.hilt.gradlePlugin)
    }
}
tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}