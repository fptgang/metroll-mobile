plugins {
    alias(libs.plugins.metroll.android.feature)
    alias(libs.plugins.metroll.android.library.compose)
    alias(libs.plugins.metroll.android.library.jacoco)
}

android {
    namespace = "com.vidz.metroll.feature.account"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.common.base)
    implementation(projects.common.theme)
} 