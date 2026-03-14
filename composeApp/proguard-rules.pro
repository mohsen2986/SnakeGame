# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/mohsen/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard/index.html

# Keep Compose internal classes
#-keep class androidx.compose.runtime.** { *; }
#-keep class androidx.compose.ui.** { *; }
#-keep class androidx.compose.foundation.** { *; }
#-keep class androidx.compose.material3.** { *; }
#
## Keep Kotlin standard library
#-keep class kotlin.** { *; }
#-keep class kotlinx.** { *; }
#
## Keep JetBrains Compose
#-keep class org.jetbrains.compose.** { *; }
