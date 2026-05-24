plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.coworker.jjikmuk"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.coworker.jjikmuk"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
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

        debug {
            isMinifyEnabled = false
        }
    }


    compileOptions {
        // Mac / Windows 양쪽에서 같은 기준으로 빌드되도록 Java 11 고정
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

hilt {
    enableAggregatingTask = true
}

kapt {
    correctErrorTypes = true
}

kotlin {
    // Java 컴파일 기준이 VERSION_11이므로 Kotlin JVM target도 11로 맞춥니다.
    // AGP 8.x + Kotlin Android 플러그인 조합에서 Kotlin이 JDK 21 기준으로 잡히면
    // Java target 11 / Kotlin target 21 불일치 오류가 발생할 수 있습니다.
    jvmToolchain(11)
}

dependencies {
    // Android 기본
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)

    // Lifecycle / ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt 런타임 라이브러리입니다.
    // @Inject, @AndroidEntryPoint, @HiltViewModel 같은 Hilt API를 사용할 때 필요합니다.
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

}