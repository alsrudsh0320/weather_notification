import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


// local.properties 파일에서 인증키를 로드합니다.
val properties = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}
val authKey = properties.getProperty("AUTH_KEY", "")

android {
    namespace = "com.alsrudsh0320.weather_notification"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alsrudsh0320.weather_notification"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 인증키 주입
        buildConfigField("String", "AUTH_KEY", "\"$authKey\"")
    }
    // buildConfigField 사용을 위한 허가
    buildFeatures {
        buildConfig = true
        viewBinding = true
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

    // 네트워크 통신 Retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
}