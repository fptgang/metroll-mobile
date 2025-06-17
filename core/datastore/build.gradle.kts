plugins {
    alias(libs.plugins.metroll.android.library)
    alias(libs.plugins.metroll.android.library.jacoco)
    alias(libs.plugins.metroll.hilt)
    alias(libs.plugins.metroll.android.room)
    id("kotlinx-serialization")
}

android {
    namespace = "com.vidz.metroll.core.datastore"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
