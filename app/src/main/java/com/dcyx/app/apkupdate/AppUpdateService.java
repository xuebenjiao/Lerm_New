/**  
 * All rights Reserved, Designed By Android_Robot   
 * @Title  AppUpdateService.java   
 * @Package com.cloud.update   
 * @Description    TODO(用一句话描述该文件做什么)   
 * @author 方心贺     
 * @date   2014-7-30 下午3:09:00   
 * @version V1.0     
 */  
package com.dcyx.app.apkupdate;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dcyx.app.util.LogUtils;

/**   
 * @ClassName  AppUpdateService   
 * @Description TODO(这里用一句话描述这个类的作用)   
 * @author xbj
 *      
 */

public class AppUpdateService extends Service {

	/**   
	 * @Title onBind   
	 * @Description     
	 * @param arg0
	 * @return   
	 * @see Service#onBind(Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.i("AppUpdateService", "onStartCommand");
		int mStartType = -1;
		long now  = 0;
		long next = 0;
	
		if(intent != null){
			mStartType = intent.getIntExtra(AppUpdateManager.START_APP_UPDATE_KYE, -1);
			now = intent.getLongExtra(AppUpdateManager.TIMER_START_KEY, 0L);
			next = intent.getLongExtra(AppUpdateManager.TIMER_NEXT_KEY, 12*60*60*1000L);
		}
		if(mStartType != -1){
//			AppUpdateManager.start(this, mStartType, now, next);
		}else{
//			ToastUtils.showLongToast(R.string.app_update_start_failure);
		}
		return START_STICKY;
	}
}
