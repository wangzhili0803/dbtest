#---------------------基础部分,基本不用改动---------------------------------------------
-verbose
-dontoptimize
-dontpreverify
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Signature,*Annotation*
-printmapping proguardMapping.txt
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-optimizations !method/marking/static,!method/removal/parameter,!code/removal/advanced
#-----------------------------------------------------------------------------------

#--------------------默认保留区域----------------------------------------------------
-keep public class android.os.Bundle
-keep public class android.content.Intent
-keep public class * extends android.app.Service
-keep public class * extends android.app.Activity
-keep public class * extends android.os.IInterface
-keep public class * extends android.app.Application
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# 如果你需要兼容6.0系统，请不要混淆org.apache.http.legacy.jar
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}

-keep class android.support.** { *; }
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}
-keep class javax.**
-keep class org.**{*;}
-keep class android.net.SSLCertificateSocketFactory{*;}
-keepattributes EnclosingMethod
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class **.R$* {
    *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
    void *(**On*Event);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}
## 注解支持
-keepclassmembers class *{
   void *(android.view.View);
}
#------------------------------------------------------------------------------------

#-----------------------------webview------------------------------------------------
-keep class android.webkit.**
-keep public class * extends android.webkit.WebChromeClient

-keepattributes *JavascriptInterface*

-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

-keepclasseswithmembernames class * extends android.webkit.WebChromeClient{*;}
#------------------------------------------------------------------------------------

#------------------------------------------

#-----------------腾讯sdk,bugly------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }
-keep class com.tencent.** {*;}
-keep public interface com.tencent.**
-keep public class * extends com.tencent.tinker.loader.app.ApplicationLifeCycle {*;}
#------------------------------------------

#----------------------------gilde------------------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#-----------------------------greendao------------------------------------------------
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

#-----------------------------RxJava RxAndroid RxLifeCycle2-----------------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.trello.rxlifecycle2.** { *; }
-keep interface com.trello.rxlifecycle2.** { *; }
-dontwarn com.trello.rxlifecycle2.**

#------------------fastjson------------------
-keep class com.alibaba.fastjson.**
-dontwarn com.alibaba.fastjson.**

# keep BmobSDK
-dontwarn cn.bmob.v3.**
-keep class cn.bmob.v3.** {*;}
# 确保JavaBean不被混淆-否则gson将无法将数据解析成具体对象
-keep class * extends cn.bmob.v3.BmobObject {
    *;
}
-keep class com.example.bmobexample.bean.BankCard{*;}
-keep class com.example.bmobexample.bean.GameScore{*;}
-keep class com.example.bmobexample.bean.MyUser{*;}
-keep class com.example.bmobexample.bean.Person{*;}
-keep class com.example.bmobexample.file.Movie{*;}
-keep class com.example.bmobexample.file.Song{*;}
-keep class com.example.bmobexample.relation.Post{*;}
-keep class com.example.bmobexample.relation.Comment{*;}

# keep okhttp3、okio
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *; }
-dontwarn okio.**

-dontwarn org.slf4j.**
-dontwarn org.reactivestreams.**

-keep class cn.leancloud.** { *;}
-keep class cn.leancloud.query.** { *;}

# 个推
-dontwarn com.getui.**
-keep class com.getui.**{*;}

#------------------EventBus ------------------
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#----------------其它-------------------------------------------------------------
-keep class com.jerry.baselib.common.bean.** { *; }
-keep class com.jerry.bobo.bean.** { *; }