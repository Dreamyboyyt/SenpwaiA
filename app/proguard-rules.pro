# ProGuard rules for SenpwaiA

# Keep custom classes
-keep class com.sleepy.senpwaia.models.** { *; }
-keep class com.sleepy.senpwaia.data.** { *; }
-keep class com.sleepy.senpwaia.services.** { *; }
-keep class com.sleepy.senpwaia.ui.viewmodels.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Kotlin classes and methods
-keep class kotlin.Metadata { *; }
-keep class kotlin.jvm.internal.** { *; }

# Keep Retrofit related classes
-keep class com.squareup.retrofit2.** { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep OkHttp related classes
-keep class com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**

# Keep Okio related classes
-keep class okio.** { *; }
-dontwarn okio.**

# Keep Gson related classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.squareup.moshi.** { *; }

# Keep JSoup related classes
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Keep Coil related classes
-keep class coil.** { *; }
-dontwarn coil.**

# Keep WorkManager related classes
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Keep DataStore related classes
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# Keep navigation related classes
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# Keep ViewModel related classes
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Keep Compose related classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Activities and Fragments for XML attributes
-keepclassmembers public class * extends android.app.Activity {
    public void *(android.view.View);
}

# Keep constants in case of internal linkage
-keepclassmembers class **.R$* {
    public static <fields>;
}

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep annotations
-keep @interface android.webkit.JavascriptInterface { *; }

# For view tag lookup on some limited cases
-keep class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep all classes in the application package
-keep class com.sleepy.senpwaia.** { *; }

# Remove logging statements in release build
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep the original parameter names for reflection
-keepattributes RuntimeVisibleParameterAnnotations