
plugins {
    alias(libs.plugins.metroll.android.feature)
    alias(libs.plugins.metroll.android.library.compose)
    alias(libs.plugins.metroll.android.library.jacoco)
}

android {
    namespace = "com.vidz.metroll.feature.auth"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.common.base)
    implementation(projects.common.theme)

    // Firebase Auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}
