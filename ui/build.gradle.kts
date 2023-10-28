plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.javacat.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":common"))
    implementation(libs.bundles.viewmodel)
    implementation(libs.bundles.nav)
    implementation(libs.bundles.coroutines)

    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.material)
    implementation (libs.androidx.constraintlayout)
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.fragment.ktx)
    implementation (libs.glide)



}