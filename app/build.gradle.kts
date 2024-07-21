plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-kapt")
}

android {
    namespace = "com.rj.poc.coraltask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rj.poc.coraltask"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    val room_version = "2.6.1"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // CameraX core library using the camera2 implementation
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    // CameraX Lifecycle library
    implementation(libs.androidx.camera.lifecycle)
    // CameraX View class
    implementation(libs.androidx.camera.view)

    implementation ("androidx.room:room-runtime:2.5.0") // or the latest version
    kapt ("androidx.room:room-compiler:2.5.0") // or the latest version
    // Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:2.5.0") // or the latest version
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation ("com.github.bumptech.glide:glide:4.16.0")




}