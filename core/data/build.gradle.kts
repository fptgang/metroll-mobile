plugins {
    alias(libs.plugins.metroll.android.library)
    alias(libs.plugins.metroll.android.library.jacoco)
    alias(libs.plugins.metroll.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.vidz.metroll.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://52.163.66.235:8080/api/v1/\""
                )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.named("debug").get()
        }
    }
    buildFeatures {
        buildConfig = true
    }
}


dependencies {
//    implementation(projects.app)
    implementation(projects.core.domain)
//    implementation(projects.core.datastore)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.moshi)
    implementation(libs.converter.moshi)
    implementation(libs.converter.gson)
    implementation (libs.moshi.kotlin)
    ksp (libs.moshi.kotlin.codegen)
}
