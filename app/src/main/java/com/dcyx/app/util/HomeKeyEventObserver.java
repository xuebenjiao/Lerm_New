package com.dcyx.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author xue
 * @desc home键观察者
 * @date 2017/11/16.
 */

public class HomeKeyEventObserver {
    /*上下文*/
    private Context mContext;
    /*监听广播*/
    private HomeKeyEventBroadCastReceiver mHomeKeyEventReceiver;
    /*屏幕状态接口*/
    private HomeKeyEventListener mHomeKeyEventListener;
    public HomeKeyEventObserver(Context context) {
        mContext = context;
        mHomeKeyEventReceiver = new HomeKeyEventObserver.HomeKeyEventBroadCastReceiver();
    }
    public HomeKeyEventObserver(Context context,HomeKeyEventListener listener) {
        this(context);
        mHomeKeyEventListener = listener;
        registerListener();
    }

    /**
     * 开始观察按压home键
     * @param listener
     */
    public void startObserver(HomeKeyEventListener listener) {
        mHomeKeyEventListener = listener;
        registerListener();
    }

    /**
     * 停止观察
     */
    public void shutdownObserver() {
        unregisterListener();
    }

        /**
         * 注册广播
         */
    private void registerListener() {
        if (mContext != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mContext.registerReceiver(mHomeKeyEventReceiver, filter);
        }
    }

    /**
     * 销毁广播
     */
    private void unregisterListener() {
        if (mContext != null)
            mContext.unregisterReceiver(mHomeKeyEventReceiver);
    }
    /**
     * Home键是一个系统的按钮，我们无法通过onKeyDown进行拦截，它是拦截不
     * 到的，我们只能得到他在什么时候被按下了。就是通过广播接收者,此广播也不需要任何权限
     */
    private class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
        private String TAG = "TagForHomeKeyEventBroadCastReceiver";
        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";
        static final String SYSTEM_RECENT_APPS = "recentapps";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null) {
                    //点击了Home键    ----      长按了Home键
                    if (reason.equals(SYSTEM_HOME_KEY)||reason.equals(SYSTEM_RECENT_APPS)) {
                        mHomeKeyEventListener.onPressHome();
                    }
                }
            }
        }
    }
    /**
     * home键回调接口
     */
    public interface HomeKeyEventListener {// 返回给调用者屏幕状态信息
        public void onPressHome();
    }
}
