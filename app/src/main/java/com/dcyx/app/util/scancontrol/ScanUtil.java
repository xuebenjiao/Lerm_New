package com.dcyx.app.util.scancontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.dcyx.app.global.Ibase.BaseScan;
import com.dcyx.app.util.LogUtils;
import com.dcyx.app.util.ToastUtils;

/**
* @name ScanUtil
* @author xbj
* @date 2017/9/14
* @desc  各种设备的扫描工类具
 */

public class ScanUtil implements BaseScan {
    /**
     * Activity对应的handler实例
     */
    public static Handler mHandler = null;
    /**
     * 肖邦(supoin SHT36)
     */
    public static final int XB = 1;
    /**
     * 扫描统一初始化实例
     */
    private static BaseScan scanManager;
    /**
     * Activity对应的上下文
     */
	private Context mContext;
	/**
	 * 肖邦 PDA对应的广播扫描类
	 */
	private static BroadCastReceiver_XB sBroadCastReceiver_xb;
	//BN2 非广播方式
	//
	public int deviceType; 
	private boolean isScan;
	private String  tag = "scan";
	private static final  String scanpower_filepath = "/proc/devicepower/rfidpower";
	private static final  String scanbarpower_filepath = "/proc/devicepower/scanpower";
	public ScanUtil(Context context) {
		mContext = context;
		getDeviceType();
		getScanManager();	
		Log.i(tag, "构造方法OK");
	}
	
	/**
	 * 获取设备型号
	 * @Title: getDeviceType
	 * @return void
	 */
	private void getDeviceType(){
		String release=android.os.Build.VERSION.RELEASE; // android系统版本号
		String model = android.os.Build.MODEL;// android设备型号
		LogUtils.e("ScanUtil", "getDeviceType release="+release);

		LogUtils.e("ScanUtil", "getDeviceType model="+model);
		if(model.equals("SHT36")||model.equals("SHT")||model.equals("PDA")){//5.1版本是SHT,之前是 SHT36
			deviceType= XB;
		}else{
			deviceType=-1;
			ToastUtils.showLongToast("未匹配到本机对应的扫描驱动");
		}
	}
	
	/**
	 * 初始化扫描驱动或者注册广播驱动
	 * scanManager方式初始化为非广播方式加载驱动
	 */
	private void getScanManager(){
		switch(deviceType){
		case XB:
			if(sBroadCastReceiver_xb==null){
				sBroadCastReceiver_xb = new BroadCastReceiver_XB();
			}
			registerReceiver(sBroadCastReceiver_xb, "com.android.server.scannerservice.broadcast");
			break;
		default:
			break;
		}
		Log.i(tag, "getScanManager OK");
	}
	/**
	 * @described activity初始化或者从后台进程显示时调用onResume()方法,初始化驱动
	 */
	public void init() {
	}

	/**
	 *  @described 物理按键扫描时activity中onKeydown()中调用进行扫码
	 */
	public void scan() {
	}

	/**
	 *  @described activity隐藏或者程序退出时在onPause()方法中调用,销毁驱动
	 */
	public void destory() {
		switch(deviceType){
		case XB:
			destoryReceiver(sBroadCastReceiver_xb);
			break;
	  default :
		  break;
		}
	}
	
	/**
	 * @Title: cancleScan 
	 * @Description:activity中onKeyup()方法中调用,取消扫描    
	 * @return void
	 */
	public void cancleScan(){
	}
	/**
	 * @described 获取Activity对应的handler实例
	 * @return a instance of Hander
	 */
	public static Handler getScanHandler(){
		return mHandler;
	}

	
	/**
	 * @described 注册广播接收器(本类统一方法)
	 * @author nff
	 * @param receiver 要注册的broadcast实例
	 * @param action 需要过滤的action,多个以逗号(,)隔开传参
	 */
    private void registerReceiver(BroadcastReceiver receiver,String... action) {
    	try {
    		IntentFilter filter = new IntentFilter();
    		for (String subAction: action) {
    			filter.addAction(subAction);
			}
    		if(receiver!=null){
    			mContext.registerReceiver(receiver, filter);
    		}
    	} catch (Exception e) {

    	}
    }
    /**
     * @described 销毁广播接收器(本类统一方法)
     * @author nff
     * @param receiver 需要销毁的广播实例
     */
    private void destoryReceiver(BroadcastReceiver receiver){
    	if(receiver!=null){
    		mContext.unregisterReceiver(receiver);
    		receiver=null;
    	}
	 }

	/**
	 * 设置界面对应的handler
	 * @param uHandler
	 */
	 public static  void setHandler(Handler uHandler){
		mHandler = uHandler;
	 }
    

}
