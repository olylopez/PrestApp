plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.prestapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prestapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // mask

    //HILT
    implementation("com.google.dagger:hilt-android:2.51")
    implementation(libs.firebase.components)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.media3.exoplayer)
    //noinspection UseTomlInstead
    ksp("com.google.dagger:hilt-android-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //accompanist UI y navigation
    implementation("com.google.accompanist:accompanist-swiperefresh:0.23.1")
    implementation("com.google.accompanist:accompanist-navigation-material:0.23.1")


    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //animation NAVIGATION
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.navigation.compose)

// Dependencias para las serializaciones
    implementation (libs.kotlinx.serialization.json)


    // Dependencias de Jetpack Compose adicionales
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.runtime.rxjava2)


// Dependencias de Coil para cargar imágenes
    implementation (libs.coil.compose)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)


    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.material)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    //optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.foundation.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(libs.material3)

}