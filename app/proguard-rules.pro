-dontskipnonpubliclibraryclasses
-optimizationpasses 3
-flattenpackagehierarchy
-keepattributes SourceFile,LineNumberTable
-printmapping map.txt

-keep public class com.google.android.gms.ads.** {
   public *;
}

-keep public class com.google.ads.** {
   public *;
}

-keep class android.support.v7.widget.SearchView { *; }

# Kotlin
-dontwarn kotlin.**

# Timber
-dontwarn org.jetbrains.annotations.**

# Support libs
-dontwarn android.support.**

# Others
-dontobfuscate
-dontskipnonpubliclibraryclassmembers
