/**
 * All rights Reserved, Designed By Android_Robot   
 * @Title:  AppUpdateUtil.java
 * @Package com.cloud.update
 * @Description:    TODO(用一句话描述该文件做什么)
 * @author: fxh
 * @date:   2014-7-23 下午1:36:33   
 * @version V1.0
 */
package com.dcyx.app.apkupdate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dcyx.app.R;
import com.dcyx.app.apkupdate.http.OkGoUpdateHttpUtil;
import com.dcyx.app.apkupdate.util.HProgressDialogUtils;
import com.dcyx.app.global.CustomApplication;
import com.dcyx.app.util.CommonUtils;
import com.dcyx.app.util.FileUtils;
import com.dcyx.app.util.LogUtils;
import com.dcyx.app.util.NetworkUtils;
import com.dcyx.app.util.ToastUtils;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;

import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("HandlerLeak")
public class AppUpdateManager {
	private static final String TAG = "AppUpdateManager";
	// 供启动service时intent传递启动app更新模式的key值
	public static final String START_APP_UPDATE_KYE = "START_APP_UPDATE_KYE";
	// 供启动service时intent传递计时开始点的key值
	public static final String TIMER_START_KEY = "TIMER_START_KEY";
	// 供启动service时intent传递计时循环时间的key值
	public static final String TIMER_NEXT_KEY = "TIMER_NEXT_KEY";
	/**
	 * 程序启动时进行更新
	 */
	public static final int START_APP_UPDATE_LANCHER = 0;
	/**
	 * 设置定时器进行更新
	 */
	public static final int START_APP_UPDATE_TIMER = 1;
	/**
	 * 手动更新
	 */
	public static final int START_APP_UPDATE_USERSELF = 2;
	/**
	 * 版本号key值
	 */
	public static final String VERSION_CODE_KEY = "versionCode";
	/**
	 * 版本名称key值
	 */
	public static final String VERSION_NAME_KEY = "versionName";
	/**
	 * 版本大小key值
	 */
	public static final String APK_SIZE_KEY = "apkSize";
	/**
	 * 版本更新内容key值
	 */
	public static final String UPDATE_INFO_KEY = "updateInfo";
	// 服务器文件目录
	public static  String UPDATE_SERVER = "";
	// apk文件名称
	public static final String UPDATE_APKNAME = "Lerm.apk";
	// apk信息的文件名称
	public static final String UPDATE_VERJSON = "apk_info.txt";
	// 本地sdcard存储目录
	public static   String SAVE_FILE_PATH = CommonUtils.getFilePath("apkupdate");

//	private static AppUpdateManager mCreateDU;

	private ProgressDialog pBar;// 进度条对话框
	private String packgeName;
	private static int mStartType;
	private Map<String, String> netApkInfoMap;
	private Context mContext;
	private Timer timer = new Timer();
	private TimerTask task = new MyTimerTask();
	private Handler handler = new Myhandler();
	private String netApkInfo;
	private Dialog mdaDialog;
	// 工具类单例模式
	public  AppUpdateManager(Context context) {
		super();
	}
	/**
	 *
	 * @Title start
	 * @Description TODO(APP更新入口)
	 * @param context
	 *            当前上下文
	 * @param startType
	 *            启动更新的状态，0：启动程序时，1：登陆后定时更新，2：手动更新
	 * @param now
	 *            当启动状态为1时，第一次和执行的距离现在的时间间隔，单位毫秒
	 * @param next
	 *            当启动状态为1时，两个执行的时间间隔，单位毫秒
	 * @return void
	 * @author xbj
	 * @date 2017/11/6
	 */
	public  void start(Context context, int startType, long now, long next) {
		CustomApplication app =  (CustomApplication) context.getApplicationContext();

		/*if (mCreateDU == null) {
			mCreateDU = new AppUpdateManager(context);
		}*/
		if(!app.SERVER_IP.startsWith("http://")){
			app.SERVER_IP  = "http://"+app.SERVER_IP;
		}
		UPDATE_SERVER = app.SERVER_IP+"APKUPDATE/";
		mStartType = startType;
		mContext = context;

		switch (startType) {
			case START_APP_UPDATE_LANCHER:
				compareToVersion();
				break;
			case START_APP_UPDATE_TIMER:
				startTimer(now, next);
				break;
			case START_APP_UPDATE_USERSELF:
				mdaDialog = creatNotifyWaitDialog();
				mdaDialog.show();
				compareToVersion();
				break;
			default:
				break;
		}
	}

	/**
	 *
	 * @Title close
	 * @Description TODO(关闭定时器)
	 * @return void
	 * @author xbj
	 * @date 2017/11/6
	 */
	public  void close() {
		closeTimer();
	}

	/**
	 * (启动一个定时器)
	 */
	private void startTimer(long now, long next) {
		LogUtils.e(TAG, "start 1");
		if (timer == null) {
			timer = new Timer();
		}
		if (task == null) {
			task = new MyTimerTask();
		}
		//启动定时器
		timer.schedule(task, now, next);
	}

	/**
	 * 定时器关闭
	 */
	private void closeTimer() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.cancel();
		timer = null;
		task = null;
	}

	/**
	 * 获取服务器版本信息
	 */
	private void compareToVersion() {
		LogUtils.e(TAG, "compareToVersion 3");
		packgeName = mContext.getPackageName();
		new Thread() {
			public void run() {
				if (getServerVerCode()) {
					handler.sendEmptyMessage(mStartType);
				} else {
					if(mdaDialog!=null) {
						mdaDialog.dismiss();
						mdaDialog = null;
					}
					ToastUtils.showLongToastSafe(R.string.get_nothing_from_server);
				}
			}
		}.start();
	}

	/**
	 *获取服务器版本信息
	 * @return
	 */
	private boolean getServerVerCode() {
		netApkInfo = getContent(UPDATE_SERVER + UPDATE_VERJSON);
		/*if(CustomApplication.IS_DEBUG) {
			netApkInfo = AssetsUtils.getTextFromAssets(CustomApplication.getInstance().context, "apk_info.txt");
		}*/
		netApkInfoMap = AppUpdateUtil.getInstanse().JsonParse(netApkInfo);
		if (netApkInfoMap == null) {
			if(mdaDialog != null){
				mdaDialog.dismiss();
			}
			return false;
		}
		return true;
	}

	/**
	 * 获取服务端版本内容
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String getContent(String url) {
		StringBuilder sb = new StringBuilder();
		InputStream is = null;
		try {
			is = AppUpdateUtil.getInstanse().getInputStream(mContext, url);
			byte[] buf = new byte[1024];
			int ch = 0;
			while ((ch = is.read(buf)) > 0) {
				sb.append(new String(buf, 0, ch));
			}
			is.close();
		} catch (IOException e) {
			LogUtils.e(TAG, "getContent:" + e.toString());
			return null;
		}
		return sb.toString();
	}

	/**
	 * @name MyTimerTask
	 * @Description:TODO(定时执行的任务，加入了网络是否连接检测，网络无连接一分钟后重新检测)
	 * @author xbj
	 * @date 2017/11/6
	 */

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			LogUtils.i(TAG, "TimerTask");
			if(!NetworkUtils.isConnected()){
				ToastUtils.showLongToast(R.string.network_note);
			}else{
				compareToVersion();
			}
		}
	}

	/**
	 *
	 * @ClassName Myhandler
	 * @Description TODO(处理线程返回结果)
	 * @author xbj
	 * @date 2017/11/6
	 *
	 */
	class Myhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case START_APP_UPDATE_TIMER:
					new Thread() {
						public void run() {
							UserSelfHandle();
							//TimerHandle();
						}
					}.start();
					break;
				case START_APP_UPDATE_LANCHER:
					new Thread() {
						public void run() {
							UserSelfHandle();
						}
					}.start();

					break;
				case START_APP_UPDATE_USERSELF:
		/*			new Thread() {
						public void run() {*/
							UserSelfHandle();
				/*		}
					}.start();*/
					break;
				default:
					break;
			}
		}


	}
	/**
	 * @Title UserSelfHandle
	 * @Description TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 * @throws
	 * @author xbj
	 * @date 2017/11/6
	 */
	private void UserSelfHandle() {
		/*
		 * 1、判断已下载的apk是否是最新的，若不是最新的下载并提示安装 2、如果是最新的，判断与已安装版本是否一致，若不一致提示更新
		 */

		if(mdaDialog!=null){
			mdaDialog.dismiss();
			mdaDialog = null;
		}
		int netVersion = Integer.parseInt(netApkInfoMap
				.get(VERSION_CODE_KEY));
		int installVersion = AppUpdateUtil.getInstanse().getVerCode(
				mContext, packgeName);

		LogUtils.i(TAG, "UserSelfHandle:netVersion="+netVersion+",installVersion="+installVersion);//localVersion="+localVersion+",
		if(mdaDialog != null){
			mdaDialog.dismiss();
		}
		if(netVersion > installVersion){
			if(FileUtils.isFileExists(SAVE_FILE_PATH+UPDATE_APKNAME)){//判断是否已经下载
				InstallApp();
			}
			else {
				// 下载apk_info
				downFile(UPDATE_SERVER + UPDATE_VERJSON, SAVE_FILE_PATH,
						UPDATE_VERJSON, false);
				// 提示下载并安装
//				Looper.prepare();
				doNewVersionUpdate();
//				Looper.loop();
			}
		}else {
			if (mStartType == START_APP_UPDATE_USERSELF ) {//如果是app启动时的检测，则不予提示，如果是用户自己 点击了版本检测则提示
				//提示已是最新版本
//				Looper.prepare();
				notNewVersionShow();
//				Looper.loop();
			}
		}
	}
	/**
	 * @Title TimerHandle
	 * @Description TODO(定时更新处理)
	 * @return void
	 * @author xbj
	 * @date 2017/11/6
	 */
	public void TimerHandle() {
		// 判断运行程序版本与网络版本大小
		if (netApkInfoMap != null
				&& !netApkInfoMap.isEmpty()
				&& AppUpdateUtil.getInstanse().getVerCode(mContext, packgeName) < Integer
				.parseInt(netApkInfoMap.get(VERSION_CODE_KEY))) {
			// 下载apk,下载完成提示是否安装
			downFile(UPDATE_SERVER + UPDATE_APKNAME, SAVE_FILE_PATH,
					UPDATE_APKNAME, true);
			// 下载apk_info
			downFile(UPDATE_SERVER + UPDATE_VERJSON, SAVE_FILE_PATH,
					UPDATE_VERJSON, false);
			netApkInfoMap.clear();
		}
	}

	/**
	 *
	 * @param url 下载文件的路径
	 * @param filePath 保存下载文件的本地路径
	 * @param fileName 下载的文件名
	 * @param isApk
	 */
	private void downFile(final String url, final String filePath,
						  final String fileName, final boolean isApk) {
		new Thread() {
			public void run() {
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = AppUpdateUtil.getInstanse().getInputStream(mContext,
							url);
					fos = AppUpdateUtil.getInstanse().getOutputStream(filePath,
							fileName);
					byte[] buf = new byte[1024];
					int ch = 0;
					int count = 0;
					while ((ch = is.read(buf)) > 0) {
						count += ch;
						LogUtils.i(TAG, count + "");
						fos.write(buf, 0, ch);
					}
					fos.flush();
					fos.close();
					is.close();
					if (isApk) {
						Looper.prepare();
						creatNotifyInstallDialog();
						Looper.loop();
					}
				} catch (IOException e) {
					LogUtils.e(TAG, "downFile:" + e.toString());
				}
			}
		}.start();
	}

	/**
	 * @Title creatNotifyInstallDialog
	 * @Description TODO(提示是否进行更新对话框)
	 * @return void
	 * @author xbj
	 * @date 2017/11/6
	 */
	private void creatNotifyInstallDialog() {
		Builder builder = new Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示")
				.setMessage("检测到最新版本，是否更新？")
				.setNegativeButton("暂不更新", null)
				.setPositiveButton("马上更新",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								AppUpdateUtil.getInstanse().update(mContext,
										SAVE_FILE_PATH+UPDATE_APKNAME);
							}
						});
		AlertDialog dialog = builder.create();

		dialog.show();
	}
	/**
	 * @Title creatNotifyWaitDialog
	 * @Description TODO(提示是否进行更新对话框)
	 * @return void
	 * @author xbj
	 * @date 2017/11/6
	 */
	private Dialog creatNotifyWaitDialog() {
		Builder builder = new Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示")
				.setMessage("正在检测版本更新，请稍等...");
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	/**
	 * 提示已是最新版本
	 */
	private void notNewVersionShow() {
		StringBuffer sb = new StringBuffer();
		sb.append("已是最新版本,无需更新!");
		Dialog dialog = new Builder(mContext,AlertDialog.THEME_HOLO_LIGHT).setTitle("软件更新")
				.setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
							}
						}).create();// 创建
		dialog.show();
	}

	private void doNewVersionUpdate() {
		LogUtils.e(TAG, "doNewVersionUpdate");
		String verCode = AppUpdateUtil.getInstanse().getVerName(mContext,
				packgeName);
		String verName = AppUpdateUtil.getInstanse().getVerName(mContext,
				packgeName);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verCode);
		sb.append("新版本:");
		sb.append(netApkInfoMap.get(VERSION_NAME_KEY));
		sb.append("\n是否更新?");
		Dialog dialog = new Builder(mContext,AlertDialog.THEME_HOLO_LIGHT)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								dialog = null;
								new UpdateAppManager
										.Builder()
										//必须设置，当前Activity
										.setActivity((Activity) mContext)
										//必须设置，实现httpManager接口的对象
										.setHttpManager(new OkGoUpdateHttpUtil())
										//设置下载参数对象
										.setUpdateAppBean(new UpdateAppBean(UPDATE_SERVER + UPDATE_APKNAME,SAVE_FILE_PATH,new OkGoUpdateHttpUtil()))
										.build()
										//必须设置，更新地址
										.download(new DownloadService.DownloadCallback() {
											@Override
											public void onStart() {
												HProgressDialogUtils.showHorizontalProgressDialog((Activity) mContext, "下载进度", true);
											}
											/**
											 * 进度
											 * @param progress  进度 0.00 -1.00 ，总大小
											 * @param totalSize 总大小 单位B
											 */
											@Override
											public void onProgress(float progress, long totalSize) {
												HProgressDialogUtils.setProgress((long) (progress * totalSize));
											}
											/**
											 *
											 * @param total 总大小 单位B
											 */
											@Override
											public void setMax(long total) {
												HProgressDialogUtils.setMax(total);
											}
											@Override
											public boolean onFinish(File file) {
												HProgressDialogUtils.cancel();
												return true;
											}
											@Override
											public void onError(String msg) {
												HProgressDialogUtils.cancel();
											}
											@Override
											public boolean onInstallAppAndAppOnForeground(File file) {
												return false;
											}
										});
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int whichButton) {
							}
						}).create();// 创建
		dialog.show();
	}

	/**
	 * 根据url 下载最新 apk
	 * @param url
	 */
	private void downFile(final String url) {
		new Thread() {
			public void run() {
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = AppUpdateUtil.getInstanse().getInputStream(mContext,
							url);
					fos = AppUpdateUtil.getInstanse().getOutputStream(
							SAVE_FILE_PATH, UPDATE_APKNAME);
					byte[] buf = new byte[1024];
					int ch = -1;
					int count = 0;
					while ((ch = is.read(buf)) > 0) {
						fos.write(buf, 0, ch);
						count += ch;
						changePbar(count);
					}
					fos.flush();
					fos.close();
					InstallApp();
				} catch (ClientProtocolException e) {
					LogUtils.e(TAG, "downFile:" + e.toString());
				} catch (IOException e) {
					LogUtils.e(TAG, "downFile:" + e.toString());
				}
			}

		}.start();
//		}

	}

	private void changePbar(int count) {
//		pBar.setProgress(((int) count) / (1024 * 1024));
	}

	/**
	 * 安装更新app
	 */
	private void InstallApp() {
		handler.post(new Runnable() {
			public void run() {
				AppUpdateUtil.getInstanse().update(mContext, SAVE_FILE_PATH+UPDATE_APKNAME);
			}
		});
	}
}
