# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class ** { @com.squareup.moshi.FromJson *; @com.squareup.moshi.ToJson *; }
