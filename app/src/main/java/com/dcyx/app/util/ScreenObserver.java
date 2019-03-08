package com.dcyx.app.util;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

/**
 * @author xue
 * @desc 设备屏幕观察者
 * @date 2017/11/16.
 */

public class ScreenObserver {
    /*上下文*/
    private Context mContext;
    /*监听广播*/
    private ScreenBroadcastReceiver mScreenReceiver;
    /*屏幕状态接口*/
    private ScreenStateListener mScreenStateListener;

    public ScreenObserver(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * 开始观察屏幕
     * @param listener
     */
    public void startObserver(ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener();
        getScreenState();
    }

    /**
     * 停止观察屏幕变化
     */
    public void shutdownObserver() {
        unregisterListener();
    }

    /**
     * 获取screen状态
     */
    @SuppressLint("NewApi")
    private void getScreenState() {
        if (mContext == null) {
            return;
        }
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 注册广播
     */
    private void registerListener() {
        if (mContext != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(mScreenReceiver, filter);
        }
    }

    /**
     * 销毁广播
     */
    private void unregisterListener() {
        if (mContext != null)
            mContext.unregisterReceiver(mScreenReceiver);
    }


    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mScreenStateListener.onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mScreenStateListener.onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                mScreenStateListener.onUserPresent();
            }
        }
    }

    /**
     * 屏幕状态接口
     */
    public interface ScreenStateListener {// 返回给调用者屏幕状态信息
        public void onScreenOn();

        public void onScreenOff();

        public void onUserPresent();
    }
}
