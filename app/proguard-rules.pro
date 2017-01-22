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

# Kotlin
-dontwarn kotlin.**

# Timber
-dontwarn org.jetbrains.annotations.**

# Support libs
-dontwarn android.support.**

# Others
-dontobfuscate
-dontskipnonpubliclibraryclassmembers
