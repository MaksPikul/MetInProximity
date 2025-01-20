plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.metinproximityfront"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.metinproximityfront"
        minSdk = 24
        targetSdk = 35
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

object VERSION {
    const val NAVIGATION = "2.8.0"
    const val APPAUTH = "0.11.1"
    const val BROWSER = "1.8.0"
    const val OKHTTP = "4.12.0"
    const val GSON = "2.11.0"
}

dependencies {

    implementation ("net.openid:appauth:${VERSION.APPAUTH}")

    // Chrome Custom tabs for the login window
    implementation ("androidx.browser:browser:${VERSION.BROWSER}")

    // API requests and JSON
    implementation ("com.squareup.okhttp3:okhttp:${VERSION.OKHTTP}")
    implementation ("com.google.code.gson:gson:${VERSION.GSON}")

    implementation("androidx.security:security-crypto:1.0.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    //implementation(libs.androidx.security.crypto.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}