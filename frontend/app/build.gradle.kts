plugins {
    // Android 애플리케이션 모듈을 빌드하기 위한 기본 플러그인입니다.
    alias(libs.plugins.android.application)

    // AGP 8.x 환경에서 Kotlin 소스 파일을 컴파일하기 위한 플러그인입니다.
    // AGP 9.x에서는 별도 적용 시 충돌했지만, AGP 8.x로 낮추면 필요합니다.
    alias(libs.plugins.kotlin.android)

    // Hilt 의존성 주입을 앱 모듈에 실제로 적용합니다.
    // @AndroidEntryPoint, @HiltViewModel, @HiltAndroidApp 등을 사용할 수 있게 합니다.
    alias(libs.plugins.hilt)

    // Hilt compiler를 KSP 방식으로 실행하기 위한 플러그인입니다.
    // 기존 kapt 대신 KSP를 사용해 어노테이션 처리 코드를 생성합니다.
    alias(libs.plugins.ksp)
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
    implementation("androidx.fragment:fragment-ktx:1.8.5")
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

    // Hilt 어노테이션 처리기입니다.
    // @Inject, @HiltViewModel, @Module 등을 분석해서 필요한 DI 코드를 빌드 시점에 생성합니다.
    ksp(libs.hilt.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}