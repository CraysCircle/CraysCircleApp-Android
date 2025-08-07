# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep source file and line number information for debugging
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name
-renamesourcefileattribute SourceFile

# Keep Room database classes
-keep class me.vivekanand.crayscircle.data.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Compose related classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Wi-Fi Aware related classes
-keep class android.net.wifi.aware.** { *; }
-dontwarn android.net.wifi.aware.**

# Keep DataStore related classes
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# Keep JSON related classes (for PeerDevice serialization)
-keep class org.json.** { *; }
-dontwarn org.json.**

# Keep UUID generation
-keep class java.util.UUID { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Material Design 3 components
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.material3.**

# Keep Accompanist permissions
-keep class com.google.accompanist.permissions.** { *; }
-dontwarn com.google.accompanist.permissions.**

# Optimize string operations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Keep developer watermark information
-keep class me.vivekanand.crayscircle.** {
    @java.lang.Deprecated *;
}