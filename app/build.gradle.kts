plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.swackles.jellyfin"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.swackles.jellyfin"
        minSdk = 26
        targetSdk = 36
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.runtime)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.layout)
    implementation(libs.systemuicontroller)
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.destinations.core)
    implementation(libs.bundles.ui.impl)
    implementation(libs.jellyfin)
    implementation(libs.bundles.exoplayer.impl)
    implementation(libs.bundles.room.impl)
    implementation(libs.coil)

    kapt(libs.dagger.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)

    ksp(libs.room.compiler)
    ksp(libs.destinations.ksp)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.manifest)

    implementation(project(":libs:auth"))
    implementation(project(":libs:jellyfin"))

    androidTestImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.testing.android)
}