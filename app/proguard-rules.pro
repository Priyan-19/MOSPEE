# MOSPEE ProGuard rules

# Keep Room entities and DAOs
-keep class com.mospee.data.local.entity.** { *; }
-keep class com.mospee.data.local.dao.** { *; }
-keep class com.mospee.domain.model.** { *; }

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# Google Play Services / Maps
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
