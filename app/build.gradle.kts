import com.vidz.convention.MetrollBuildType

plugins {
    alias(libs.plugins.metroll.android.application)
    alias(libs.plugins.metroll.android.application.compose)
    alias(libs.plugins.metroll.android.application.flavors)
    alias(libs.plugins.metroll.android.application.jacoco)
    alias(libs.plugins.metroll.hilt)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
}

android {

    defaultConfig {
        applicationId = "com.vidz.app.metroll"
        versionCode = 8
        versionName = "0.1.2" // X.Y.Z; X = Major, Y = minor, Z = Patch level
//        buildConfigField(
//            "String",
//            "BASE_URL",
//            "\"http://40.87.80.54:8080/\""
//        )
        // Custom test runner to set up Hilt dependency graph
        multiDexEnabled = true //Add this line

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            applicationIdSuffix = MetrollBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = MetrollBuildType.RELEASE.applicationIdSuffix
            // Temporary fix for Protobuf obfuscation issue
            // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // isObfuscationEnabled = false

            // who clones the code to sign and run the release variant, use the debug signing key.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }



    buildFeatures{
        compose = true
        buildConfig = true

    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.vidz.metroll_mobile"
}

dependencies {
    // Feature modules
    implementation(projects.feature.home)
    implementation(projects.feature.auth)
    implementation(projects.feature.ticket)
    implementation(projects.feature.routeManagement)
    implementation(projects.feature.account)
    implementation(projects.feature.membership)
    implementation(projects.feature.staff)
    implementation(projects.feature.qrScanner)
    implementation(projects.feature.test)
    
    // Common modules
    implementation(projects.common.base)
    implementation(projects.common.theme)
    
    // Core modules
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
//
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)
//
    debugImplementation(libs.androidx.compose.ui.testManifest)
//    debugImplementation(projects.uiTestHiltManifest)
//
    kspTest(libs.hilt.compiler)
//
//    testImplementation(projects.core.dataTest)
//    testImplementation(projects.core.datastoreTest)
//    testImplementation(libs.hilt.android.testing)
//    testImplementation(projects.sync.syncTest)
//    testImplementation(libs.kotlin.test)
//
//    testDemoImplementation(libs.androidx.navigation.testing)
//    testDemoImplementation(libs.robolectric)
//    testDemoImplementation(libs.roborazzi)
//    testDemoImplementation(projects.core.screenshotTesting)
//    testDemoImplementation(projects.core.testing)
//
    androidTestImplementation(libs.androidx.test.runner)
//    androidTestImplementation(projects.core.dataTest)
//    androidTestImplementation(projects.core.datastoreTest)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
//    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)

//    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
