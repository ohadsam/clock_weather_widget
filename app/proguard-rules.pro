# kotlinx.serialization (used by :core WidgetSettings) keeps its generated serializers.
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.clockweather.core.**$$serializer { *; }
-keepclassmembers class com.clockweather.core.** { *** Companion; }
-keepclasseswithmembers class com.clockweather.core.** { kotlinx.serialization.KSerializer serializer(...); }
-dontwarn kotlinx.serialization.**
