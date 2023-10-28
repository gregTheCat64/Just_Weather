import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id ("kotlin-kapt")
}

android {
    namespace = "ru.javacat.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {


    implementation(project(":domain"))
    implementation(project(":common"))
    implementation(libs.bundles.room)

    ksp(libs.room.compiler)

    implementation(libs.bundles.coroutines)
    implementation(libs.hilt)
    implementation(libs.bundles.retrofit)



}