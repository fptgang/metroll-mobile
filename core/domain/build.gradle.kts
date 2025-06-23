plugins {
    alias(libs.plugins.metroll.android.library)
    alias(libs.plugins.metroll.android.library.jacoco)
    alias(libs.plugins.metroll.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vidz.metroll.core.domain"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    
    // Firebase Auth - needed for hybrid use cases
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}
