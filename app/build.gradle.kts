plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("com.google.gms.google-services")
}

android {
    namespace = "org.third.medicalapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.third.medicalapp"
        minSdk = 24
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
    viewBinding {
        enable = true
    }
}

dependencies {
    //기본
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //volley & gson & retrofit
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.firebaseui:firebase-ui-storage:8.0.0")
    // 네이버 지도 SDK
    implementation("com.naver.maps:map-sdk:3.17.0")
    implementation ("com.google.android.gms:play-services-location:20.0.0")
}
