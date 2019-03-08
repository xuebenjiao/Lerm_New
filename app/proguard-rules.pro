# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
#指定代码的压缩级别
    		-optimizationpasses 5

    		#包明不混合大小写
    		-dontusemixedcaseclassnames

    		#不去忽略非公共的库类
    		-dontskipnonpubliclibraryclasses

    		 #优化  不优化输入的类文件
    		-dontoptimize

    		 #不做预校验
    		-dontpreverify

    		 #混淆时是否记录日志
    		-verbose

    		 # 混淆时所采用的算法
    		-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    		#保护注解
    		-keepattributes *Annotation*
    		#内部类
            -keepattributes InnerClasses
    		# 保持哪些类不被混淆
    		-keep public class * extends android.app.Fragment
    		-keep public class * extends android.app.Activity
    		-keep public class * extends android.app.Application
    		-keep public class * extends android.app.Service
    		-keep public class * extends android.content.BroadcastReceiver
    		-keep public class * extends android.content.ContentProvider
    		-keep public class * extends android.app.backup.BackupAgentHelper
    		-keep public class * extends android.preference.Preference
    		-keep public class com.android.vending.licensing.ILicensingService
    		#如果有引用v4包可以添加下面这行
    		-keep public class * extends android.support.v4.app.Fragment
    		-keep public class * extends android.support.v4.app.FragmentActivity
    		-keep public class * extends android.support.v7.widget
    		-keep class org.xmlpull.v1.** {*;}
            -keep class android.util.Xml.** {*;}
            #Dialog 按钮监听事件（内部类 DialogInterface.OnClickListener）
            -keep class android.content.**{*;}
             #EvnetBus的混淆
            -keepattributes *Annotation*
            -keepclassmembers class * {
                @org.greenrobot.eventbus.Subscribe <methods>;
            }
            -keep enum org.greenrobot.eventbus.ThreadMode { *; }

            # Only required if you use AsyncExecutor
            -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
                <init>(java.lang.Throwable);
            }

    		#忽略警告
    		-ignorewarning
    		##记录生成的日志数据,gradle build时在本项目根目录输出##
    		#apk 包内所有 class 的内部结构
    		-dump class_files.txt
    		#未混淆的类和成员
    		-printseeds seeds.txt
    		#列出从 apk 中删除的代码
    		-printusage unused.txt
    		#混淆前后的映射
    		-printmapping mapping.txt
    		#描述apk内所有class文件的内部结构。
    		#-printdump dump.txt
    		#忽略警告
    		-dontwarn com.dcyx.app.**
    		-dontwarn okio.**
    		-dontwarn org.xmlpull.v1.**
    		#如果引用了v4或者v7包
    		-dontwarn android.support.**
    		#自己写的自定义控件不要混淆
            -keep public class * extends android.view.View { *; }
	        #okhttp 封装不混淆
            -keep class com.dcyx.app.util.okhttp.** {*;}
             #cache 封装不混淆
             -keep class com.dcyx.app.util.** {*;}
            #该包下的所有实体不要混淆
            -keep class com.dcyx.app.mvp.model.entity.** { *; }
            #百度地图
             -keep class com.baidu.**{*;}
             -keep class vi.com.**{*;}
             -dontwarn com.baidu.**
   		   -keepclasseswithmembernames class * {
    		    native <methods>;
    		}
    		# Keep names - Native method names. Keep all native class/method names.
            -keepclasseswithmembers,allowshrinking class * {
                native <methods>;
            }

    		#保持自定义控件类不被混淆
    		-keepclasseswithmembers class * {
    		    public <init>(android.content.Context, android.util.AttributeSet);
    		}

    		#保持自定义控件类不被混淆
    		-keepclassmembers class * extends android.app.Activity {
    		   public void *(android.view.View);
    		}

    		#保持自定义控件类不被混淆
    		-keepclassmembers class * extends android.support.v4.app.FragmentActivity {
    		   public void *(android.view.View);
    		}
    		#保持自定义控件类不被混淆
             -keepclassmembers class * extends android.support.v4.app.Fragment {
              public void *(android.view.View);
             }

    		#保持 Parcelable 不被混淆
    		-keep class * implements android.os.Parcelable {
    		  public static final android.os.Parcelable$Creator *;
    		}

    		#保持 Serializable 不被混淆
    		-keepnames class * implements java.io.Serializable

    		#保持 Serializable 不被混淆并且enum 类也不被混淆
    		-keepclassmembers class * implements java.io.Serializable {
    		    static final long serialVersionUID;
    		    private static final java.io.ObjectStreamField[] serialPersistentFields;
    		    !static !transient <fields>;
    		    !private <fields>;
    		    !private <methods>;
    		    private void writeObject(java.io.ObjectOutputStream);
    		    private void readObject(java.io.ObjectInputStream);
    		    java.lang.Object writeReplace();
    		    java.lang.Object readResolve();
    		}

    		#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
    		-keepclassmembers enum * {
    		  public static **[] values();
    		  public static ** valueOf(java.lang.String);
    		}

    		-keepclassmembers class * {
    		    public void *ButtonClicked(android.view.View);
    		}

    		#不混淆资源类
    		-keepclassmembers class **.R$* {
    		    public static <fields>;
    		}


-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}