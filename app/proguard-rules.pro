# R8 runs in full mode and these are the only rules: the default Android rule file is
# not applied (see app/build.gradle), since its broad keep rules for views and
# activities only matter for XML layouts and animators, which the app doesn't use.
# Obfuscated stack traces need build/outputs/mapping/release/mapping.txt to be read.

# Parcelable.CREATOR is looked up by name by the framework.
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Enum.valueOf(Class, String) calls values() reflectively (see EnumUtil).
-keepclassmembers enum * {
    public static **[] values();
}
