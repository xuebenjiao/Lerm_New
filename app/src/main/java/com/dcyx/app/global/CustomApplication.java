package com.dcyx.app.global;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.dcyx.app.global.config.HttpParamsConstants;
import com.dcyx.app.mvp.model.entity.DepartmentInfoModel;
import com.dcyx.app.mvp.model.entity.UserInfo;
import com.dcyx.app.util.CommonUtils;
import com.dcyx.app.util.SharedPreUtils;
import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.squareup.leakcanary.LeakCanary;


import java.util.ArrayList;


/**
 * @name CustomApplication
 * @author xbj
 * @date 2017/8/10
 * @desc 自定义Application 存储一些全局变量
 */

public class CustomApplication extends Application {
    private static final String TAG = CustomApplication.class.getName();
    /*上下文 */
    public   Context context;
    /*用户信息*/
    public static UserInfo sUserInfo;
    /*定位信息*/
    public static String CityName;
    /*服务器地址*/
    public static String SERVER_IP = "http://202.109.79.92:52486/";//"http://202.109.79.92:52486/"; // 公司测试服务器
    /*服务器地址*/
    public static String SERVER_API_PORT = "8014";//"http://202.109.79.92:52486/"; // 公司测试服务器
    /*是否调式模式*/
    public static boolean IS_DEBUG = false;
    /*Lerm与Lerm-could的匹配标识*/
    public static boolean IS_LERM_CLOUND ;//默认是true--Clound   false--Lerm
    /*单例*/
    private static CustomApplication instance = null;
    /*当前数据库中的所有组别*/
    public static ArrayList<DepartmentInfoModel> sAllDepartmentInfoModels;
    @Override
    public void onCreate() {
    /*
   ThreadPolicy线程策略检测
    线程策略检测的内容有
    自定义的耗时调用 使用detectCustomSlowCalls()开启
    磁盘读取操作 使用detectDiskReads()开启
    磁盘写入操作 使用detectDiskWrites()开启
    网络操作 使用detectNetwork()开启
   VmPolicy虚拟机策略检测
    Activity泄露 使用detectActivityLeaks()开启
    未关闭的Closable对象泄露 使用detectLeakedClosableObjects()开启
    泄露的Sqlite对象 使用detectLeakedSqlLiteObjects()开启
    检测实例数量 使用setClassInstanceLimit()开启*/
        if (IS_DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
        ////判断是否在Analyzer进程里
        if(!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
        super.onCreate();

        BlockCanary.install(this,new BlockCanaryContext()).start();
        instance = this;
        context = getApplicationContext();
        IS_DEBUG = CommonUtils.isApkInDebug(context);//根据apk的测试版本设置值
        IS_LERM_CLOUND = (boolean) SharedPreUtils.get(context, HttpParamsConstants.SETTING_LERM_CLOUD,true);//默认是LermCloud
        CrashHandler crashHandler = new CrashHandler(); //CrashHandler.getInstance();
        crashHandler.init(context);

    }

    /**
     * 获取单例
     * @return
     */
    public static CustomApplication getInstance() {
        return instance;
    }
}
