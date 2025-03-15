plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.hackathon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hackathon"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    implementation(libs.volley)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.okhttp)
    implementation (libs.gson)
        implementation(libs.glide)
        implementation(libs.glide.compiler)

        implementation (libs.retrofit)
        implementation (libs.retrofit2.converter.gson)
       // annotationProcessor (libs.compiler.v4151)


//        implementation ('com.github.bumptech.glide:glide:4.12.0')
annotationProcessor (libs.glide.compiler)

        implementation(libs.retrofit2.converter.gson)
        implementation(libs.logging.interceptor)
    }





