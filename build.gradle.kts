// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android 앱 모듈에서 사용하는 기본 플러그인입니다.
    alias(libs.plugins.android.application) apply false

    // AGP 8.x 환경에서 Kotlin 소스 파일을 컴파일하기 위한 플러그인입니다.
    // 실제 적용은 app/build.gradle.kts에서 alias(libs.plugins.kotlin.android)로 합니다.
    alias(libs.plugins.kotlin.android) apply false

    // Hilt 의존성 주입을 사용하기 위한 Gradle Plugin입니다.
    // 실제 적용은 app/build.gradle.kts에서 alias(libs.plugins.hilt)로 합니다.
    alias(libs.plugins.hilt) apply false

    // KSP 기반 어노테이션 처리를 위한 플러그인입니다.
    // Hilt compiler를 kapt 대신 ksp로 실행하기 위해 사용합니다.
    alias(libs.plugins.ksp) apply false
}