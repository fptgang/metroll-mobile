# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Dagger Hilt - Keep all generated classes
-keep class dagger.** { *; }
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
-keep class **_HiltModules* { *; }
-keep class **_Factory* { *; }
-keep class **_MembersInjector* { *; }
-keep class **_ComponentTreeDeps { *; }
-keep class **_ComponentManager { *; }
-keep class **_Impl { *; }

# Keep all our data layer classes completely
-keep class com.vidz.data.** { *; }
-keep class com.vidz.domain.** { *; }
-keep class com.vidz.datastore.** { *; }

# Keep Retrofit API interfaces
-keep interface com.vidz.data.server.retrofit.api.** { *; }

# Specific classes that R8 was complaining about
-keep class com.vidz.data.di.AuthModule* { *; }
-keep class com.vidz.data.di.NetworkModule* { *; }
-keep class com.vidz.data.di.ApiModule* { *; }
-keep class com.vidz.data.di.RepositoryModule* { *; }
-keep class com.vidz.data.repository.** { *; }
-keep class com.vidz.data.server.retrofit.** { *; }

# Keep all classes with @Module annotation
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }

# Keep all classes with @Provides and @Binds annotations
-keepclassmembers class * {
    @dagger.Provides *;
    @dagger.Binds *;
}

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepattributes *Annotation*

# Keep all javax.inject annotations
-keep class javax.inject.** { *; }
-keepattributes *Annotation*

# Protobuf
-keep class com.vidz.datastore.proto.** { *; }
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep class * extends com.google.protobuf.GeneratedMessageLite$Builder { *; }
-keep class * extends com.google.protobuf.GeneratedMessageLite$MethodToInvoke { *; }
-keep class com.google.protobuf.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# More aggressive Protobuf rules to prevent field name obfuscation
-keepclassmembernames class com.vidz.datastore.proto.** {
    <fields>;
}

# Prevent obfuscation of all protobuf generated classes
-keepnames class com.vidz.datastore.proto.**
-keepnames class * extends com.google.protobuf.GeneratedMessageLite

# Specific rule to keep the userData_ field
-keepclassmembernames class com.vidz.datastore.proto.UserPreferences {
    com.vidz.datastore.proto.UserData userData_;
}

# kotlinx.serialization
-keep class kotlinx.serialization.** { *; }
-keep class kotlin.reflect.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keep class * implements kotlinx.serialization.KSerializer { *; }

# Keep all data classes and their fields
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep all classes in domain and data layers completely
-keep class com.vidz.domain.model.** { *; }
-keep class com.vidz.data.server.dto.** { *; }