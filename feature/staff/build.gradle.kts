plugins {
    alias(libs.plugins.metroll.android.feature)
    alias(libs.plugins.metroll.android.library.compose)
    alias(libs.plugins.metroll.android.library.jacoco)
}

android {
    namespace = "com.vidz.metroll.feature.staff"
}

dependencies {
//    implementation(projects.core.data)
    implementation(projects.common.base)
    implementation(projects.common.theme)
    implementation(projects.feature.qrScanner)

} 