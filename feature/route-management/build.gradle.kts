plugins {
    alias(libs.plugins.metroll.android.feature)
    alias(libs.plugins.metroll.android.library.compose)
    alias(libs.plugins.metroll.android.library.jacoco)
}

android {
    namespace = "com.vidz.metroll.feature.routemanagement"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.common.base)
    implementation(projects.common.theme)
    
    // MapBox dependencies
    implementation(libs.maps.android)
    implementation(libs.maps.compose)

} 