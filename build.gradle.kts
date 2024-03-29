// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    @Suppress("UNUSED_VARIABLE")
    val buildConfig: Map<String, Any> by extra {
        mapOf(
            "compileSdk" to 33,
            "minSdk" to 26,
            "compatibility" to JavaVersion.VERSION_17,
            "jvmTarget" to "17"
        )
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.hilt.gradlePlugin)
    }
}
tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}