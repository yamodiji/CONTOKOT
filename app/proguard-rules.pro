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

# Keep Jetpack Compose classes
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room classes
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep data classes used with Room
-keep class com.speedDrawer.speed_drawer.data.model.** { *; }

# Keep ViewModel classes
-keep class com.speedDrawer.speed_drawer.presentation.viewmodel.** { *; }

# Keep Application class
-keep class com.speedDrawer.speed_drawer.SpeedDrawerApplication { *; } 