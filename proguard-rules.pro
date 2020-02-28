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
#-keep class org.spongycastle.** { *; }
#-dontwarn org.spongycastle.**


# proguard configuration for iText

-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.**

-keep class com.itextpdf.** { *; }

-keep class javax.xml.crypto.dsig.** { *; }
-dontwarn javax.xml.crypto.dsig.**

-keep class org.apache.jcp.xml.dsig.internal.dom.** { *; }
-dontwarn org.apache.jcp.xml.dsig.internal.dom.**

-keep class javax.xml.crypto.dom.** { *; }
-dontwarn javax.xml.crypto.dom.**

-keep class org.apache.xml.security.utils.** { *; }
-dontwarn org.apache.xml.security.utils.**

-keep class javax.xml.crypto.XMLStructure
-dontwarn javax.xml.crypto.XMLStructure


-keep public class android.support.v7.widget.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.v7.**
-keep class android.support.v7.* { *; }
-keep interface android.support.v7.* { *; }

-keep public  class com.polter.mobipolter.activities.database.**
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep interface com.polter.mobipolter.activities.database.**
-keepattributes Signature
#-keep class com.itextpdf.text.** { *; }
-dontwarn com.itextpdf.text.**
#
#-keep class org.spongycastle.** { *; }
#-dontwarn org.spongycastle.**
#
#-keep class com.itextpdf.** { *; }
#
#-keep class com.itextpdf.awt.PdfGraphics2D { *; }
#
#-keep class javax.xml.crypto.dsig.** { *; }
#-dontwarn javax.xml.crypto.dsig.**
#
#-keep class org.apache.jcp.xml.dsig.internal.dom.** { *; }
#-dontwarn org.apache.jcp.xml.dsig.internal.dom.**
#
#-keep class javax.xml.crypto.dom.** { *; }
#-dontwarn javax.xml.crypto.dom.**
#
#-keep class org.apache.xml.security.utils.** { *; }
#-dontwarn org.apache.xml.security.utils.**
#
#-keep class javax.xml.crypto.XMLStructure
#-dontwarn javax.xml.crypto.XMLStructure
#
-dontwarn com.itextpdf.**
-ignorewarnings
