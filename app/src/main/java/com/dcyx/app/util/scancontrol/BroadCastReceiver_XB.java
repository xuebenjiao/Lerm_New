package com.dcyx.app.util.scancontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.dcyx.app.global.config.HandlerWhat;
import com.dcyx.app.util.LogUtils;

/**
 * @name BroadCastReceiver_XB
 * @author xbj
 * @date 2017/9/14
 * @desc 肖邦PDA扫描工具类(广播方式)
 */

public class BroadCastReceiver_XB extends BroadcastReceiver {
	/*广播的ACTION标识*/
	public static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";
	/*扫描结果标识*/
	public static final String SCN_CUST_EX_SCODE = "scannerdata";
	/* 开始扫描的标识 */
	public static final String SCN_CUST_ACTION_START = "android.intent.action.SCANNER_BUTTON_DOWN";
	/* 释放扫描的标识 */
	public static final String SCN_CUST_ACTION_CANCEL = "android.intent.action.SCANNER_BUTTON_UP";

	private static final String SCANNER_CTRL_FILE = "/sys/devices/platform/scan_se955/se955_state";

	public static final String SCANNER_POWER = "com.android.server.scannerservice.onoff";
	/*肖邦扫描按键码*/
	public static final int SCAN_CODE= 249;
	/*肖邦扫描PTT按键码*/
	public static final int SCAN_PTT_CODE= 766;

	@Override
	public void onReceive(Context context, Intent intent) {
		Handler handler = ScanUtil.getScanHandler();
		if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)&& handler!=null) {
			try {
				String 	scanRes = intent.getStringExtra(SCN_CUST_EX_SCODE).toString();
				Message message = new Message();
				message.what = HandlerWhat.SCAN_RESULT;
				message.obj = scanRes;
				handler.sendMessage(message);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				LogUtils.e("in", e.toString());
			}
		}
	}
}
