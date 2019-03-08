/**
 * All rights Reserved, Designed By kyee 
 * @Title:  AppUpdateUtil.java
 * @Package com.cloud.update
 * @Description:    TODO(apk更新工具类)
 * @author: fxh
 * @date:   2014-7-23 下午1:36:33   
 * @version V1.0
 */
package com.dcyx.app.apkupdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;

import com.dcyx.app.util.LogUtils;
import com.dcyx.app.util.StringUtils;
import com.dcyx.app.util.ToastUtils;

/**
 *
 * @ClassName  AppUpdateUtil
 * @Description TODO(apk更新工具类)
 * @author xbj
 * @date 2017/11/4
 */

public class AppUpdateUtil {

	private static final String TAG = "AppUpdateUtil";
	private static AppUpdateUtil mCreateDU;
	// 工具类单例模式
	private AppUpdateUtil() {
		super();
	}
	public static AppUpdateUtil getInstanse(){
		if(mCreateDU == null){
			mCreateDU = new AppUpdateUtil();
		}
		return mCreateDU;
	}
	/**
	 *
	 * @Title update
	 * @Description TODO(启动系统安装程序，安装apk)
	 * @return void
	 * @author xbj
	 * @date 2017/11/4
	 */
	public void update(Context context,String apkUrl) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		                                           //Environment.getExternalStorageDirectory(),
		intent.setDataAndType(Uri.fromFile(new File(apkUrl)),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 *
	 * @Title getVerCode
	 * @Description TODO(获取当前运行程序的版本号)
	 * @param context
	 * @return int   版本号   
	 * @author xbj
	 * @date 2017/11/4
	 */
	public int getVerCode(Context context,String packgeName) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(packgeName, 0).versionCode;
		} catch (NameNotFoundException e) {
			LogUtils.e(TAG, "getVerCode:"+e.toString());
		}
		return verCode;
	}
	/**
	 *
	 * @Title getVerName
	 * @Description TODO(获取当前运行程序的版本名称)
	 * @param context
	 * @return String
	 * @author xbj
	 * @date 2017/11/4
	 */
	public String getVerName(Context context,String packgeName) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(packgeName, 0).versionName;
		} catch (NameNotFoundException e) {
			LogUtils.e(TAG, "getVerName:"+e.toString());
		}
		return verName;

	}
	/**
	 *
	 * @Title readFile
	 * @Description TODO(读取文件内容到内存)
	 * @param context  当前上下文
	 * @param filePath 文件在sdcard中的路径
	 * @return String  读取到的内容
	 */
	public String readFile(Context context,String filePath,String fileName){
		StringBuilder sb = new StringBuilder();
		try {
			//判断sdCard是否挂载
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				//获取sd卡目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				File file = new File(sdCardDir, filePath+fileName);
				boolean is = file.exists();
				if(!is){
					return null;
				}
				//获取输入流
				FileInputStream fis = new FileInputStream(file);
				//获取输入流包装类
//				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//				String line = null;
				//循环读取

				byte[] buf = new byte[8];
				int ch = 0;
				while ((ch = fis.read(buf)) > 0) {
					sb.append(new String(buf, 0, ch));
				}
				//关闭输入流

				fis.close();

			}
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "readFile:"+e.toString());
			return sb.toString();
		} catch (IOException e) {
			LogUtils.e(TAG, "readFile:"+e.toString());
			return sb.toString();
		}

		return sb.toString();
	}
	/**
	 *
	 * @Title writeFile
	 * @Description TODO(将制定字符串写入制定的文件)
	 * @param context    当前的上下文
	 * @param writeInfo  要写入的字符串
	 * @param filePath   文件路径
	 * @param fileName   文件名称
	 * @return void
	 * @author xbj
	 * @date 2017/11/4
	 */
	public void writeFile(Context context,String writeInfo,String filePath,String fileName){
		//判断sdCard是否挂载
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				//获取sd卡目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				File destDir = new File(
						sdCardDir,
						filePath);
				if (!destDir.exists()) {
					destDir.mkdirs();
				}

				File file = new File(
						sdCardDir,
						filePath+fileName);

				FileOutputStream fileOutputStream = new FileOutputStream(file);
				PrintStream ps = new PrintStream(fileOutputStream);
				ps.print(writeInfo);
				ps.close();
			}
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "writeFile:"+e.toString());
		}
	}
	/**
	 *
	 * @Title getInputStream
	 * @Description TODO(这里用一句话描述这个方法的作用)
	 * @param context
	 * @param url
	 * @throws IOException
	 * @return List<Object>
	 * @throws ClientProtocolException
	 * @author xbj
	 * @date 2017/11/4
	 */
	public InputStream getInputStream(Context context,String url) throws IOException{
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 5000); // 超时设置
		params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		response = client.execute(get);
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		return is;
	}
	/**
	 *
	 * @Title getOutputStream
	 * @Description TODO(这里用一句话描述这个方法的作用)
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 * @return FileOutputStream
	 * @author xbj
	 * @date 2017/11/4
	 */
	public FileOutputStream getOutputStream(String filePath,String fileName) throws IOException{

		FileOutputStream fileOutputStream = null;

		File destDir = new File(filePath);//Environment.getExternalStorageDirectory(),
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		File file = new File(filePath + fileName);//Environment.getExternalStorageDirectory(),
		if(file.exists()){
			file.createNewFile();
		}
		fileOutputStream = new FileOutputStream(file);
		return fileOutputStream;
	}
	/**
	 *
	 * @Title JsonParse
	 * @Description TODO(解析读到的JSON)
	 * @param json  需要解析的字符串
	 * @return Map<String,String>  存放解析出的的结果    
	 * @author xbj
	 * @date 2017/11/4
	 */
	public Map<String, String> JsonParse(String json){
		Map<String, String> map = null;
		if(StringUtils.isEmpty(json)){
			LogUtils.e(TAG, "JsonParse:入参不能为空！");
			return null;
		}
		try {
			JSONArray array = new JSONArray(json);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				map = new HashMap<String, String>();
				map.put(AppUpdateManager.VERSION_CODE_KEY, obj.getString(AppUpdateManager.VERSION_CODE_KEY));
				map.put(AppUpdateManager.VERSION_NAME_KEY, obj.getString(AppUpdateManager.VERSION_NAME_KEY));
				map.put(AppUpdateManager.APK_SIZE_KEY, obj.getString(AppUpdateManager.APK_SIZE_KEY));
				map.put(AppUpdateManager.UPDATE_INFO_KEY, obj.getString(AppUpdateManager.UPDATE_INFO_KEY));
			}
		} catch (JSONException e) {
			LogUtils.e(TAG, "JsonParse:"+e.toString());
			ToastUtils.showLongToast("版本信息不是JSON格式！");
			return null;
		}
		return map;
	}
	/**
	 *
	 * @Title fileIsExist
	 * @Description TODO(判断文件是否存在)
	 * @param filePath
	 * @param fileName
	 * @return File
	 * @author xbj
	 * @date 2017/11/4
	 */
	public File fileIsExist(String filePath,String fileName){
		File file = new File(Environment.getExternalStorageDirectory(),filePath+fileName);
		if(file.exists()){
			return file;
		}
		return null;
	}

	public void startServiceForUpdateApk(Context context,int type,long now,long next ){
		Intent intent =  new Intent(context,AppUpdateService.class);
		intent.putExtra(AppUpdateManager.START_APP_UPDATE_KYE, type);
		intent.putExtra(AppUpdateManager.TIMER_START_KEY, now);
		intent.putExtra(AppUpdateManager.TIMER_NEXT_KEY, next);
		context.startService(intent);
	}
}
