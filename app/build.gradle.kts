plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.xuhh.culturalknowledge"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.xuhh.culturalknowledge"
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
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //by viewModels
    implementation(libs.androidx.activity.ktx)

    //coroutine
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    //viewModelScope lifecycleScope
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //dataBinding
    implementation(libs.lifecycle.compiler)

    //BRV
    implementation(libs.brv)

    //lottie
    implementation(libs.airbnb.lottie)

    //shapeofview
    implementation(libs.shapeofview)

    //okhttp
    implementation(libs.okhttp)
    implementation(libs.logging)
    //okio
    implementation(libs.okio)

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.converter)
    implementation(libs.gson)

    //Glide
    implementation(libs.glide)
    //swiperefreshlayout
    implementation(libs.swiperefreshlayout)
}