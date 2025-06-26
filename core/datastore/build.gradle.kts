plugins {
    alias(libs.plugins.metroll.android.library)
    alias(libs.plugins.metroll.android.library.jacoco)
    alias(libs.plugins.metroll.hilt)
    alias(libs.plugins.metroll.android.room)
    id("kotlinx-serialization")
    alias(libs.plugins.protobuf)
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
    implementation(libs.androidx.dataStore.core)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.12"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
