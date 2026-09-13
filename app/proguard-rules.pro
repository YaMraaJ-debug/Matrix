# ProGuard / R8 Rules for Ghost Downloader 3

# Keep Room database and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Keep Data Models
-keep class com.example.ghostdownloader.data.model.** { *; }
-keep class com.example.ghostdownloader.data.local.** { *; }

# Keep Kotlinx Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Jetpack Compose runtime
-keep class androidx.compose.runtime.** { *; }

# Keep Android Architecture Components & ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# Keep OkHttp & Networking
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
