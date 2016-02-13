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

-keep class com.nagopy.android.aplin.entity.App {
	<fields>;
}


# Kotlin
-dontwarn kotlin.**

# RxJava / RxAndroid
# http://stackoverflow.com/questions/34315402/proguard-and-rxandroid-v1-1-0
-dontwarn sun.misc.Unsafe
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

# Timber
-dontwarn org.jetbrains.annotations.**

# Support libs
-dontwarn android.support.**

# Others
-dontobfuscate
-dontskipnonpubliclibraryclassmembers
