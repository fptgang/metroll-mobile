plugins {
    alias(libs.plugins.metroll.android.feature)
    alias(libs.plugins.metroll.android.library.compose)
    alias(libs.plugins.metroll.android.library.jacoco)
}

android {
    namespace = "com.vidz.metroll.feature.qrscanner"
}

dependencies {
//    implementation(projects.core.data)
    implementation(projects.common.base)
    implementation(projects.common.theme)
    
    // CameraX dependencies
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    
    // QR code scanning dependencies
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

} 