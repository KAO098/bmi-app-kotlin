plugins {
    alias(libs.plugins.android.application) // ใช้ alias ของ Android application plugin
    alias(libs.plugins.kotlin.android) // ใช้ alias ของ Kotlin plugin
    alias(libs.plugins.kotlin.compose) // ใช้ alias ของ Kotlin Compose plugin
    id("com.google.gms.google-services") // Firebase plugin
}

android {
    namespace = "com.example.myapplication2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication2"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.location)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation(libs.androidx.material3.v120alpha04)
    implementation(libs.material3)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.material3.v100)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.activity.compose.v170)
    implementation(libs.androidx.foundation)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)

    implementation(libs.firebase.auth.v2212) // Firebase Authentication
    implementation(platform(libs.firebase.bom)) // Firebase BOM
    implementation(libs.firebase.analytics) // Firebase Analytics
}