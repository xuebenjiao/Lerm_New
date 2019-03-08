package com.dcyx.app.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by xue on 2017/8/11.
 * Activity 设置工具类
 */

public class ActivityUtils {
    /**
     * 设置Activity全屏显示
     * 设置Activity全屏显示。
     * @param activity Activity引用
     * @param isFull true为全屏，false为非全屏
     */
    public static void setFullScreen(Activity activity, boolean isFull){
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (isFull) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     *  隐藏系统标题栏
     *  隐藏Activity的系统默认标题栏
     * @param activity Activity对象
     */
    public static void hideTitleBar(Activity activity){
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 隐藏软件输入法
     * @param activity
     */
    public static void hideSoftInput(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     *  使UI适配输入法
     * @param activity
     */
    public static void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    /**
     * 显示Toast消息。
     * 显示Toast消息，并保证运行在UI线程中
     * @param activity 当前界面
     * @param message 需要显示的信息
     */
    public static void toastShow(final Activity activity,final String message){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     *  设置Activity显示方向
     *  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT 竖屏
     *  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 横屏
     *  ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED 系统选择显示方向
     * @param activity Activity对象
     */
    public  void setScreenOrientation(Activity activity,int orientation){
        activity.setRequestedOrientation(orientation);
    }
}
