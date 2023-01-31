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
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# keep all classes in all packages of MyLib module
-keep class com.example.mylib.*{
    public protected *;
}

-keep class com.example.bookshelf.*{
 *;
}

-keep class * extends com.example.bookshelf.bussiness.db.AppDatabase{}

-allowaccessmodification interface * extends androidx.room.RoomDatabase
-allowaccessmodification interface * extends com.example.bookshelf.bussiness.db.AppDatabase
-allowaccessmodification class * extends com.example.bookshelf.bussiness.db.AppDatabase
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**